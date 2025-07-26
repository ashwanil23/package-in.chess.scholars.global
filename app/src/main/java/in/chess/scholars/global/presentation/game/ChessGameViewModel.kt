package `in`.chess.scholars.global.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.*
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.usecases.*
import `in`.chess.scholars.global.engine.ChessGameEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Game screen.
 */
data class GameUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val board: Array<Array<ChessPiece?>> = emptyArray(),
    val currentPlayer: PieceColor = PieceColor.WHITE,
    val playerColor: PieceColor = PieceColor.WHITE,
    val opponentData: UserData? = null,
    val isMyTurn: Boolean = false,
    val selectedPiece: Position? = null,
    val validMoves: Set<Position> = emptySet(),
    val isCheck: Boolean = false,
    val gameResult: GameResult = GameResult.InProgress,
    val opponentOfferedDraw: Boolean = false
) {
    // Override equals and hashCode to prevent recomposition from the board array instance change
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GameUiState
        if (isLoading != other.isLoading) return false
        if (error != other.error) return false
        if (!board.contentDeepEquals(other.board)) return false
        if (currentPlayer != other.currentPlayer) return false
        if (playerColor != other.playerColor) return false
        if (opponentData != other.opponentData) return false
        if (isMyTurn != other.isMyTurn) return false
        if (selectedPiece != other.selectedPiece) return false
        if (validMoves != other.validMoves) return false
        if (isCheck != other.isCheck) return false
        if (gameResult != other.gameResult) return false
        if (opponentOfferedDraw != other.opponentOfferedDraw) return false
        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + board.contentDeepHashCode()
        result = 31 * result + currentPlayer.hashCode()
        result = 31 * result + playerColor.hashCode()
        result = 31 * result + (opponentData?.hashCode() ?: 0)
        result = 31 * result + isMyTurn.hashCode()
        result = 31 * result + (selectedPiece?.hashCode() ?: 0)
        result = 31 * result + validMoves.hashCode()
        result = 31 * result + isCheck.hashCode()
        result = 31 * result + gameResult.hashCode()
        result = 31 * result + opponentOfferedDraw.hashCode()
        return result
    }
}

/**
 * ViewModel for the chess game screen.
 */
class ChessGameViewModel(
    private val getGameStreamUseCase: GetGameStreamUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val endGameUseCase: EndGameUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    private val gameEngine = ChessGameEngine()
    private val _uiState = MutableStateFlow(GameUiState(board = gameEngine.board.value))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameId: String? = null

    fun initializeGame(gameId: String) {
        this.gameId = gameId
        val currentUserId = getCurrentUserIdUseCase()

        viewModelScope.launch {
            getGameStreamUseCase(gameId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        val gameState = result.data
                        val playerColor = if (gameState.player1Id == currentUserId) PieceColor.WHITE else PieceColor.BLACK
                        val opponentId = if (playerColor == PieceColor.WHITE) gameState.player2Id else gameState.player1Id

                        // Update game engine with moves from Firestore
                        gameEngine.resetBoard()
                        gameState.moves.forEach { move -> gameEngine.makeMove(move.from, move.to) }

                        updateLocalUiState(playerColor)

                        // Fetch opponent data if not already loaded
                        if (_uiState.value.opponentData == null && opponentId.isNotEmpty()) {
                            fetchOpponentData(opponentId)
                        }
                    }
                    is DataResult.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                    }
                }
            }
        }
    }

    private fun fetchOpponentData(opponentId: String) {
        viewModelScope.launch {
            getUserDataUseCase(opponentId).collect { result ->
                if (result is DataResult.Success) {
                    _uiState.value = _uiState.value.copy(opponentData = result.data)
                }
            }
        }
    }

    fun onSquareClick(position: Position) {
        if (!_uiState.value.isMyTurn || uiState.value.gameResult !is GameResult.InProgress) return

        val selectedPos = _uiState.value.selectedPiece
        val pieceAtPos = gameEngine.board.value[position.row][position.col]

        if (selectedPos == null) {
            // If no piece is selected, select the clicked piece if it's ours
            if (pieceAtPos != null && pieceAtPos.color == _uiState.value.playerColor) {
                _uiState.value = _uiState.value.copy(
                    selectedPiece = position,
                    validMoves = gameEngine.getValidMovesForPiece(position)
                )
            }
        } else {
            // If a piece is selected, try to move it
            if (position in _uiState.value.validMoves) {
                makeMove(selectedPos, position)
            } else {
                // If the click is on another of our pieces, switch selection
                if (pieceAtPos != null && pieceAtPos.color == _uiState.value.playerColor) {
                    _uiState.value = _uiState.value.copy(
                        selectedPiece = position,
                        validMoves = gameEngine.getValidMovesForPiece(position)
                    )
                } else {
                    // Deselect
                    _uiState.value = _uiState.value.copy(selectedPiece = null, validMoves = emptySet())
                }
            }
        }
    }

    private fun makeMove(from: Position, to: Position) {
        val piece = gameEngine.board.value[from.row][from.col] ?: return
        val move = Move(from, to, piece = piece)
        if (gameEngine.makeMove(from, to)) {
            updateLocalUiState(_uiState.value.playerColor) // Update UI immediately for responsiveness
            viewModelScope.launch {
                updateGameUseCase(gameId!!, move)
                // Check if the move ended the game
                val result = gameEngine.getGameResult()
                if (result !is GameResult.InProgress) {
                    endGameUseCase(gameId!!, result)
                }
            }
        }
    }

    fun resignGame() {
        val winner = if (_uiState.value.playerColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val result = GameResult.Win(winner)
        viewModelScope.launch {
            endGameUseCase(gameId!!, result)
        }
    }

    fun offerDraw() {
        // In a real implementation, this would set a "drawOfferedBy" field in Firestore
        // For now, we assume the use case handles this logic
    }

    fun acceptDraw() {
        val result = GameResult.Draw(DrawReason.AGREEMENT)
        viewModelScope.launch {
            endGameUseCase(gameId!!, result)
        }
    }

    private fun updateLocalUiState(playerColor: PieceColor) {
        val newBoardState = gameEngine.board.value.map { it.clone() }.toTypedArray()
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            board = newBoardState,
            currentPlayer = gameEngine.currentPlayer.value,
            playerColor = playerColor,
            isMyTurn = gameEngine.currentPlayer.value == playerColor,
            isCheck = gameEngine.isCheck.value,
            gameResult = gameEngine.getGameResult(),
            selectedPiece = null,
            validMoves = emptySet()
        )
    }
}

