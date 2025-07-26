//package `in`.chess.scholars.global.data.repository
//
//import com.google.firebase.auth.FirebaseAuth
//import `in`.chess.scholars.global.domain.repository.MatchmakingRepository
//import `in`.chess.scholars.global.domain.repository.MatchmakingState
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ListenerRegistration
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import kotlin.math.abs
//
///**
// * Firebase implementation of the MatchmakingRepository.
// * Handles the logic for finding and creating matches in Firestore.
// */
//class MatchmakingRepositoryImpl(
//    private val auth: FirebaseAuth,
//    private val firestore: FirebaseFirestore
//) : MatchmakingRepository {
//
//    private var matchmakingListener: ListenerRegistration? = null
//    private var currentUserId: String? = null
//
//    override fun findMatch(userRating: Int, betAmount: Float): Flow<MatchmakingState> = callbackFlow {
//        currentUserId = auth.currentUser?.uid
//        if (currentUserId == null) {
//            trySend(MatchmakingState.Error("User not authenticated."))
//            close()
//            return@callbackFlow
//        }
//
//        trySend(MatchmakingState.Searching("$userRating ± 100"))
//
//        // Launch a coroutine to handle the suspendable matchmaking logic
//        val matchmakingJob = launch {
//            try {
//                // --- Step 1: Try to find an existing opponent ---
//                val opponentsQuery = firestore.collection("matchmaking")
//                    .whereEqualTo("status", "searching")
//                    .whereEqualTo("betAmount", betAmount)
//                    .limit(20)
//
//                val opponentsSnapshot = opponentsQuery.get().await()
//
//                val suitableOpponent = opponentsSnapshot.documents.find { doc ->
//                    val opponentId = doc.getString("userId")
//                    val opponentRating = doc.getLong("rating")?.toInt() ?: 0
//                    val ratingDiff = abs(userRating - opponentRating)
//                    opponentId != null && opponentId != currentUserId && ratingDiff <= 100
//                }
//
//                // --- Step 2: If opponent found, try to claim them atomically ---
//                if (suitableOpponent != null) {
//                    val opponentId = suitableOpponent.id
//                    val gameId = createGameWithOpponent(currentUserId!!, opponentId, betAmount)
//
//                    if (gameId != null) {
//                        trySend(MatchmakingState.MatchFound(gameId, opponentId))
//                        close()
//                        return@launch // Exit coroutine, match found
//                    }
//                }
//
//                // --- Step 3: If no opponent, join the queue and listen for a match ---
//                joinQueueAndListen(currentUserId!!, userRating, betAmount) { state ->
//                    trySend(state)
//                    if (state is MatchmakingState.MatchFound || state is MatchmakingState.Error) {
//                        close()
//                    }
//                }
//            } catch (e: Exception) {
//                trySend(MatchmakingState.Error(e.message ?: "Failed to find a match"))
//                close(e)
//            }
//        }
//
//        // This block is called when the flow is cancelled by the consumer
//        awaitClose {
//            matchmakingJob.cancel()
//            // Launch a new coroutine in this scope to call the suspend function
//            launch {
//                cancelMatchmaking()
//            }
//        }
//    }
//
//    private suspend fun createGameWithOpponent(myId: String, opponentId: String, betAmount: Float): String? {
//        return try {
//            firestore.runTransaction { transaction ->
//                val opponentDocRef = firestore.collection("matchmaking").document(opponentId)
//                val opponentDoc = transaction.get(opponentDocRef)
//
//                if (!opponentDoc.exists() || opponentDoc.getString("status") != "searching") {
//                    return@runTransaction null // Opponent was taken
//                }
//
//                val gameDocRef = firestore.collection("games").document()
//                val gameData = mapOf(
//                    "player1" to myId,
//                    "player2" to opponentId,
//                    "betAmount" to betAmount,
//                    "status" to "in_progress",
//                    "createdAt" to System.currentTimeMillis(),
//                    "currentPlayer" to "WHITE",
//                    "moves" to emptyList<String>()
//                )
//                transaction.set(gameDocRef, gameData)
//
//                transaction.update(opponentDocRef, "status", "matched", "gameId", gameDocRef.id)
//
//                gameDocRef.id
//            }.await()
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    private suspend fun joinQueueAndListen(
//        myId: String,
//        rating: Int,
//        betAmount: Float,
//        onStateChange: (MatchmakingState) -> Unit
//    ) {
//        val matchmakingData = mapOf(
//            "userId" to myId,
//            "rating" to rating,
//            "betAmount" to betAmount,
//            "status" to "searching",
//            "timestamp" to System.currentTimeMillis()
//        )
//        firestore.collection("matchmaking").document(myId).set(matchmakingData).await()
//
//        matchmakingListener = firestore.collection("matchmaking").document(myId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    onStateChange(MatchmakingState.Error(error.message ?: "Listener failed"))
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//                    val status = snapshot.getString("status")
//                    if (status == "matched") {
//                        val gameId = snapshot.getString("gameId")
//                        val gameDoc = firestore.collection("games").document(gameId!!).get()
//                        gameDoc.addOnSuccessListener { gameSnapshot ->
//                            val player1 = gameSnapshot.getString("player1")
//                            val player2 = gameSnapshot.getString("player2")
//                            val opponentId = if (player1 == myId) player2 else player1
//                            if (opponentId != null) {
//                                onStateChange(MatchmakingState.MatchFound(gameId, opponentId))
//                            } else {
//                                onStateChange(MatchmakingState.Error("Could not determine opponent."))
//                            }
//                        }
//                    }
//                }
//            }
//    }
//
//    override suspend fun cancelMatchmaking() {
//        matchmakingListener?.remove()
//        matchmakingListener = null
//        currentUserId?.let {
//            try {
//                firestore.collection("matchmaking").document(it).delete().await()
//            } catch (e: Exception) {
//                // Ignore exceptions on cancellation
//            }
//        }
//    }
//}


























package `in`.chess.scholars.global.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Transaction
import `in`.chess.scholars.global.domain.repository.MatchmakingRepository
import `in`.chess.scholars.global.domain.repository.MatchmakingState
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

        val matchmakingJob = launch {
            try {
                // First, clean up any stale matchmaking entries
                cleanupStaleEntries()

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

                // Set up real-time listener for match status
                matchmakingListener = firestore.collection("matchmaking")
                    .document(currentUserId!!)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(MatchmakingState.Error(error.message ?: "Listener failed"))
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            val status = snapshot.getString("status")
                            when (status) {
                                "matched" -> {
                                    val gameId = snapshot.getString("gameId")
                                    val opponentId = snapshot.getString("opponentId")
                                    if (gameId != null && opponentId != null) {
                                        trySend(MatchmakingState.MatchFound(gameId, opponentId))
                                    }
                                }
                                "searching" -> {
                                    // Continue searching
                                    launch {
                                        searchForOpponent(userRating, betAmount)
                                    }
                                }
                                "error" -> {
                                    val errorMsg = snapshot.getString("errorMessage") ?: "Matchmaking failed"
                                    trySend(MatchmakingState.Error(errorMsg))
                                }
                            }
                        }
                    }

                // Actively search for opponents
                while (isActive) {
                    searchForOpponent(userRating, betAmount)
                    delay(5000) // Poll for a new opponent every 5 seconds
                }

            } catch (e: Exception) {
                trySend(MatchmakingState.Error(e.message ?: "Failed to find a match"))
                close(e)
            }
        }

        awaitClose {
            matchmakingJob.cancel()
            launch {
                cancelMatchmaking()
            }
        }
    }

    private suspend fun searchForOpponent(userRating: Int, betAmount: Float) {
        try {
            // Query for suitable opponents
            val potentialOpponents = firestore.collection("matchmaking")
                .whereEqualTo("status", "searching")
                .whereEqualTo("betAmount", betAmount.toInt())
                .get()
                .await()

            // Filter out self and find best match
            val suitableOpponent = potentialOpponents.documents
                .filter { it.id != currentUserId }
                .filter { doc ->
                    val opponentRating = doc.getLong("rating")?.toInt() ?: 0
                    val ratingDiff = abs(userRating - opponentRating)
                    ratingDiff <= RATING_TOLERANCE }
                .minByOrNull { doc ->
                    val opponentRating = doc.getLong("rating")?.toInt() ?: 0
                    abs(userRating - opponentRating)
                }

            if (suitableOpponent != null) {
                attemptMatch(suitableOpponent.id, betAmount)
            }
        } catch (e: Exception) {
            // Continue searching on error
        }
    }

    private suspend fun attemptMatch(opponentId: String, betAmount: Float) {
        Log.d("Matchmaking", "Attempting to match with opponent: $opponentId")
        try {
            val result = firestore.runTransaction { transaction ->
                val myDocRef = firestore.collection("matchmaking").document(currentUserId!!)
                val opponentDocRef = firestore.collection("matchmaking").document(opponentId)

                val myDoc = transaction.get(myDocRef)
                val opponentDoc = transaction.get(opponentDocRef)

                // --- Logging Step 1: Check if documents exist ---
                if (!myDoc.exists()) {
                    Log.e("Matchmaking", "My own document does not exist in transaction!")
                    return@runTransaction null
                }
                if (!opponentDoc.exists()) {
                    Log.e("Matchmaking", "Opponent's document does not exist in transaction!")
                    return@runTransaction null
                }

                val myStatus = myDoc.getString("status")
                val opponentStatus = opponentDoc.getString("status")

                // --- Logging Step 2: Check player statuses ---
                Log.d("Matchmaking", "My status: '$myStatus', Opponent status: '$opponentStatus'")

                // Verify both are still searching
                if (myStatus != "searching" || opponentStatus != "searching") {
                    Log.w("Matchmaking", "Aborting transaction: One or both players are not 'searching'.")
                    return@runTransaction null
                }

                // --- Logging Step 3: If checks pass, create the game ---
                Log.d("Matchmaking", "Checks passed. Creating game and updating documents.")

                val gameId = firestore.collection("games").document().id
                val gameData = hashMapOf(
                    "gameId" to gameId,
                    "player1Id" to currentUserId!!,
                    "player2Id" to opponentId,
                    "player1Color" to "WHITE",
                    "player2Color" to "BLACK",
                    "currentPlayer" to "WHITE",
                    "betAmount" to betAmount.toInt(), // Make sure this is still an Int
                    "status" to "active",
                    "moves" to emptyList<Map<String, Any>>(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "lastMoveAt" to FieldValue.serverTimestamp(),
                    "board" to initializeBoard() // Assuming this function exists
                )

                transaction.set(firestore.collection("games").document(gameId), gameData)

                // Update both matchmaking entries
                transaction.update(myDocRef, mapOf(
                    "status" to "matched",
                    "gameId" to gameId,
                    "opponentId" to opponentId
                ))

                transaction.update(opponentDocRef, mapOf(
                    "status" to "matched",
                    "gameId" to gameId,
                    "opponentId" to currentUserId
                ))

                return@runTransaction gameId
            }.await()

            if (result != null) {
                Log.i("Matchmaking", "Transaction SUCCEEDED. Game created: $result")
            } else {
                Log.w("Matchmaking", "Transaction returned null. Match was likely already taken.")
            }

        } catch (e: Exception) {
            // --- Logging Step 4: Catch any unexpected transaction errors ---
            Log.e("Matchmaking", "Transaction FAILED with exception.", e)
        }
    }

    private suspend fun cleanupStaleEntries() {
        try {
            val staleTime = System.currentTimeMillis() - MAX_WAIT_TIME_MS
            val staleEntries = firestore.collection("matchmaking")
                .whereEqualTo("status", "searching")
                .whereLessThan("timestamp", staleTime)
                .get()
                .await()

            staleEntries.documents.forEach { doc ->
                doc.reference.delete()
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }

    override suspend fun cancelMatchmaking() {
        matchmakingListener?.remove()
        matchmakingListener = null
        currentUserId?.let {
            try {
                firestore.collection("matchmaking").document(it).delete().await()
            } catch (e: Exception) {
                // Ignore exceptions on cancellation
            }
        }
    }

    private fun initializeBoard(): List<Map<String, Any>> {
        // Initialize the board state as a list of piece positions
        val board = mutableListOf<Map<String, Any>>()

        // Add white pieces
        board.add(mapOf("type" to "ROOK", "color" to "WHITE", "position" to "a1"))
        board.add(mapOf("type" to "KNIGHT", "color" to "WHITE", "position" to "b1"))
        board.add(mapOf("type" to "BISHOP", "color" to "WHITE", "position" to "c1"))
        board.add(mapOf("type" to "QUEEN", "color" to "WHITE", "position" to "d1"))
        board.add(mapOf("type" to "KING", "color" to "WHITE", "position" to "e1"))
        board.add(mapOf("type" to "BISHOP", "color" to "WHITE", "position" to "f1"))
        board.add(mapOf("type" to "KNIGHT", "color" to "WHITE", "position" to "g1"))
        board.add(mapOf("type" to "ROOK", "color" to "WHITE", "position" to "h1"))

        // White pawns
        for (col in 'a'..'h') {
            board.add(mapOf("type" to "PAWN", "color" to "WHITE", "position" to "${col}2"))
        }

        // Black pieces
        board.add(mapOf("type" to "ROOK", "color" to "BLACK", "position" to "a8"))
        board.add(mapOf("type" to "KNIGHT", "color" to "BLACK", "position" to "b8"))
        board.add(mapOf("type" to "BISHOP", "color" to "BLACK", "position" to "c8"))
        board.add(mapOf("type" to "QUEEN", "color" to "BLACK", "position" to "d8"))
        board.add(mapOf("type" to "KING", "color" to "BLACK", "position" to "e8"))
        board.add(mapOf("type" to "BISHOP", "color" to "BLACK", "position" to "f8"))
        board.add(mapOf("type" to "KNIGHT", "color" to "BLACK", "position" to "g8"))
        board.add(mapOf("type" to "ROOK", "color" to "BLACK", "position" to "h8"))

        // Black pawns
        for (col in 'a'..'h') {
            board.add(mapOf("type" to "PAWN", "color" to "BLACK", "position" to "${col}7"))
        }

        return board
    }
}