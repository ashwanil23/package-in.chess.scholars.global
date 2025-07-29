package `in`.chess.scholars.global.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import `in`.chess.scholars.global.domain.model.ChatMessage
import `in`.chess.scholars.global.domain.model.GameResult
import `in`.chess.scholars.global.domain.model.GameState
import `in`.chess.scholars.global.domain.model.Move
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.GameRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GameRepositoryImpl(
    private val firestore: FirebaseFirestore
) : GameRepository {

    override fun getGameStream(gameId: String): Flow<DataResult<GameState>> = callbackFlow {
        val gameDocument = firestore.collection("games").document(gameId)

        val listener = gameDocument.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResult.Error(error))
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val gameState = snapshot.toObject(`in`.chess.scholars.global.domain.model.GameState::class.java)
                if (gameState != null) {
                    trySend(DataResult.Success(gameState.copy(gameId = snapshot.id)))
                } else {
                    trySend(DataResult.Error(Exception("Failed to parse game data.")))
                }
            } else {
                trySend(DataResult.Error(Exception("Game not found.")))
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun createGame(player1Id: String, player2Id: String, betAmount: Float): DataResult<String> {
        return try {
            val newGame = GameState(
                player1Id = player1Id,
                player2Id = player2Id,
                createdAt = Timestamp.now(),
                lastMoveAt = Timestamp.now() // Initialize lastMoveAt
            )
            val documentRef = firestore.collection("games").add(newGame).await()
            DataResult.Success(documentRef.id)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun updateGame(gameId: String, move: Move): DataResult<Unit> {
        return try {
            val gameRef = firestore.collection("games").document(gameId)

            // *** FIX: Manually convert the Move data class to a Map for Firestore ***
            // Firestore's arrayUnion cannot serialize complex Kotlin objects reliably.
            // This ensures the data is stored in a clean, predictable format.
            val moveData = mapOf(
                "from" to mapOf("row" to move.from.row, "col" to move.from.col),
                "to" to mapOf("row" to move.to.row, "col" to move.to.col),
                "piece" to mapOf(
                    "type" to move.piece.type.name,
                    "color" to move.piece.color.name,
                    "position" to mapOf("row" to move.piece.position.row, "col" to move.piece.position.col),
                    "hasMoved" to move.piece.hasMoved
                ),
                "capturedPiece" to move.capturedPiece?.let {
                    mapOf(
                        "type" to it.type.name,
                        "color" to it.color.name,
                        "position" to mapOf("row" to it.position.row, "col" to it.position.col),
                        "hasMoved" to it.hasMoved
                    )
                },
                "isPromotion" to move.isPromotion,
                "isCastling" to move.isCastling,
                "isEnPassant" to move.isEnPassant
            )

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(gameRef)
                val gameState = snapshot.toObject(GameState::class.java)
                    ?: throw Exception("Game not found")

                val lastMoveTime = gameState.lastMoveAt?.toDate()?.time ?: gameState.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                val now = System.currentTimeMillis()
                val timeElapsed = now - lastMoveTime

                val newTimeLeft = if (gameState.currentPlayer == "WHITE") {
                    gameState.player1TimeLeft - timeElapsed
                } else {
                    gameState.player2TimeLeft - timeElapsed
                }

                if (newTimeLeft <= 0) {
                    val winner = if (gameState.currentPlayer == "WHITE") "BLACK" else "WHITE"
                    transaction.update(gameRef, mapOf(
                        "status" to "finished",
                        "winner" to winner,
                        "endedAt" to FieldValue.serverTimestamp()
                    ))
                    return@runTransaction
                }

                val nextPlayer = if (gameState.currentPlayer == "WHITE") "BLACK" else "WHITE"

                val updates = mutableMapOf<String, Any>(
                    "moves" to FieldValue.arrayUnion(moveData), // Use the converted map here
                    "currentPlayer" to nextPlayer,
                    "lastMoveAt" to FieldValue.serverTimestamp()
                )
                if (gameState.currentPlayer == "WHITE") {
                    updates["player1TimeLeft"] = newTimeLeft
                } else {
                    updates["player2TimeLeft"] = newTimeLeft
                }

                transaction.update(gameRef, updates)
            }.await()

            DataResult.Success(Unit)
        } catch (e: Exception) {
            // Add logging to see transaction failures in Logcat
            Log.e("GameRepository", "Failed to update game: ${e.message}", e)
            DataResult.Error(e)
        }
    }


    override suspend fun endGame(gameId: String, result: GameResult): DataResult<Unit> {
        return try {
            val gameRef = firestore.collection("games").document(gameId)
            val updates = mutableMapOf<String, Any?>()
            updates["endedAt"] = FieldValue.serverTimestamp()

            when (result) {
                is GameResult.Win -> {
                    updates["status"] = "finished"
                    updates["winner"] = result.winner.name
                }
                is GameResult.Draw -> {
                    updates["status"] = "draw"
                    updates["drawReason"] = result.reason.name
                }
                is GameResult.InProgress -> {
                    // This case should ideally not be passed to endGame
                }
            }
            gameRef.update(updates).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getChatStream(gameId: String): Flow<DataResult<List<ChatMessage>>> = callbackFlow {
        val messagesCollection = firestore.collection("games").document(gameId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limitToLast(50)

        val listener = messagesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResult.Error(error))
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val messages = snapshot.toObjects(ChatMessage::class.java)
                trySend(DataResult.Success(messages))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(gameId: String, message: ChatMessage): DataResult<Unit> {
        return try {
            firestore.collection("games").document(gameId)
                .collection("messages")
                .add(message)
                .await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun updateDrawOffer(gameId: String, offeringPlayerId: String?): DataResult<Unit> {
        return try {
            firestore.collection("games").document(gameId)
                .update("drawOfferBy", offeringPlayerId)
                .await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
