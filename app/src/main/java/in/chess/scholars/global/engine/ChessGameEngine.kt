package `in`.chess.scholars.global.engine

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import `in`.chess.scholars.global.domain.model.*
import kotlin.math.abs

class ChessGameEngine {
    private val _board = mutableStateOf(initializeBoard())
    val board: State<Array<Array<ChessPiece?>>> = _board

    private val _currentPlayer = mutableStateOf(PieceColor.WHITE)
    val currentPlayer: State<PieceColor> = _currentPlayer

    private val _gameHistory = mutableListOf<Move>()
    val gameHistory: List<Move> = _gameHistory

    private val _isCheck = mutableStateOf(false)
    val isCheck: State<Boolean> = _isCheck

    private val _isCheckmate = mutableStateOf(false)
    val isCheckmate: State<Boolean> = _isCheckmate

    private val _isStalemate = mutableStateOf(false)
    val isStalemate: State<Boolean> = _isStalemate

    private val _isDraw = mutableStateOf(false)
    val isDraw: State<Boolean> = _isDraw

    private val _drawReason = mutableStateOf<DrawReason?>(null)
    val drawReason: State<DrawReason?> = _drawReason

    private val _positionHistory = mutableListOf<String>()
    private var halfMoveClock = 0
    private var enPassantTarget: Position? = null
    private val castlingRights = mutableMapOf("K" to true, "Q" to true, "k" to true, "q" to true)

