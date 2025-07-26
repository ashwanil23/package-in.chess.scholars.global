//package `in`.chess.scholars.global.presentation.game
//
//import android.annotation.SuppressLint
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.Spring
//import androidx.compose.animation.core.spring
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.scaleIn
//import androidx.compose.animation.scaleOut
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.unit.times
//import androidx.navigation.NavController
//import `in`.chess.scholars.global.domain.model.*
//import kotlinx.coroutines.delay
//
//@Composable
//fun GameScreen(
//    gameId: String,
//    betAmount: Float,
//    navController: NavController,
//    viewModel: ChessGameViewModel
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    // Initialize the game when the screen is first composed
//    LaunchedEffect(gameId) {
//        viewModel.initializeGame(gameId)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFF1a1a2e),
//                        Color(0xFF16213e),
//                        Color(0xFF0f3460)
//                    )
//                )
//            )
//    ) {
//        if (uiState.isLoading) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//        } else {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                PlayerInfo(
//                    player = uiState.opponentData,
//                    isCurrentTurn = uiState.currentPlayer != uiState.playerColor
//                )
//
//                ChessBoard(
//                    board = uiState.board,
//                    playerColor = uiState.playerColor,
//                    selectedPiece = uiState.selectedPiece,
//                    validMoves = uiState.validMoves,
//                    onSquareClick = viewModel::onSquareClick
//                )
//
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    GameControls(
//                        isMyTurn = uiState.isMyTurn,
//                        onResign = viewModel::resignGame,
//                        onOfferDraw = viewModel::offerDraw
//                    )
//                    PlayerInfo(
//                        player = null, // Represents the current user
//                        isCurrentTurn = uiState.currentPlayer == uiState.playerColor
//                    )
//                }
//            }
//        }
//
//        // Game Over Overlay
//        AnimatedVisibility(
//            visible = uiState.gameResult !is GameResult.InProgress,
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//            GameOverOverlay(
//                result = uiState.gameResult,
//                playerColor = uiState.playerColor,
//                onDismiss = { navController.popBackStack() }
//            )
//        }
//    }
//}
//
//@Composable
//private fun PlayerInfo(player: UserData?, isCurrentTurn: Boolean) {
//    val borderColor = if (isCurrentTurn) Color(0xFF4CAF50) else Color.Transparent
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e))
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(Icons.Default.Person, contentDescription = "Player", tint = Color.White)
//            Spacer(modifier = Modifier.width(8.dp))
//            Column {
//                Text(player?.displayName ?: "You", color = Color.White, fontWeight = FontWeight.Bold)
//                Text("Rating: ${player?.rating ?: "..."}", color = Color.Gray, fontSize = 14.sp)
//            }
//        }
//    }
//}
//
//@SuppressLint("UnusedBoxWithConstraintsScope")
//@Composable
//private fun ChessBoard(
//    board: Array<Array<ChessPiece?>>,
//    playerColor: PieceColor,
//    selectedPiece: Position?,
//    validMoves: Set<Position>,
//    onSquareClick: (Position) -> Unit
//) {
//    BoxWithConstraints(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .aspectRatio(1f)
//            .clip(RoundedCornerShape(12.dp))
//    ) {
//        val squareSize = maxWidth / 8
//        val isBoardFlipped = playerColor == PieceColor.BLACK
//
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            for (row in 0..7) {
//                for (col in 0..7) {
//                    val isLight = (row + col) % 2 == 0
//                    drawRect(
//                        color = if (isLight) Color(0xFFF0D9B5) else Color(0xFFB58863),
//                        topLeft = Offset(col * squareSize.toPx(), row * squareSize.toPx()),
//                        size = Size(squareSize.toPx(), squareSize.toPx())
//                    )
//                }
//            }
//        }
//
//        board.forEachIndexed { row, rowArray ->
//            rowArray.forEachIndexed { col, piece ->
//                val position = Position(row, col)
//                val displayRow = if (isBoardFlipped) 7 - row else row
//                val displayCol = if (isBoardFlipped) 7 - col else col
//
//                Box(
//                    modifier = Modifier
//                        .size(squareSize)
//                        .offset(x = displayCol * squareSize, y = displayRow * squareSize)
//                        .clickable { onSquareClick(position) }
//                ) {
//                    if (selectedPiece == position) {
//                        Box(modifier = Modifier.fillMaxSize().background(Color.Green.copy(alpha = 0.3f)))
//                    } else if (position in validMoves) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(if (piece != null) 0.dp else squareSize * 0.3f)
//                                .background(
//                                    Color.Green.copy(alpha = if (piece != null) 0.5f else 0.3f),
//                                    CircleShape
//                                )
//                        )
//                    }
//                    piece?.let {
//                        ChessPieceView(it, squareSize)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ChessPieceView(piece: ChessPiece, size: Dp) {
//    Canvas(modifier = Modifier.size(size * 0.8f)) {
//        val color = if (piece.color == PieceColor.WHITE) Color.White else Color(0xFF2C2C2C)
//        val outlineColor = if (piece.color == PieceColor.WHITE) Color.Black else Color.White
//        when (piece.type) {
//            PieceType.PAWN -> drawPawn(color, outlineColor)
//            PieceType.ROOK -> drawRook(color, outlineColor)
//            PieceType.KNIGHT -> drawKnight(color, outlineColor)
//            PieceType.BISHOP -> drawBishop(color, outlineColor)
//            PieceType.QUEEN -> drawQueen(color, outlineColor)
//            PieceType.KING -> drawKing(color, outlineColor)
//        }
//    }
//}
//
//@Composable
//private fun GameControls(isMyTurn: Boolean, onResign: () -> Unit, onOfferDraw: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        Button(onClick = onOfferDraw, enabled = isMyTurn) {
//            Icon(Icons.Default.Handshake, contentDescription = "Offer Draw")
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Offer Draw")
//        }
//        Button(
//            onClick = onResign,
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//        ) {
//            Icon(Icons.Default.Flag, contentDescription = "Resign")
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Resign")
//        }
//    }
//}
//
//@Composable
//private fun GameOverOverlay(result: GameResult, playerColor: PieceColor, onDismiss: () -> Unit) {
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) {
//        visible = true
//        delay(4000)
//        onDismiss()
//    }
//
//    val (title, icon, color) = when (result) {
//        is GameResult.Win -> {
//            if (result.winner == playerColor) Triple("You Win!", Icons.Default.EmojiEvents, Color(0xFFFFD700))
//            else Triple("You Lose", Icons.Default.SentimentVeryDissatisfied, Color(0xFFFF5252))
//        }
//        is GameResult.Draw -> Triple("Draw", Icons.Default.Handshake, Color.Gray)
//        else -> Triple("", null, Color.Transparent)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.7f))
//            .clickable(onClick = onDismiss),
//        contentAlignment = Alignment.Center
//    ) {
//        AnimatedVisibility(
//            visible = visible,
//            enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)),
//            exit = scaleOut()
//        ) {
//            Card(
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e))
//            ) {
//                Column(
//                    modifier = Modifier.padding(32.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    if (icon != null) {
//                        Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(64.dp))
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                    Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
//                }
//            }
//        }
//    }
//}















