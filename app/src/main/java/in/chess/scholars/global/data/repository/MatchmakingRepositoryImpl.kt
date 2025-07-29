package `in`.chess.scholars.global.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import `in`.chess.scholars.global.domain.repository.MatchmakingRepository
import `in`.chess.scholars.global.domain.repository.MatchmakingState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

/**
 * Enhanced Firebase implementation of the MatchmakingRepository with real-time synchronization.
 * Handles the logic for finding and creating matches in Firestore with proper race condition handling.
 */
class MatchmakingRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : MatchmakingRepository {

    private var matchmakingListener: ListenerRegistration? = null
    private var currentUserId: String? = null
    private var activeSearchJob: Job? = null // Keep track of the active search coroutine
    private val RATING_TOLERANCE = 100
    private val MAX_WAIT_TIME_MS = 60000L // 1 minute max wait

    override fun findMatch(userRating: Int, betAmount: Float): Flow<MatchmakingState> = callbackFlow {
        currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            trySend(MatchmakingState.Error("User not authenticated."))
            close()
            return@callbackFlow
        }

        trySend(MatchmakingState.Searching("Rating: $userRating ± $RATING_TOLERANCE"))

        // Cancel any previous job before starting a new one
        activeSearchJob?.cancel()
        activeSearchJob = launch {
            try {
                // First, clean up any stale matchmaking entries for the current user
                cleanupStaleEntries(currentUserId!!)

                // Create or update user's matchmaking entry
                val userMatchmakingData = hashMapOf(
                    "userId" to currentUserId!!,
                    "rating" to userRating,
                    "betAmount" to betAmount,
                    "status" to "searching",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "ratingMin" to (userRating - RATING_TOLERANCE),
                    "ratingMax" to (userRating + RATING_TOLERANCE)
                )

                firestore.collection("matchmaking")
                    .document(currentUserId!!)
                    .set(userMatchmakingData)
                    .await()

                // Set up real-time listener for match status ONCE.
                // This is the single source of truth for when a match is made.
                matchmakingListener?.remove() // Remove any stale listeners
                matchmakingListener = firestore.collection("matchmaking")
                    .document(currentUserId!!)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(MatchmakingState.Error(error.message ?: "Listener failed"))
                            close(error) // Close the flow on listener error
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            val status = snapshot.getString("status")
                            if (status == "matched") {
                                val gameId = snapshot.getString("gameId")
                                val opponentId = snapshot.getString("opponentId")
                                if (gameId != null && opponentId != null) {
                                    trySend(MatchmakingState.MatchFound(gameId, opponentId))
                                    close() // Match found, close the flow.
                                }
                            }
                        }
                    }

