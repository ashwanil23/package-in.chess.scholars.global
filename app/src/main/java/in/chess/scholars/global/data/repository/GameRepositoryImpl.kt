package `in`.chess.scholars.global.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
                // Explicitly use the correct GameState class to avoid ambiguity
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
                createdAt = System.currentTimeMillis()
                // betAmount should be a field in the GameState data class
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

            // Convert the Move data class to a Map to ensure compatibility with FieldValue.arrayUnion
            val moveMap = mapOf(
                "from" to mapOf("row" to move.from.row, "col" to move.from.col),
                "to" to mapOf("row" to move.to.row, "col" to move.to.col),
                "piece" to mapOf(
                    "type" to move.piece.type.name,
                    "color" to move.piece.color.name
                ),
                "capturedPiece" to move.capturedPiece?.let {
                    mapOf("type" to it.type.name, "color" to it.color.name)
                }
            )

            gameRef.update("moves", FieldValue.arrayUnion(moveMap)).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun endGame(gameId: String, result: GameResult): DataResult<Unit> {
        return try {
            val gameRef = firestore.collection("games").document(gameId)
            val updates = mutableMapOf<String, Any?>()
            updates["endedAt"] = System.currentTimeMillis()

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
}