//package `in`.chess.scholars.global.presentation.game
//
//import android.annotation.SuppressLint
//import androidx.compose.animation.*
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.*
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.rotate
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.unit.times
//import androidx.navigation.NavController
//import `in`.chess.scholars.global.domain.model.*
//
//@Composable
//fun GameScreen(
//    gameId: String,
//    betAmount: Float,
//    navController: NavController,
//    viewModel: ChessGameViewModel
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    var showResignDialog by remember { mutableStateOf(false) }
//    var showDrawDialog by remember { mutableStateOf(false) }
//
//    // Initialize the game when the screen is first composed
//    LaunchedEffect(gameId) {
//        viewModel.initializeGame(gameId)
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        // Animated gradient background
//        PremiumGameBackground()
//
//        // Main game content
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(vertical = 8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Opponent info
//            PlayerInfoCard(
//                player = uiState.opponentData,
//                isCurrentTurn = uiState.currentPlayer != uiState.playerColor,
//                timeLeft = "05:00", // TODO: Implement actual timer
//                isOpponent = true
//            )
//
//            // Chess board with premium design
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                PremiumChessBoard(
//                    board = uiState.board,
//                    playerColor = uiState.playerColor,
//                    selectedPiece = uiState.selectedPiece,
//                    validMoves = uiState.validMoves,
//                    lastMove = null, // TODO: Track last move
//                    isCheck = uiState.isCheck,
//                    onSquareClick = viewModel::onSquareClick
//                )
//
//                // Loading overlay
//                if (uiState.isLoading) {
//                    LoadingOverlay()
//                }
//            }
//
//            // Game controls
//            GameControlsBar(
//                isMyTurn = uiState.isMyTurn,
//                onResign = { showResignDialog = true },
//                onOfferDraw = { showDrawDialog = true },
//                onChat = { /* TODO: Implement chat */ }
//            )
//
//            // Current player info
//            PlayerInfoCard(
//                player = null, // Current user
//                isCurrentTurn = uiState.currentPlayer == uiState.playerColor,
//                timeLeft = "05:00", // TODO: Implement actual timer
//                isOpponent = false
//            )
//        }
//
//        // Game status indicators
//        GameStatusIndicators(
//            isCheck = uiState.isCheck,
//            gameResult = uiState.gameResult
//        )
//
//        // Dialogs
//        if (showResignDialog) {
//            ResignConfirmationDialog(
//                onConfirm = {
//                    viewModel.resignGame()
//                    showResignDialog = false
//                },
//                onDismiss = { showResignDialog = false }
//            )
//        }
//
//        if (showDrawDialog) {
//            DrawOfferDialog(
//                onConfirm = {
//                    viewModel.offerDraw()
//                    showDrawDialog = false
//                },
//                onDismiss = { showDrawDialog = false }
//            )
//        }
//
//        // Game over overlay with animation
//        AnimatedVisibility(
//            visible = uiState.gameResult !is GameResult.InProgress,
//            enter = fadeIn() + scaleIn(
//                initialScale = 0.8f,
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioMediumBouncy,
//                    stiffness = Spring.StiffnessLow
//                )
//            ),
//            exit = fadeOut() + scaleOut()
//        ) {
//            GameOverOverlay(
//                result = uiState.gameResult,
//                playerColor = uiState.playerColor,
//                betAmount = betAmount,
//                onDismiss = { navController.popBackStack() }
//            )
//        }
//    }
//}
//
//@Composable
//private fun PremiumGameBackground() {
//    val infiniteTransition = rememberInfiniteTransition()
//
//    val animatedFloat by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(30000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        )
//    )
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        // Dark gradient base
//        drawRect(
//            brush = Brush.radialGradient(
//                colors = listOf(
//                    Color(0xFF0A0A0F),
//                    Color(0xFF1A1A2E),
//                    Color(0xFF0F0F1F)
//                ),
//                center = center,
//                radius = size.minDimension
//            )
//        )
//
//        // Animated geometric patterns
//        val patternCount = 6
//        for (i in 0 until patternCount) {
//            rotate(animatedFloat + i * 60f, pivot = center) {
//                drawPath(
//                    path = Path().apply {
//                        moveTo(center.x, center.y - size.minDimension * 0.4f)
//                        lineTo(center.x + size.minDimension * 0.2f, center.y)
//                        lineTo(center.x, center.y + size.minDimension * 0.4f)
//                        lineTo(center.x - size.minDimension * 0.2f, center.y)
//                        close()
//                    },
//                    color = Color(0xFF4ECDC4).copy(alpha = 0.05f)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun PlayerInfoCard(
//    player: UserData?,
//    isCurrentTurn: Boolean,
//    timeLeft: String,
//    isOpponent: Boolean
//) {
//    val animatedBorderAlpha by animateFloatAsState(
//        targetValue = if (isCurrentTurn) 1f else 0f,
//        animationSpec = tween(300)
//    )
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp)
//            .height(72.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.Black.copy(alpha = 0.3f)
//        ),
//        border = BorderStroke(
//            width = 2.dp,
//            color = Color(0xFF4ECDC4).copy(alpha = animatedBorderAlpha)
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Avatar
//                Box(
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(CircleShape)
//                        .background(
//                            if (isOpponent) {
//                                Brush.linearGradient(
//                                    colors = listOf(
//                                        Color(0xFFFF5252),
//                                        Color(0xFFFF1744)
//                                    )
//                                )
//                            } else {
//                                Brush.linearGradient(
//                                    colors = listOf(
//                                        Color(0xFF4ECDC4),
//                                        Color(0xFF44A3A0)
//                                    )
//                                )
//                            }
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    if (player != null) {
//                        Text(
//                            player.displayName.firstOrNull()?.toString() ?: "?",
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 20.sp
//                        )
//                    } else {
//                        Icon(
//                            Icons.Default.Person,
//                            contentDescription = "You",
//                            tint = Color.White,
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }
//                }
//
//                Column {
//                    Text(
//                        player?.displayName ?: "You",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 16.sp
//                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Star,
//                            contentDescription = "Rating",
//                            tint = Color(0xFFFFD700),
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Text(
//                            "${player?.rating ?: "..."}",
//                            color = Color.White.copy(alpha = 0.7f),
//                            fontSize = 14.sp
//                        )
//                    }
//                }
//            }
//
//            // Timer
//            TimerDisplay(
//                timeLeft = timeLeft,
//                isActive = isCurrentTurn
//            )
//        }
//    }
//}
//
//@Composable
//private fun TimerDisplay(timeLeft: String, isActive: Boolean) {
//    val animatedScale by animateFloatAsState(
//        targetValue = if (isActive) 1.1f else 1f,
//        animationSpec = if (isActive) {
//            infiniteRepeatable(
//                animation = tween(1000),
//                repeatMode = RepeatMode.Reverse
//            )
//        } else {
//            snap()
//        }
//    )
//
//    Box(
//        modifier = Modifier
//            .scale(animatedScale)
//            .background(
//                color = if (isActive) Color(0xFF4ECDC4).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
//                shape = RoundedCornerShape(8.dp)
//            )
//            .padding(horizontal = 12.dp, vertical = 6.dp)
//    ) {
//        Text(
//            timeLeft,
//            color = if (isActive) Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f),
//            fontWeight = FontWeight.Bold,
//            fontSize = 16.sp
//        )
//    }
//}
//
//@SuppressLint("UnusedBoxWithConstraintsScope")
//@Composable
//private fun PremiumChessBoard(
//    board: Array<Array<ChessPiece?>>,
//    playerColor: PieceColor,
//    selectedPiece: Position?,
//    validMoves: Set<Position>,
//    lastMove: Pair<Position, Position>?,
//    isCheck: Boolean,
//    onSquareClick: (Position) -> Unit
//) {
//    BoxWithConstraints(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f)
//            .clip(RoundedCornerShape(16.dp))
//    ) {
//        val squareSize = maxWidth / 8
//        val isBoardFlipped = playerColor == PieceColor.BLACK
//
//        // Board background
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            // Board shadow
//            drawRoundRect(
//                color = Color.Black.copy(alpha = 0.5f),
//                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
//                size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
//                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
//            )
//
//            // Board frame
//            drawRoundRect(
//                brush = Brush.linearGradient(
//                    colors = listOf(
//                        Color(0xFF8B7355),
//                        Color(0xFF654321),
//                        Color(0xFF8B7355)
//                    )
//                ),
//                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
//            )
//
//            // Inner board
//            drawRoundRect(
//                color = Color(0xFF3E2723),
//                topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
//                size = Size(size.width - 16.dp.toPx(), size.height - 16.dp.toPx()),
//                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
//            )
//        }
//
//        // Chess squares and pieces
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp)
//                .clip(RoundedCornerShape(12.dp))
//        ) {
//            // Board squares
//            Canvas(modifier = Modifier.fillMaxSize()) {
//                for (row in 0..7) {
//                    for (col in 0..7) {
//                        val isLight = (row + col) % 2 == 0
//                        val squareColor = when {
//                            selectedPiece == Position(row, col) -> {
//                                if (isLight) Color(0xFF7BC862) else Color(0xFF649B54)
//                            }
//                            lastMove?.first == Position(row, col) || lastMove?.second == Position(row, col) -> {
//                                if (isLight) Color(0xFFF6F669).copy(alpha = 0.5f) else Color(0xFFBBCA2B).copy(alpha = 0.5f)
//                            }
//                            else -> {
//                                if (isLight) Color(0xFFEFEFD5) else Color(0xFF769656)
//                            }
//                        }
//
//                        drawRect(
//                            color = squareColor,
//                            topLeft = Offset(col * squareSize.toPx(), row * squareSize.toPx()),
//                            size = Size(squareSize.toPx(), squareSize.toPx())
//                        )
//                    }
//                }
//            }
//
//            // Pieces and interactive elements
//            board.forEachIndexed { row, rowArray ->
//                rowArray.forEachIndexed { col, piece ->
//                    val position = Position(row, col)
//                    val displayRow = if (isBoardFlipped) 7 - row else row
//                    val displayCol = if (isBoardFlipped) 7 - col else col
//
//                    Box(
//                        modifier = Modifier
//                            .size(squareSize)
//                            .offset(x = displayCol * squareSize, y = displayRow * squareSize)
//                            .clickable { onSquareClick(position) }
//                    ) {
//                        // Valid move indicators
//                        if (position in validMoves) {
//                            ValidMoveIndicator(
//                                haspiece = piece != null,
//                                squareSize = squareSize
//                            )
//                        }
//
//                        // Chess piece
//                        piece?.let {
//                            PremiumChessPiece(
//                                piece = it,
//                                size = squareSize,
//                                isSelected = selectedPiece == position
//                            )
//                        }
//
//                        // Check indicator
//                        if (isCheck && piece?.type == PieceType.KING && piece.color == playerColor) {
//                            CheckIndicator()
//                        }
//                    }
//                }
//            }
//
//            // File and rank labels
//            FileRankLabels(squareSize, isBoardFlipped)
//        }
//    }
//}
//
//@Composable
//private fun ValidMoveIndicator(haspiece: Boolean, squareSize: Dp) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        if (haspiece) {
//            // Capture indicator
//            Canvas(modifier = Modifier.fillMaxSize()) {
//                drawCircle(
//                    color = Color.Black.copy(alpha = 0.2f),
//                    radius = size.minDimension / 2,
//                    style = Stroke(width = 4.dp.toPx())
//                )
//            }
//        } else {
//            // Move indicator
//            Canvas(modifier = Modifier.size(squareSize * 0.3f)) {
//                drawCircle(
//                    color = Color.Black.copy(alpha = 0.3f),
//                    radius = size.minDimension / 2
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun PremiumChessPiece(
//    piece: ChessPiece,
//    size: Dp,
//    isSelected: Boolean
//) {
//    val animatedScale by animateFloatAsState(
//        targetValue = if (isSelected) 1.1f else 1f,
//        animationSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        )
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .scale(animatedScale),
//        contentAlignment = Alignment.Center
//    ) {
//        Canvas(
//            modifier = Modifier.size(size * 0.85f)
//        ) {
//            val pieceColor = if (piece.color == PieceColor.WHITE) {
//                Brush.linearGradient(
//                    colors = listOf(
//                        Color(0xFFFAFAFA),
//                        Color(0xFFE0E0E0),
//                        Color(0xFFFAFAFA)
//                    )
//                )
//            } else {
//                Brush.linearGradient(
//                    colors = listOf(
//                        Color(0xFF424242),
//                        Color(0xFF212121),
//                        Color(0xFF424242)
//                    )
//                )
//            }
//
//            val outlineColor = if (piece.color == PieceColor.WHITE) {
//                Color(0xFF616161)
//            } else {
//                Color(0xFF9E9E9E)
//            }
//
//            // Shadow
//            drawCircle(
//                color = Color.Black.copy(alpha = 0.3f),
//                radius = size.toPx() * 0.35f,
//                center = center + Offset(2.dp.toPx(), 4.dp.toPx())
//            )
//
//            when (piece.type) {
//                PieceType.PAWN -> drawPawn(pieceColor, outlineColor)
//                PieceType.ROOK -> drawRook(pieceColor, outlineColor)
//                PieceType.KNIGHT -> drawKnight(pieceColor, outlineColor)
//                PieceType.BISHOP -> drawBishop(pieceColor, outlineColor)
//                PieceType.QUEEN -> drawQueen(pieceColor, outlineColor)
//                PieceType.KING -> drawKing(pieceColor, outlineColor)
//            }
//        }
//    }
//}

























package `in`.chess.scholars.global.presentation.game

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import kotlin.math.*

@Composable
fun GameScreen(
    gameId: String,
    betAmount: Float,
    navController: NavController,
    viewModel: ChessGameViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResignDialog by remember { mutableStateOf(false) }
    var showDrawDialog by remember { mutableStateOf(false) }

    // Initialize the game when the screen is first composed
    LaunchedEffect(gameId) {
        viewModel.initializeGame(gameId)
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
                timeLeft = "05:00", // TODO: Implement actual timer
                isOpponent = true
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
                    lastMove = null, // TODO: Track last move
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
                onResign = { showResignDialog = true },
                onOfferDraw = { showDrawDialog = true },
                onChat = { /* TODO: Implement chat */ }
            )

            // Current player info
            PlayerInfoCard(
                player = null, // Current user
                isCurrentTurn = uiState.currentPlayer == uiState.playerColor,
                timeLeft = "05:00", // TODO: Implement actual timer
                isOpponent = false
            )
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
                betAmount = betAmount,
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
    timeLeft: String,
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
                timeLeft = timeLeft,
                isActive = isCurrentTurn
            )
        }
    }
}

@Composable
private fun TimerDisplay(timeLeft: String, isActive: Boolean) {
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

    Box(
        modifier = Modifier
            .scale(animatedScale)
            .background(
                color = if (isActive) Color(0xFF4ECDC4).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            timeLeft,
            color = if (isActive) Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f),
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
            // Board shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )

            // Board frame
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

            // Inner board
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
                        // Valid move indicators
                        if (position in validMoves) {
                            ValidMoveIndicator(
                                haspiece = piece != null,
                                squareSize = squareSize
                            )
                        }

                        // Chess piece
                        piece?.let {
                            PremiumChessPiece(
                                piece = it,
                                size = squareSize,
                                isSelected = selectedPiece == position
                            )
                        }

                        // Check indicator
                        if (isCheck && piece?.type == PieceType.KING && piece.color == playerColor) {
                            CheckIndicator()
                        }
                    }
                }
            }

            // File and rank labels
            FileRankLabels(squareSize, isBoardFlipped)
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