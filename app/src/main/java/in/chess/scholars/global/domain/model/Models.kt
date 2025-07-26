package `in`.chess.scholars.global.domain.model

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
data class Position(val row: Int, val col: Int) {
    fun isValid() = row in 0..7 && col in 0..7
}

/**
 * Represents a single chess piece with its properties.
 */
data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val position: Position,
    val hasMoved: Boolean = false
)

/**
 * Represents a single move made in a game.
 */
data class Move(
    val from: Position,
    val to: Position,
    val piece: ChessPiece,
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
    val moves: List<Move> = emptyList(),
    val status: String = "in_progress", // e.g., "in_progress", "finished", "draw"
    val winner: String? = null, // "WHITE" or "BLACK"
    val drawReason: String? = null,
    val createdAt: Long = 0L,
    val endedAt: Long? = null
)

