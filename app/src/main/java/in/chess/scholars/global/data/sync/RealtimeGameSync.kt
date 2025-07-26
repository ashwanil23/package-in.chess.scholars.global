package `in`.chess.scholars.global.data.sync

import com.google.firebase.firestore.*
import `in`.chess.scholars.global.data.cache.LocalCacheManager
import `in`.chess.scholars.global.domain.model.*
import `in`.chess.scholars.global.engine.ChessGameEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for real-time game synchronization with optimistic updates
 * and conflict resolution for 120 FPS smooth gameplay
 */
class RealtimeGameSync(
    private val firestore: FirebaseFirestore,
    private val cacheManager: LocalCacheManager,
    private val currentUserId: String
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gameListeners = ConcurrentHashMap<String, ListenerRegistration>()
    private val pendingMoves = ConcurrentHashMap<String, MutableList<PendingMove>>()
    private val gameEngines = ConcurrentHashMap<String, ChessGameEngine>()

    data class PendingMove(
        val move: Move,
        val timestamp: Long,
        val moveId: String,
        var status: MoveStatus = MoveStatus.PENDING
    )

    enum class MoveStatus {
        PENDING,
        CONFIRMED,
        REJECTED
    }

    /**
     * Start syncing a game with optimistic updates
     */
    fun startGameSync(gameId: String): Flow<GameSyncState> = callbackFlow {
        val engine = ChessGameEngine()
        gameEngines[gameId] = engine
        pendingMoves[gameId] = mutableListOf()

        // Load cached state immediately
        scope.launch {
            cacheManager.getCachedGameState(gameId)?.let { cachedState ->
                // Apply cached moves to engine
                cachedState.moves.forEach { move ->
                    engine.makeMove(move.from, move.to)
                }
                trySend(GameSyncState.Updated(engine.board.value, engine.currentPlayer.value))
            }
        }

        // Set up real-time listener
        val gameRef = firestore.collection("games").document(gameId)
        val listener = gameRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(GameSyncState.Error(error.message ?: "Sync failed"))
                return@addSnapshotListener
            }

            snapshot?.let { doc ->
                if (doc.exists()) {
                    scope.launch {
                        processGameUpdate(gameId, doc, engine) { state ->
                            trySend(state)
                        }
                    }
                }
            }
        }

        gameListeners[gameId] = listener

        // Periodic sync to handle any missed updates
        val syncJob = scope.launch {
            while (isActive) {
                delay(5000) // Sync every 5 seconds
                syncPendingMoves(gameId)
            }
        }

        awaitClose {
            syncJob.cancel()
            stopGameSync(gameId)
        }
    }

    /**
     * Make a move with optimistic update
     */
    suspend fun makeMove(
        gameId: String,
        move: Move,
        onResult: (Boolean) -> Unit
    ) {
        val engine = gameEngines[gameId] ?: return onResult(false)
        val moveId = "${System.currentTimeMillis()}_${currentUserId}"

        // Optimistic update - apply move locally immediately
        if (engine.makeMove(move.from, move.to)) {
            // Add to pending moves
            val pendingMove = PendingMove(move, System.currentTimeMillis(), moveId)
            pendingMoves[gameId]?.add(pendingMove)

            // Update UI immediately
            onResult(true)

            // Send to server
            scope.launch {
                try {
                    val gameRef = firestore.collection("games").document(gameId)

                    // Use transaction to ensure atomic update
                    firestore.runTransaction { transaction ->
                        val gameDoc = transaction.get(gameRef)
                        val currentMoves = gameDoc.get("moves") as? List<Map<String, Any>> ?: emptyList()

                        // Verify it's still our turn
                        val currentPlayer = gameDoc.getString("currentPlayer")
                        val player1Id = gameDoc.getString("player1Id")
                        val player2Id = gameDoc.getString("player2Id")

                        val isMyTurn = (currentPlayer == "WHITE" && player1Id == currentUserId) ||
                                (currentPlayer == "BLACK" && player2Id == currentUserId)

                        if (!isMyTurn) {
                            throw Exception("Not your turn")
                        }

                        // Add move to the moves list
                        val moveData = mapOf(
                            "moveId" to moveId,
                            "from" to mapOf("row" to move.from.row, "col" to move.from.col),
                            "to" to mapOf("row" to move.to.row, "col" to move.to.col),
                            "piece" to mapOf(
                                "type" to move.piece.type.name,
                                "color" to move.piece.color.name
                            ),
                            "timestamp" to FieldValue.serverTimestamp(),
                            "playerId" to currentUserId
                        )

                        val updatedMoves = currentMoves + moveData

                        // Update game state
                        transaction.update(gameRef, mapOf(
                            "moves" to updatedMoves,
                            "currentPlayer" to if (currentPlayer == "WHITE") "BLACK" else "WHITE",
                            "lastMoveAt" to FieldValue.serverTimestamp()
                        ))
                    }.await()

                    // Mark move as confirmed
                    pendingMoves[gameId]?.find { it.moveId == moveId }?.status = MoveStatus.CONFIRMED

                } catch (e: Exception) {
                    // Revert optimistic update
                    pendingMoves[gameId]?.find { it.moveId == moveId }?.status = MoveStatus.REJECTED
                    // You'll need to implement move reversion in ChessGameEngine
                    onResult(false)
                }
            }
        } else {
            onResult(false)
        }
    }

    /**
     * Process game updates from Firestore
     */
    private suspend fun processGameUpdate(
        gameId: String,
        document: DocumentSnapshot,
        engine: ChessGameEngine,
        onStateChange: (GameSyncState) -> Unit
    ) {
        try {
            val moves = document.get("moves") as? List<Map<String, Any>> ?: emptyList()
            val currentPlayer = document.getString("currentPlayer") ?: "WHITE"
            val status = document.getString("status") ?: "active"

            // Rebuild game state from moves
            engine.resetBoard()
            moves.forEach { moveData ->
                val from = moveData["from"] as? Map<String, Any>
                val to = moveData["to"] as? Map<String, Any>

                if (from != null && to != null) {
                    val fromPos = Position(
                        (from["row"] as Long).toInt(),
                        (from["col"] as Long).toInt()
                    )
                    val toPos = Position(
                        (to["row"] as Long).toInt(),
                        (to["col"] as Long).toInt()
                    )

                    engine.makeMove(fromPos, toPos)
                }
            }

            // Cache the updated state
            val gameState = GameState(
                gameId = gameId,
                player1Id = document.getString("player1Id") ?: "",
                player2Id = document.getString("player2Id") ?: "",
                moves = moves.mapNotNull { moveData ->
                    val from = moveData["from"] as? Map<String, Any>
                    val to = moveData["to"] as? Map<String, Any>
                    val pieceData = moveData["piece"] as? Map<String, Any>

                    if (from != null && to != null && pieceData != null) {
                        Move(
                            from = Position(
                                (from["row"] as Long).toInt(),
                                (from["col"] as Long).toInt()
                            ),
                            to = Position(
                                (to["row"] as Long).toInt(),
                                (to["col"] as Long).toInt()
                            ),
                            piece = ChessPiece(
                                type = PieceType.valueOf(pieceData["type"] as String),
                                color = PieceColor.valueOf(pieceData["color"] as String),
                                position = Position(0, 0) // Will be updated by engine
                            )
                        )
                    } else null
                },
                status = status,
                createdAt = document.getTimestamp("createdAt"),
                endedAt = document.getTimestamp("endedAt")
            )

            cacheManager.cacheGameState(gameState)

            // Update UI
            onStateChange(
                GameSyncState.Updated(
                    board = engine.board.value,
                    currentPlayer = if (currentPlayer == "WHITE") PieceColor.WHITE else PieceColor.BLACK,
                    isCheck = engine.isCheck.value,
                    gameResult = engine.getGameResult()
                )
            )

        } catch (e: Exception) {
            onStateChange(GameSyncState.Error(e.message ?: "Failed to process update"))
        }
    }

    /**
     * Sync any pending moves that might have failed
     */
    private suspend fun syncPendingMoves(gameId: String) {
        val pending = pendingMoves[gameId] ?: return
        val toRetry = pending.filter { it.status == MoveStatus.PENDING }

        toRetry.forEach { pendingMove ->
            if (System.currentTimeMillis() - pendingMove.timestamp > 10000) {
                // Move is too old, mark as rejected
                pendingMove.status = MoveStatus.REJECTED
            } else {
                // Retry the move
                makeMove(gameId, pendingMove.move) { /* handled elsewhere */ }
            }
        }

        // Clean up old moves
        pending.removeAll {
            it.status != MoveStatus.PENDING &&
                    System.currentTimeMillis() - it.timestamp > 30000
        }
    }

    /**
     * Stop syncing a game
     */
    fun stopGameSync(gameId: String) {
        gameListeners[gameId]?.remove()
        gameListeners.remove(gameId)
        gameEngines.remove(gameId)
        pendingMoves.remove(gameId)
    }

    /**
     * Clean up all resources
     */
    fun cleanup() {
        gameListeners.values.forEach { it.remove() }
        gameListeners.clear()
        gameEngines.clear()
        pendingMoves.clear()
        scope.cancel()
    }
}

/**
 * State updates for game synchronization
 */
sealed class GameSyncState {
    data class Updated(
        val board: Array<Array<ChessPiece?>>,
        val currentPlayer: PieceColor,
        val isCheck: Boolean = false,
        val gameResult: GameResult = GameResult.InProgress
    ) : GameSyncState()

    data class Error(val message: String) : GameSyncState()

    object Loading : GameSyncState()
}