                // Actively search for opponents in a loop
                while (isActive) {
                    val opponentFound = searchForOpponent(userRating, betAmount)
                    // If an opponent is found and a match attempt is made,
                    // we can break the loop and rely solely on the listener.
                    if (opponentFound) {
                        Log.d("Matchmaking", "Match attempt initiated. Halting active search and waiting for listener.")
                        break
                    }
                    delay(5000) // Poll for a new opponent every 5 seconds
                }

            } catch (e: Exception) {
                if (isActive) { // Only send error if the job wasn't cancelled
                    trySend(MatchmakingState.Error(e.message ?: "Failed to find a match"))
                    close(e)
                }
            }
        }

        awaitClose {
            activeSearchJob?.cancel()
            launch {
                cancelMatchmaking()
            }
        }
    }

    private suspend fun searchForOpponent(userRating: Int, betAmount: Float): Boolean {
        try {
            val potentialOpponents = firestore.collection("matchmaking")
                .whereEqualTo("status", "searching")
                .whereEqualTo("betAmount", betAmount.toInt())
                .get()
                .await()

            val suitableOpponent = potentialOpponents.documents
                .filter { it.id != currentUserId }
                .filter { doc ->
                    val opponentRating = doc.getLong("rating")?.toInt() ?: 0
                    abs(userRating - opponentRating) <= RATING_TOLERANCE
                }
                .minByOrNull { doc ->
                    abs(userRating - (doc.getLong("rating")?.toInt() ?: 0))
                }

            if (suitableOpponent != null) {
                attemptMatch(suitableOpponent.id, betAmount)
                return true // Indicate that a match attempt was made
            }
        } catch (e: Exception) {
            Log.w("Matchmaking", "Error during opponent search, will retry.", e)
        }
        return false // No suitable opponent found
    }

    private suspend fun attemptMatch(opponentId: String, betAmount: Float) {
        Log.d("Matchmaking", "Attempting to match with opponent: $opponentId")

        // *** FIX: Designate one player (with the smaller UID) as the game creator ***
        // This is the core fix to prevent the race condition.
        val iAmTheGameCreator = currentUserId!! < opponentId

        try {
            firestore.runTransaction { transaction ->
                val myDocRef = firestore.collection("matchmaking").document(currentUserId!!)
                val opponentDocRef = firestore.collection("matchmaking").document(opponentId)

                val myDoc = transaction.get(myDocRef)
                val opponentDoc = transaction.get(opponentDocRef)

                if (!myDoc.exists() || !opponentDoc.exists() ||
                    myDoc.getString("status") != "searching" ||
                    opponentDoc.getString("status") != "searching") {
                    // One of the players is no longer available, abort.
                    Log.w("Matchmaking", "Transaction aborted: One or both players are not 'searching'.")
                    return@runTransaction // Abort transaction
                }

                // Only the designated creator will create the game document.
                // The other player will get the gameId from the listener update.
                val gameId = if (iAmTheGameCreator) {
                    val newGameRef = firestore.collection("games").document()
                    val gameData = hashMapOf(
                        "gameId" to newGameRef.id,
                        "player1Id" to currentUserId!!,
                        "player2Id" to opponentId,
                        "player1Color" to "WHITE",
                        "player2Color" to "BLACK",
                        "currentPlayer" to "WHITE",
                        "betAmount" to betAmount,
                        "status" to "active",
                        "moves" to emptyList<Map<String, Any>>(),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "lastMoveAt" to FieldValue.serverTimestamp(),
                        "board" to initializeBoard()
                    )
                    transaction.set(newGameRef, gameData)
                    newGameRef.id // Return the new game ID
                } else {
                    // If I am not the creator, I will get the gameId from the opponent's document
                    // once the listener fires. For the transaction, we can use a placeholder or null.
                    null
                }

                Log.d("Matchmaking", "Transaction checks passed. Updating documents.")

                // Update both matchmaking entries atomically.
                transaction.update(myDocRef, mapOf(
                    "status" to "matched",
                    "gameId" to gameId, // Will be null if not the creator
                    "opponentId" to opponentId
                ))

                transaction.update(opponentDocRef, mapOf(
                    "status" to "matched",
                    "gameId" to gameId, // Will be null if not the creator
                    "opponentId" to currentUserId
                ))

            }.await()
            Log.i("Matchmaking", "Transaction to update status to 'matched' has completed.")
        } catch (e: Exception) {
            Log.e("Matchmaking", "Transaction FAILED with exception.", e)
        }
    }

    private suspend fun cleanupStaleEntries(userId: String) {
        try {
            val entry = firestore.collection("matchmaking").document(userId).get().await()
            if (entry.exists() && entry.getString("status") != "matched") {
                entry.reference.delete().await()
            }
        } catch (e: Exception) {
            Log.w("Matchmaking", "Error during cleanup, ignoring.", e)
        }
    }

    override suspend fun cancelMatchmaking() {
        matchmakingListener?.remove()
        matchmakingListener = null
        activeSearchJob?.cancel() // Cancel the search coroutine
        currentUserId?.let {
            try {
                val doc = firestore.collection("matchmaking").document(it).get().await()
                // Only delete if still searching, to avoid race conditions on successful match
                if (doc.exists() && doc.getString("status") == "searching") {
                    firestore.collection("matchmaking").document(it).delete().await()
                }
            } catch (e: Exception) {
                // Ignore exceptions on cancellation
            }
        }
    }

    private fun initializeBoard(): List<Map<String, Any>> {
        val board = mutableListOf<Map<String, Any>>()
        board.add(mapOf("type" to "ROOK", "color" to "WHITE", "position" to "a1"))
        board.add(mapOf("type" to "KNIGHT", "color" to "WHITE", "position" to "b1"))
        board.add(mapOf("type" to "BISHOP", "color" to "WHITE", "position" to "c1"))
        board.add(mapOf("type" to "QUEEN", "color" to "WHITE", "position" to "d1"))
        board.add(mapOf("type" to "KING", "color" to "WHITE", "position" to "e1"))
        board.add(mapOf("type" to "BISHOP", "color" to "WHITE", "position" to "f1"))
        board.add(mapOf("type" to "KNIGHT", "color" to "WHITE", "position" to "g1"))
        board.add(mapOf("type" to "ROOK", "color" to "WHITE", "position" to "h1"))
        for (col in 'a'..'h') {
            board.add(mapOf("type" to "PAWN", "color" to "WHITE", "position" to "${col}2"))
        }
        board.add(mapOf("type" to "ROOK", "color" to "BLACK", "position" to "a8"))
        board.add(mapOf("type" to "KNIGHT", "color" to "BLACK", "position" to "b8"))
        board.add(mapOf("type" to "BISHOP", "color" to "BLACK", "position" to "c8"))
        board.add(mapOf("type" to "QUEEN", "color" to "BLACK", "position" to "d8"))
        board.add(mapOf("type" to "KING", "color" to "BLACK", "position" to "e8"))
        board.add(mapOf("type" to "BISHOP", "color" to "BLACK", "position" to "f8"))
        board.add(mapOf("type" to "KNIGHT", "color" to "BLACK", "position" to "g8"))
        board.add(mapOf("type" to "ROOK", "color" to "BLACK", "position" to "h8"))
        for (col in 'a'..'h') {
            board.add(mapOf("type" to "PAWN", "color" to "BLACK", "position" to "${col}7"))
        }
        return board
    }
}
