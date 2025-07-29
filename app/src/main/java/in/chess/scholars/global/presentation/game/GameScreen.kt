package `in`.chess.scholars.global.presentation.game

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.*

// CORRECTED: Helper function to format time
private fun formatTime(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameId: String,
    betAmount: Float,
    navController: NavController,
    viewModel: ChessGameViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showInfoSheet by remember { mutableStateOf(false) }
    var showResignDialog by remember { mutableStateOf(false) }
    var showDrawDialog by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    var showDrawOfferReceivedDialog by remember { mutableStateOf(false) }


    // Notify ViewModel when chat sheet state changes
    LaunchedEffect(showChatSheet) {
        if (showChatSheet) {
            viewModel.onChatOpened()
        } else {
            viewModel.onChatClosed()
        }
    }
    // Initialize the game when the screen is first composed
    LaunchedEffect(gameId) {
        viewModel.initializeGame(gameId, betAmount)
    }

    // Observer for draw offer state
    LaunchedEffect(uiState.drawOfferState) {
        showDrawOfferReceivedDialog = uiState.drawOfferState == GameUiState.DrawOfferState.RECEIVED
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Animated gradient background
        PremiumGameBackground()

        // Main game content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Opponent info
            PlayerInfoCard(
                player = uiState.opponentData,
                isCurrentTurn = uiState.currentPlayer != uiState.playerColor,
                // CORRECTED: Pass the correct timer value
                timeLeftMs = if (uiState.playerColor == PieceColor.WHITE) uiState.player2TimeLeftMs else uiState.player1TimeLeftMs,
                isOpponent = true
            )
            CapturedPiecesRow(
                pieces = if (uiState.playerColor == PieceColor.WHITE) uiState.blackCapturedPieces else uiState.whiteCapturedPieces
            )

            // Chess board with premium design
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                PremiumChessBoard(
                    board = uiState.board,
                    playerColor = uiState.playerColor,
                    selectedPiece = uiState.selectedPiece,
                    validMoves = uiState.validMoves,
                    lastMove = uiState.lastMove,
                    isCheck = uiState.isCheck,
                    onSquareClick = viewModel::onSquareClick
                )

                // Loading overlay
                if (uiState.isLoading) {
                    LoadingOverlay()
                }
            }

            // Game controls
            GameControlsBar(
                isMyTurn = uiState.isMyTurn,
                hasUnreadMessages = uiState.hasUnreadMessages,
                onResign = { showResignDialog = true },
                onOfferDraw = { showDrawDialog = true },
                onChat = { showChatSheet = true },
                onInfoClick = { showInfoSheet = true }
            )

            // Current player info
            CapturedPiecesRow(
                pieces = if (uiState.playerColor == PieceColor.WHITE) uiState.whiteCapturedPieces else uiState.blackCapturedPieces
            )
            PlayerInfoCard(
                player = null, // Current user
                isCurrentTurn = uiState.currentPlayer == uiState.playerColor,
                // CORRECTED: Pass the correct timer value
                timeLeftMs = if (uiState.playerColor == PieceColor.WHITE) uiState.player1TimeLeftMs else uiState.player2TimeLeftMs,
                isOpponent = false
            )
        }
        if (showInfoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showInfoSheet = false },
                sheetState = sheetState
            ) {
                GameInfoSheetContent(uiState.prizeInfo)
            }
        }

        if (showChatSheet) {
            ModalBottomSheet(
                onDismissRequest = { showChatSheet = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxHeight(0.7f)
            ) {
                ChatSheetContent(
                    messages = uiState.chatMessages,
                    onSendMessage = viewModel::onSendMessage,
                    currentUserId = viewModel.getCurrentUserId() ?: ""
                )
            }
        }
        // Game status indicators
        GameStatusIndicators(
            isCheck = uiState.isCheck,
            gameResult = uiState.gameResult
        )

        // Dialogs
        if (showResignDialog) {
            ResignConfirmationDialog(
                onConfirm = {
                    viewModel.resignGame()
                    showResignDialog = false
                },
                onDismiss = { showResignDialog = false }
            )
        }

        if (showDrawDialog) {
            DrawOfferDialog(
                onConfirm = {
                    viewModel.offerDraw()
                    showDrawDialog = false
                },
                onDismiss = { showDrawDialog = false }
            )
        }

        if (showDrawOfferReceivedDialog) {
            DrawOfferReceivedDialog(
                onAccept = {
                    viewModel.acceptDraw()
                    showDrawOfferReceivedDialog = false
                },
                onDecline = {
                    viewModel.rejectDraw()
                    showDrawOfferReceivedDialog = false
                }
            )
        }


        // Game over overlay with animation
        AnimatedVisibility(
            visible = uiState.gameResult !is GameResult.InProgress,
            enter = fadeIn() + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = fadeOut() + scaleOut()
        ) {
            GameOverOverlay(
                result = uiState.gameResult,
                playerColor = uiState.playerColor,
                prizeInfo = uiState.prizeInfo,
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PremiumGameBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val animatedFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Dark gradient base
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0A0A0F),
                    Color(0xFF1A1A2E),
                    Color(0xFF0F0F1F)
                ),
                center = center,
                radius = size.minDimension
            )
        )

        // Animated geometric patterns
        val patternCount = 6
        for (i in 0 until patternCount) {
            rotate(animatedFloat + i * 60f, pivot = center) {
                drawPath(
                    path = Path().apply {
                        moveTo(center.x, center.y - size.minDimension * 0.4f)
                        lineTo(center.x + size.minDimension * 0.2f, center.y)
                        lineTo(center.x, center.y + size.minDimension * 0.4f)
                        lineTo(center.x - size.minDimension * 0.2f, center.y)
                        close()
                    },
                    color = Color(0xFF4ECDC4).copy(alpha = 0.05f)
                )
            }
        }
    }
}