    private fun initializeBoard(): Array<Array<ChessPiece?>> {
        val board = Array(8) { arrayOfNulls<ChessPiece>(8) }
        for (col in 0..7) {
            board[1][col] = ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(1, col))
            board[6][col] = ChessPiece(PieceType.PAWN, PieceColor.WHITE, Position(6, col))
        }
        val pieceOrder = listOf(PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK)
        pieceOrder.forEachIndexed { col, type ->
            board[0][col] = ChessPiece(type, PieceColor.BLACK, Position(0, col))
            board[7][col] = ChessPiece(type, PieceColor.WHITE, Position(7, col))
        }
        return board
    }

    fun resetBoard() {
        _board.value = initializeBoard()
        _currentPlayer.value = PieceColor.WHITE
        _gameHistory.clear()
        _positionHistory.clear()
        _isCheck.value = false
        _isCheckmate.value = false
        _isStalemate.value = false
        _isDraw.value = false
        _drawReason.value = null
        halfMoveClock = 0
        enPassantTarget = null
        castlingRights.putAll(mapOf("K" to true, "Q" to true, "k" to true, "q" to true))
    }

    fun getValidMovesForPiece(from: Position): Set<Position> {
        val validMoves = mutableSetOf<Position>()
        for (row in 0..7) {
            for (col in 0..7) {
                val to = Position(row, col)
                if (isValidMove(from, to)) {
                    validMoves.add(to)
                }
            }
        }
        return validMoves
    }

    fun makeMove(from: Position, to: Position): Boolean {
        if (!isValidMove(from, to)) return false

        val piece = _board.value[from.row][from.col]!!
        val capturedPiece = _board.value[to.row][to.col]

        val newBoard = _board.value.map { it.clone() }.toTypedArray()
        newBoard[to.row][to.col] = piece.copy(position = to, hasMoved = true)
        newBoard[from.row][from.col] = null
        _board.value = newBoard

        _gameHistory.add(Move(from, to, piece, capturedPiece))
        _currentPlayer.value = if (_currentPlayer.value == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        updateGameStatus()
        return true
    }

    private fun isValidMove(from: Position, to: Position): Boolean {
        val piece = _board.value[from.row][from.col] ?: return false
        if (piece.color != _currentPlayer.value) return false
        if (_board.value[to.row][to.col]?.color == piece.color) return false

        val tempBoard = _board.value.map { it.clone() }.toTypedArray()
        tempBoard[to.row][to.col] = piece
        tempBoard[from.row][from.col] = null
        if (isKingInCheck(piece.color, tempBoard)) return false

        return when (piece.type) {
            PieceType.PAWN -> isValidPawnMove(piece, from, to)
            PieceType.ROOK -> isValidRookMove(from, to)
            PieceType.KNIGHT -> isValidKnightMove(from, to)
            PieceType.BISHOP -> isValidBishopMove(from, to)
            PieceType.QUEEN -> isValidQueenMove(from, to)
            PieceType.KING -> isValidKingMove(piece, from, to)
        }
    }

    private fun isKingInCheck(color: PieceColor, board: Array<Array<ChessPiece?>>): Boolean {
        val kingPos = findKing(color, board) ?: return true
        val opponentColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board[row][col]
                if (piece != null && piece.color == opponentColor) {
                    if (canPieceAttack(Position(row, col), kingPos, board)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun updateGameStatus() {
        _isCheck.value = isKingInCheck(_currentPlayer.value, _board.value)
        val hasValidMoves = hasValidMoves(_currentPlayer.value)

        if (_isCheck.value && !hasValidMoves) {
            _isCheckmate.value = true
        } else if (!_isCheck.value && !hasValidMoves) {
            _isStalemate.value = true
            _isDraw.value = true
            _drawReason.value = DrawReason.STALEMATE
        }
    }

    fun getGameResult(): GameResult {
        return when {
            _isCheckmate.value -> GameResult.Win(if (_currentPlayer.value == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE)
            _isDraw.value -> GameResult.Draw(_drawReason.value ?: DrawReason.STALEMATE)
            else -> GameResult.InProgress
        }
    }

    private fun isValidPawnMove(piece: ChessPiece, from: Position, to: Position): Boolean {
        val direction = if (piece.color == PieceColor.WHITE) -1 else 1
        val startRow = if (piece.color == PieceColor.WHITE) 6 else 1

        // Forward move
        if (from.col == to.col) {
            if (to.row == from.row + direction && _board.value[to.row][to.col] == null) return true // One square
            if (from.row == startRow && to.row == from.row + 2 * direction && _board.value[to.row][to.col] == null && _board.value[from.row + direction][from.col] == null) return true // Two squares
        }
        // Capture
        if (abs(to.col - from.col) == 1 && to.row == from.row + direction) {
            if (_board.value[to.row][to.col]?.color != piece.color) return true
        }
        return false
    }

    private fun isValidRookMove(from: Position, to: Position): Boolean {
        if (from.row != to.row && from.col != to.col) return false
        return isPathClear(from, to)
    }

    private fun isValidKnightMove(from: Position, to: Position): Boolean {
        val rowDiff = abs(to.row - from.row)
        val colDiff = abs(to.col - from.col)
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
    }

    private fun isValidBishopMove(from: Position, to: Position): Boolean {
        if (abs(to.row - from.row) != abs(to.col - from.col)) return false
        return isPathClear(from, to)
    }

    private fun isValidQueenMove(from: Position, to: Position): Boolean {
        return isValidRookMove(from, to) || isValidBishopMove(from, to)
    }

    private fun isValidKingMove(piece: ChessPiece, from: Position, to: Position): Boolean {
        val rowDiff = abs(to.row - from.row)
        val colDiff = abs(to.col - from.col)
        return rowDiff <= 1 && colDiff <= 1
    }

    private fun isPathClear(from: Position, to: Position): Boolean {
        val rowStep = (to.row - from.row).coerceIn(-1, 1)
        val colStep = (to.col - from.col).coerceIn(-1, 1)
        var currentRow = from.row + rowStep
        var currentCol = from.col + colStep
        while (currentRow != to.row || currentCol != to.col) {
            if (_board.value[currentRow][currentCol] != null) return false
            currentRow += rowStep
            currentCol += colStep
        }
        return true
    }

    private fun findKing(color: PieceColor, board: Array<Array<ChessPiece?>>): Position? {
        board.forEachIndexed { r, row ->
            row.forEachIndexed { c, piece ->
                if (piece?.type == PieceType.KING && piece.color == color) return Position(r, c)
            }
        }
        return null
    }

    private fun canPieceAttack(from: Position, to: Position, board: Array<Array<ChessPiece?>>): Boolean {
        val piece = board[from.row][from.col] ?: return false
        // This check needs to ignore whose turn it is
        return when (piece.type) {
            PieceType.PAWN -> {
                val direction = if (piece.color == PieceColor.WHITE) -1 else 1
                abs(to.col - from.col) == 1 && to.row == from.row + direction
            }
            PieceType.ROOK -> isValidRookMove(from, to) && isPathClear(from, to)
            PieceType.KNIGHT -> isValidKnightMove(from, to)
            PieceType.BISHOP -> isValidBishopMove(from, to) && isPathClear(from, to)
            PieceType.QUEEN -> isValidQueenMove(from, to) && isPathClear(from, to)
            PieceType.KING -> isValidKingMove(piece, from, to)
        }
    }

    private fun hasValidMoves(color: PieceColor): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                if (_board.value[r][c]?.color == color) {
                    if (getValidMovesForPiece(Position(r, c)).isNotEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
