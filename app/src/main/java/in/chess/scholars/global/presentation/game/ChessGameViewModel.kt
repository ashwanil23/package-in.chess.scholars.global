package `in`.chess.scholars.global.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.*
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.usecases.*
import `in`.chess.scholars.global.engine.ChessGameEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


data class PrizeInfo(
    val betAmount: Float = 0f,
    val prizePool: Float = 0f,
    val platformFee: Float = 0f,
    val taxableAmount: Float = 0f,
    val tdsDeducted: Float = 0f,
    val winningsPayable: Float = 0f
)
/**
 * UI state for the Game screen.
 */
data class GameUiState(
    val chatMessages: List<ChatMessage> = emptyList(),
    val hasUnreadMessages: Boolean = false,
    val whiteCapturedPieces: List<ChessPiece> = emptyList(),
    val blackCapturedPieces: List<ChessPiece> = emptyList(),
    val prizeInfo: PrizeInfo = PrizeInfo(),
    val lastMove: Pair<Position, Position>? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val board: Array<Array<ChessPiece?>> = Array(8) { arrayOfNulls<ChessPiece>(8) },
    val currentPlayer: PieceColor = PieceColor.WHITE,
    val playerColor: PieceColor = PieceColor.WHITE,
    val opponentData: UserData? = null,
    val isMyTurn: Boolean = false,
    val selectedPiece: Position? = null,
    val validMoves: Set<Position> = emptySet(),
    val isCheck: Boolean = false,
    val gameResult: GameResult = GameResult.InProgress,
    val drawOfferState: DrawOfferState = DrawOfferState.NONE,
    val player1TimeLeftMs: Long = DEFAULT_GAME_TIME_MS,
    val player2TimeLeftMs: Long = DEFAULT_GAME_TIME_MS
) {
    // Enum to manage the state of a draw offer
    enum class DrawOfferState {
        NONE, SENT, RECEIVED
    }
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
        if (drawOfferState != other.drawOfferState) return false
        if (player1TimeLeftMs != other.player1TimeLeftMs) return false
        if (player2TimeLeftMs != other.player2TimeLeftMs) return false
        if (whiteCapturedPieces != other.whiteCapturedPieces) return false
        if (blackCapturedPieces != other.blackCapturedPieces) return false
        if (chatMessages != other.chatMessages) return false
        if (hasUnreadMessages != other.hasUnreadMessages) return false
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
        result = 31 * result + drawOfferState.hashCode()
        result = 31 * result + player1TimeLeftMs.hashCode()
        result = 31 * result + player2TimeLeftMs.hashCode()
        result = 31 * result + chatMessages.hashCode()
        result = 31 * result + hasUnreadMessages.hashCode()
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
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getChatStreamUseCase: GetChatStreamUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateDrawOfferUseCase: UpdateDrawOfferUseCase
) : ViewModel() {

    private val gameEngine = ChessGameEngine()
    private val _uiState = MutableStateFlow(GameUiState(board = gameEngine.board.value))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameId: String? = null
    private var currentUserId: String? = null
    private var currentUserData: UserData? = null
    private var isChatSheetOpen = false
    private var timerJob: Job? = null

    fun initializeGame(gameId: String, betAmount: Float) {
        this.gameId = gameId
        this.currentUserId = getCurrentUserIdUseCase()

        _uiState.value = GameUiState(board = gameEngine.board.value)

        viewModelScope.launch {
            currentUserId?.let {
                getUserDataUseCase(it).collect { result ->
                    if (result is DataResult.Success) {
                        currentUserData = result.data
                    }
                }
            }
        }

        // *** FIX: Adjusted platform fee and clarified prize calculations ***
        // The prize pool is the sum of both players' bets.
        val prizePool = betAmount * 2
        // The platform takes a fee (e.g., 10%) from the total prize pool.
        val platformFee = prizePool * 0.10f
        // The winner's net profit (on which tax is calculated).
        val netWinnings = prizePool - platformFee - betAmount
        // Tax Deducted at Source (TDS) is 30% of net winnings, as per Indian law.
        val tdsDeducted = if (netWinnings > 0) netWinnings * 0.30f else 0f
        // The final amount the winner receives.
        val winningsPayable = prizePool - platformFee - tdsDeducted

        val prizeInfo = PrizeInfo(
            betAmount = betAmount,
            prizePool = prizePool,
            platformFee = platformFee,
            taxableAmount = netWinnings,
            tdsDeducted = tdsDeducted,
            winningsPayable = winningsPayable
        )
        _uiState.value = _uiState.value.copy(prizeInfo = prizeInfo)

        if (gameId == "offline_game") {
            gameEngine.resetBoard()
            updateLocalUiState(PieceColor.WHITE)
            return
        }
        listenForChatMessages(gameId)

        viewModelScope.launch {
            getGameStreamUseCase(gameId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        val gameState = result.data
                        val playerColor = if (gameState.player1Id == currentUserId) PieceColor.WHITE else PieceColor.BLACK
                        val opponentId = if (playerColor == PieceColor.WHITE) gameState.player2Id else gameState.player1Id

                        _uiState.value = _uiState.value.copy(
                            player1TimeLeftMs = gameState.player1TimeLeft,
                            player2TimeLeftMs = gameState.player2TimeLeft
                        )

                        if (gameState.drawOfferBy != null && gameState.drawOfferBy != currentUserId) {
                            _uiState.value = _uiState.value.copy(drawOfferState = GameUiState.DrawOfferState.RECEIVED)
                        } else if (gameState.drawOfferBy == null && _uiState.value.drawOfferState == GameUiState.DrawOfferState.RECEIVED) {
                            _uiState.value = _uiState.value.copy(drawOfferState = GameUiState.DrawOfferState.NONE)
                        }

                        gameEngine.resetBoard()
                        gameState.moves.forEach { move -> gameEngine.makeMove(move.from, move.to) }

                        updateLocalUiState(playerColor)

                        if (gameState.status == "finished" || gameState.status == "draw") {
                            val finalResult = if (gameState.status == "draw") {
                                GameResult.Draw(DrawReason.AGREEMENT)
                            } else {
                                val winnerColor = if (gameState.winner == "WHITE") PieceColor.WHITE else PieceColor.BLACK
                                GameResult.Win(winnerColor)
                            }
                            _uiState.value = _uiState.value.copy(gameResult = finalResult)
                            timerJob?.cancel()
                        } else {
                            handleTimer(gameState)
                        }


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

    private fun handleTimer(gameState: GameState) {
        timerJob?.cancel()
        if (gameState.status != "active") return

        timerJob = viewModelScope.launch {
            val isPlayer1Turn = gameState.currentPlayer == "WHITE"
            var timeLeft = if (isPlayer1Turn) gameState.player1TimeLeft else gameState.player2TimeLeft
            val lastMoveTime = gameState.lastMoveAt?.toDate()?.time ?: System.currentTimeMillis()
            val timeSinceLastMove = System.currentTimeMillis() - lastMoveTime
            timeLeft -= timeSinceLastMove

            while (this.isActive && timeLeft > 0) {
                if (isPlayer1Turn) {
                    _uiState.value = _uiState.value.copy(player1TimeLeftMs = timeLeft)
                } else {
                    _uiState.value = _uiState.value.copy(player2TimeLeftMs = timeLeft)
                }
                delay(1000)
                timeLeft -= 1000
            }

            if (this.isActive && timeLeft <= 0) {
                handleTimeout()
            }
        }
    }

    private fun handleTimeout() {
        if (gameId == null) return
        val winner = if (_uiState.value.currentPlayer == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val result = GameResult.Win(winner)
        _uiState.value = _uiState.value.copy(gameResult = result)
        viewModelScope.launch {
            endGameUseCase(gameId!!, result)
        }
    }


    private fun listenForChatMessages(gameId: String) {
        if (gameId == "offline_game") return

        viewModelScope.launch {
            getChatStreamUseCase(gameId).collect { result ->
                if (result is DataResult.Success) {
                    val hasNew = if (isChatSheetOpen) false else result.data.isNotEmpty()
                    _uiState.value = _uiState.value.copy(
                        chatMessages = result.data,
                        hasUnreadMessages = hasNew
                    )
                }
            }
        }
    }

    fun onSendMessage(messageText: String) {
        val gameId = this.gameId ?: return
        val userId = this.currentUserId ?: return
        val displayName = this.currentUserData?.displayName ?: "Player"

        if (messageText.isBlank()) return

        val chatMessage = ChatMessage(
            gameId = gameId,
            userId = userId,
            displayName = displayName,
            message = messageText.trim()
        )

        viewModelScope.launch {
            sendMessageUseCase(gameId, chatMessage)
        }
    }

    fun getCurrentUserId(): String? {
        return currentUserId
    }
    fun onChatOpened() {
        isChatSheetOpen = true
        _uiState.value = _uiState.value.copy(hasUnreadMessages = false)
    }
    fun onChatClosed() {
        isChatSheetOpen = false
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
        if (!uiState.value.isMyTurn && gameId != "offline_game") return
        if (uiState.value.gameResult !is GameResult.InProgress) return
        val selectedPos = _uiState.value.selectedPiece
        val pieceAtPos = gameEngine.board.value.getOrNull(position.row)?.getOrNull(position.col)

        if (selectedPos == null) {
            if (pieceAtPos != null && pieceAtPos.color == gameEngine.currentPlayer.value) {
                _uiState.value = _uiState.value.copy(
                    selectedPiece = position,
                    validMoves = gameEngine.getValidMovesForPiece(position)
                )
            }
        } else {
            if (position in _uiState.value.validMoves) {
                makeMove(selectedPos, position)
            } else {
                if (pieceAtPos != null && pieceAtPos.color == gameEngine.currentPlayer.value) {
                    _uiState.value = _uiState.value.copy(
                        selectedPiece = position,
                        validMoves = gameEngine.getValidMovesForPiece(position)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(selectedPiece = null, validMoves = emptySet())
                }
            }
        }
    }

    private fun makeMove(from: Position, to: Position) {
        val piece = gameEngine.board.value[from.row][from.col] ?: return
        val capturedPiece = gameEngine.board.value[to.row][to.col]
        val move = Move(from, to, piece = piece, capturedPiece = capturedPiece)

        if (gameEngine.makeMove(from, to)) {
            updateLocalUiState(_uiState.value.playerColor)
            if (gameId != "offline_game") {
                viewModelScope.launch {
                    updateGameUseCase(gameId!!, move)
                    val result = gameEngine.getGameResult()
                    if (result !is GameResult.InProgress) {
                        endGameUseCase(gameId!!, result)
                    }
                }
            }
        }
    }

    fun resignGame() {
        if (gameId == "offline_game" || gameId == null) return
        val winner = if (_uiState.value.playerColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val result = GameResult.Win(winner)

        _uiState.value = _uiState.value.copy(gameResult = result)
        timerJob?.cancel()

        viewModelScope.launch {
            endGameUseCase(gameId!!, result)
        }
    }

    fun offerDraw() {
        if (gameId == "offline_game" || gameId == null || currentUserId == null) return
        _uiState.value = _uiState.value.copy(drawOfferState = GameUiState.DrawOfferState.SENT)
        viewModelScope.launch {
            updateDrawOfferUseCase(gameId!!, currentUserId)
        }
    }

    fun acceptDraw() {
        if (gameId == "offline_game" || gameId == null) return
        val result = GameResult.Draw(DrawReason.AGREEMENT)

        _uiState.value = _uiState.value.copy(gameResult = result, drawOfferState = GameUiState.DrawOfferState.NONE)
        timerJob?.cancel()

        viewModelScope.launch {
            endGameUseCase(gameId!!, result)
            updateDrawOfferUseCase(gameId!!, null)
        }
    }

    fun rejectDraw() {
        if (gameId == "offline_game" || gameId == null) return
        _uiState.value = _uiState.value.copy(drawOfferState = GameUiState.DrawOfferState.NONE)
        viewModelScope.launch {
            updateDrawOfferUseCase(gameId!!, null)
        }
    }


    private fun updateLocalUiState(playerColor: PieceColor) {
        val newBoardState = gameEngine.board.value.map { it.clone() }.toTypedArray()
        val whiteCaptured = mutableListOf<ChessPiece>()
        val blackCaptured = mutableListOf<ChessPiece>()
        gameEngine.gameHistory.forEach { move ->
            move.capturedPiece?.let {
                if (it.color == PieceColor.WHITE) {
                    whiteCaptured.add(it)
                } else {
                    blackCaptured.add(it)
                }
            }
        }
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            board = newBoardState,
            currentPlayer = gameEngine.currentPlayer.value,
            playerColor = playerColor,
            isMyTurn = if (gameId != "offline_game") gameEngine.currentPlayer.value == playerColor else true,
            isCheck = gameEngine.isCheck.value,
            gameResult = if (_uiState.value.gameResult is GameResult.InProgress) gameEngine.getGameResult() else _uiState.value.gameResult,
            selectedPiece = null,
            validMoves = emptySet(),
            whiteCapturedPieces = whiteCaptured,
            blackCapturedPieces = blackCaptured,
            lastMove = gameEngine.gameHistory.lastOrNull()?.let { it.from to it.to }
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