@Composable
private fun PlayerInfoCard(
    player: UserData?,
    isCurrentTurn: Boolean,
    timeLeftMs: Long, // CORRECTED: Changed to Long
    isOpponent: Boolean
) {
    val animatedBorderAlpha by animateFloatAsState(
        targetValue = if (isCurrentTurn) 1f else 0f,
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color(0xFF4ECDC4).copy(alpha = animatedBorderAlpha)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isOpponent) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF5252),
                                        Color(0xFFFF1744)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4ECDC4),
                                        Color(0xFF44A3A0)
                                    )
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (player != null) {
                        Text(
                            player.displayName.firstOrNull()?.toString() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "You",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        player?.displayName ?: "You",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "${player?.rating ?: "..."}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Timer
            TimerDisplay(
                timeLeftMs = timeLeftMs,
                isActive = isCurrentTurn
            )
        }
    }
}

@Composable
private fun TimerDisplay(timeLeftMs: Long, isActive: Boolean) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = if (isActive) {
            infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            snap()
        }
    )

    // CORRECTED: Timer color changes when time is low
    val isTimeCritical = timeLeftMs <= 30_000L
    val timerColor = if (isTimeCritical) Color(0xFFFF5252) else Color(0xFF4ECDC4)

    Box(
        modifier = Modifier
            .scale(animatedScale)
            .background(
                color = if (isActive) timerColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            formatTime(timeLeftMs),
            color = if (isActive) timerColor else Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PremiumChessBoard(
    board: Array<Array<ChessPiece?>>,
    playerColor: PieceColor,
    selectedPiece: Position?,
    validMoves: Set<Position>,
    lastMove: Pair<Position, Position>?,
    isCheck: Boolean,
    onSquareClick: (Position) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        val squareSize = maxWidth / 8
        val isBoardFlipped = playerColor == PieceColor.BLACK

        // Board background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8B7355),
                        Color(0xFF654321),
                        Color(0xFF8B7355)
                    )
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF3E2723),
                topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                size = Size(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
            )
        }

        // Chess squares and pieces
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            // Board squares
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (row in 0..7) {
                    for (col in 0..7) {
                        val isLight = (row + col) % 2 == 0
                        val squareColor = when {
                            selectedPiece == Position(row, col) -> {
                                if (isLight) Color(0xFF7BC862) else Color(0xFF649B54)
                            }
                            lastMove?.first == Position(row, col) || lastMove?.second == Position(row, col) -> {
                                if (isLight) Color(0xFFF6F669).copy(alpha = 0.5f) else Color(0xFFBBCA2B).copy(alpha = 0.5f)
                            }
                            else -> {
                                if (isLight) Color(0xFFEFEFD5) else Color(0xFF769656)
                            }
                        }

                        drawRect(
                            color = squareColor,
                            topLeft = Offset(col * squareSize.toPx(), row * squareSize.toPx()),
                            size = Size(squareSize.toPx(), squareSize.toPx())
                        )
                    }
                }
            }

            // Pieces and interactive elements
            board.forEachIndexed { row, rowArray ->
                rowArray.forEachIndexed { col, piece ->
                    val position = Position(row, col)
                    val displayRow = if (isBoardFlipped) 7 - row else row
                    val displayCol = if (isBoardFlipped) 7 - col else col

                    Box(
                        modifier = Modifier
                            .size(squareSize)
                            .offset(x = displayCol * squareSize, y = displayRow * squareSize)
                            .clickable { onSquareClick(position) }
                    ) {
                        if (position in validMoves) {
                            ValidMoveIndicator(
                                haspiece = piece != null,
                                squareSize = squareSize
                            )
                        }
                        piece?.let {
                            PremiumChessPiece(
                                piece = it,
                                size = squareSize,
                                isSelected = selectedPiece == position
                            )
                        }
                        if (isCheck && piece?.type == PieceType.KING && piece.color == playerColor) {
                            CheckIndicator()
                        }
                    }
                }
            }
            FileRankLabels(squareSize, isBoardFlipped)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawOfferReceivedDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        modifier = Modifier.clip(RoundedCornerShape(24.dp))
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1a1a2e)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Handshake,
                    contentDescription = "Draw Offer",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Draw Offer Received",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Your opponent has offered a draw.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Decline", color = Color.White)
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}


@Composable
private fun ValidMoveIndicator(haspiece: Boolean, squareSize: Dp) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (haspiece) {
            // Capture indicator
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        } else {
            // Move indicator
            Canvas(modifier = Modifier.size(squareSize * 0.3f)) {
                drawCircle(
                    color = Color.Black.copy(alpha = 0.3f),
                    radius = size.minDimension / 2
                )
            }
        }
    }
}

@Composable
private fun PremiumChessPiece(
    piece: ChessPiece,
    size: Dp,
    isSelected: Boolean
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(animatedScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size * 0.85f)
        ) {
            drawChessPiece(
                pieceType = piece.type,
                pieceColor = piece.color,
                size = size.toPx() * 0.85f
            )
        }
    }
}
