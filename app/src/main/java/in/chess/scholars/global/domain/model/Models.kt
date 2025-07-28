package `in`.chess.scholars.global.domain.model

import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp

data class UserData(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val rating: Int = 1200,
    val balance: Double = 0.0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val kycStatus: KycStatus = KycStatus.NOT_STARTED,
    val panNumber: String? = null,
    val aadharNumber: String? = null,
    val verified: Boolean = false
)

/**
 * Represents the KYC (Know Your Customer) status of a user.
 */
enum class KycStatus {
    NOT_STARTED, IN_PROGRESS, VERIFIED, REJECTED
}

// --- Chess Core Models ---

enum class PieceType { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING }
enum class PieceColor { WHITE, BLACK }

/**
 * Represents a position on the 8x8 chessboard.
 * @param row The row index (0-7).
 * @param col The column index (0-7).
 */
data class Position(val row: Int = 0, val col: Int = 0) {
    fun isValid() = row in 0..7 && col in 0..7
}

/**
 * Represents a single chess piece with its properties.
 */
data class ChessPiece(
    val type: PieceType = PieceType.PAWN,
    val color: PieceColor = PieceColor.WHITE,
    val position: Position = Position(),
    val hasMoved: Boolean = false
)

/**
 * Represents a single move made in a game.
 */
data class Move(
    val from: Position = Position(),
    val to: Position = Position(),
    val piece: ChessPiece = ChessPiece(),
    val capturedPiece: ChessPiece? = null,
    val isPromotion: Boolean = false,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false
)

/**
 * Represents the reason for a draw, compliant with FIDE rules.
 */
enum class DrawReason {
    STALEMATE,
    INSUFFICIENT_MATERIAL,
    THREEFOLD_REPETITION,
    FIFTY_MOVE_RULE,
    SEVENTY_FIVE_MOVE_RULE,
    AGREEMENT
}

/**
 * Represents the result of a chess game.
 */
sealed class GameResult {
    object InProgress : GameResult()
    data class Win(val winner: PieceColor) : GameResult()
    data class Draw(val reason: DrawReason) : GameResult()
}

// --- Wallet & Transaction Models ---

/**
 * Represents a financial transaction in a user's wallet.
 */
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.DEPOSIT,
    val amount: Double = 0.0,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val referenceId: String = "", // For game ID or payment ID
    val balanceAfter: Double = 0.0,
    val isCredit: Boolean = true
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    GAME_ENTRY,
    GAME_WIN,
    REFUND
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}

// --- Statistics Models ---

/**
 * Represents a historical record of a completed game.
 */
data class GameHistory(
    val gameId: String,
    val opponent: String,
    val opponentRating: Int,
    val result: GameResultStats,
    val ratingChange: Int,
    val betAmount: Float,
    val duration: Long,
    val date: Long,
    val openingPlayed: String = "Unknown"
)

enum class GameResultStats {
    WIN, LOSS, DRAW
}

/**
 * Represents a single point in a user's rating history for graphing.
 */
data class RatingPoint(
    val date: Long,
    val rating: Int,
    val gameNumber: Int
)
data class GameState(
    val gameId: String = "",
    val player1Id: String = "",
    val player2Id: String = "",
    val player1Color: String = "WHITE", // 👈 Add this
    val player2Color: String = "BLACK", // 👈 Add this
    val currentPlayer: String = "WHITE", // 👈 Add this
    val moves: List<Move> = emptyList(),
    val status: String = "active",
    val winner: String? = null,
    val drawReason: String? = null,
    val betAmount: Float = 0f, // 👈 Add this
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val lastMoveAt: Timestamp? = null, // 👈 Add this
    @ServerTimestamp
    val endedAt: Timestamp? = null
)

data class ChatMessage(
    val messageId: String = "",
    val gameId: String = "",
    val userId: String = "",
    val displayName: String = "",
    val message: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
)

