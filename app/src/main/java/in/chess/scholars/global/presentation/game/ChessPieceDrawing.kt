package `in`.chess.scholars.global.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.chess.scholars.global.domain.model.GameResult
import `in`.chess.scholars.global.domain.model.PieceColor
import `in`.chess.scholars.global.domain.model.PieceType
import androidx.compose.ui.geometry.minDimension
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.times
import `in`.chess.scholars.global.domain.model.ChatMessage
import `in`.chess.scholars.global.domain.model.ChessPiece
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Enhanced chess piece drawing with proper path handling and premium aesthetics
 */

fun DrawScope.drawChessPiece(
    pieceType: PieceType,
    pieceColor: PieceColor,
    size: Float = this.size.minDimension * 0.8f
) {
    val color = if (pieceColor == PieceColor.WHITE) Color.White else Color(0xFF2C2C2C)
    val outlineColor = if (pieceColor == PieceColor.WHITE) Color(0xFF616161) else Color(0xFF9E9E9E)

    // Draw shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = size * 0.35f,
        center = center + Offset(2f, 4f)
    )

    when (pieceType) {
        PieceType.PAWN -> drawPawn(color, outlineColor)
        PieceType.ROOK -> drawRook(color, outlineColor)
        PieceType.KNIGHT -> drawKnight(color, outlineColor)
        PieceType.BISHOP -> drawBishop(color, outlineColor)
        PieceType.QUEEN -> drawQueen(color, outlineColor)
        PieceType.KING -> drawKing(color, outlineColor)
    }
}

private fun DrawScope.drawPawn(fillColor: Color, strokeColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.05f

    val path = Path().apply {
        moveTo(w * 0.3f, h * 0.9f)
        lineTo(w * 0.7f, h * 0.9f)
        arcTo(Rect(w * 0.2f, h * 0.8f, w * 0.8f, h * 1.0f), 0f, 180f, false)
        close()

        moveTo(w * 0.4f, h * 0.8f)
        lineTo(w * 0.4f, h * 0.6f)
        lineTo(w * 0.6f, h * 0.6f)
        lineTo(w * 0.6f, h * 0.8f)
        close()
    }

    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = Stroke(strokeWidth))

    drawCircle(fillColor, w * 0.2f, center = Offset(w * 0.5f, h * 0.4f))
    drawCircle(strokeColor, w * 0.2f, center = Offset(w * 0.5f, h * 0.4f), style = Stroke(strokeWidth))
}

private fun DrawScope.drawRook(fillColor: Color, strokeColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.05f

    val path = Path().apply {
        moveTo(w * 0.2f, h * 0.9f)
        lineTo(w * 0.8f, h * 0.9f)
        lineTo(w * 0.8f, h * 0.8f)
        lineTo(w * 0.7f, h * 0.8f)
        lineTo(w * 0.7f, h * 0.35f)
        lineTo(w * 0.8f, h * 0.25f)
        lineTo(w * 0.8f, h * 0.1f)
        lineTo(w * 0.65f, h * 0.1f)
        lineTo(w * 0.65f, h * 0.2f)
        lineTo(w * 0.55f, h * 0.2f)
        lineTo(w * 0.55f, h * 0.1f)
        lineTo(w * 0.45f, h * 0.1f)
        lineTo(w * 0.45f, h * 0.2f)
        lineTo(w * 0.35f, h * 0.2f)
        lineTo(w * 0.35f, h * 0.1f)
        lineTo(w * 0.2f, h * 0.1f)
        lineTo(w * 0.2f, h * 0.25f)
        lineTo(w * 0.3f, h * 0.35f)
        lineTo(w * 0.3f, h * 0.8f)
        lineTo(w * 0.2f, h * 0.8f)
        close()
    }
    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawKnight(fillColor: Color, strokeColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.05f

    val path = Path().apply {
        moveTo(w * 0.2f, h * 0.9f)
        lineTo(w * 0.8f, h * 0.9f)
        lineTo(w * 0.75f, h * 0.7f)
        cubicTo(w * 0.8f, h * 0.5f, w * 0.6f, h * 0.5f, w * 0.55f, h * 0.4f)
        lineTo(w * 0.75f, h * 0.2f)
        quadraticBezierTo(w * 0.8f, h * 0.1f, w * 0.7f, h * 0.1f)
        quadraticBezierTo(w * 0.6f, h * 0.15f, w * 0.5f, h * 0.25f)
        lineTo(w * 0.4f, h * 0.15f)
        lineTo(w * 0.3f, h * 0.3f)
        lineTo(w * 0.25f, h * 0.2f)
        lineTo(w * 0.2f, h * 0.4f)
        cubicTo(w * 0.3f, h * 0.6f, w * 0.2f, h * 0.7f, w * 0.25f, h * 0.7f)
        close()
    }
    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawBishop(fillColor: Color, strokeColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.05f

    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.1f) // Top point
        quadraticBezierTo(w * 0.8f, h * 0.4f, w * 0.7f, h * 0.8f)
        lineTo(w * 0.75f, h * 0.9f)
        lineTo(w * 0.25f, h * 0.9f)
        lineTo(w * 0.3f, h * 0.8f)
        quadraticBezierTo(w * 0.2f, h * 0.4f, w * 0.5f, h * 0.1f)
        close()
    }
    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = Stroke(width = strokeWidth))

    // Mitre cleft
    drawLine(
        color = strokeColor,
        start = Offset(w * 0.5f, h * 0.15f),
        end = Offset(w * 0.5f, h * 0.3f),
        strokeWidth = strokeWidth / 2
    )
}

private fun DrawScope.drawQueen(fillColor: Color, strokeColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.05f

    val path = Path().apply {
        moveTo(w * 0.2f, h * 0.9f)
        lineTo(w * 0.8f, h * 0.9f)
        lineTo(w * 0.75f, h * 0.8f)
        cubicTo(w * 0.9f, h * 0.6f, w * 0.6f, h * 0.5f, w * 0.6f, h * 0.4f)
        lineTo(w * 0.4f, h * 0.4f)
        cubicTo(w * 0.4f, h * 0.5f, w * 0.1f, h * 0.6f, w * 0.25f, h * 0.8f)
        close()
    }
    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = Stroke(width = strokeWidth))

    val crownPath = Path().apply {
        moveTo(w * 0.4f, h * 0.4f)
        lineTo(w * 0.3f, h * 0.2f)
        lineTo(w * 0.4f, h * 0.3f)
        lineTo(w * 0.5f, h * 0.15f)
        lineTo(w * 0.6f, h * 0.3f)
        lineTo(w * 0.7f, h * 0.2f)
        lineTo(w * 0.6f, h * 0.4f)
        close()
    }
    drawPath(crownPath, fillColor)
    drawPath(crownPath, strokeColor, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawKing(fillColor: Color, strokeColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.05f

    val path = Path().apply {
        moveTo(w * 0.25f, h * 0.95f)
        lineTo(w * 0.75f, h * 0.95f)
        lineTo(w * 0.7f, h * 0.85f)
        quadraticBezierTo(w * 0.5f, h * 0.75f, w * 0.6f, h * 0.5f)
        lineTo(w * 0.6f, h * 0.45f)
        lineTo(w * 0.4f, h * 0.45f)
        lineTo(w * 0.4f, h * 0.5f)
        quadraticBezierTo(w * 0.5f, h * 0.75f, w * 0.3f, h * 0.85f)
        close()
    }
    drawPath(path, fillColor)
    drawPath(path, strokeColor, style = Stroke(width = strokeWidth))

    val crownPath = Path().apply {
        moveTo(w * 0.4f, h * 0.45f)
        lineTo(w * 0.3f, h * 0.2f)
        lineTo(w * 0.5f, h * 0.3f)
        lineTo(w * 0.7f, h * 0.2f)
        lineTo(w * 0.6f, h * 0.45f)
        close()
    }
    drawPath(crownPath, fillColor)
    drawPath(crownPath, strokeColor, style = Stroke(width = strokeWidth))

    // Cross on top
    drawLine(strokeColor, Offset(w * 0.5f, h * 0.05f), Offset(w * 0.5f, h * 0.2f), strokeWidth)
    drawLine(strokeColor, Offset(w * 0.4f, h * 0.12f), Offset(w * 0.6f, h * 0.12f), strokeWidth)
}
@Composable
fun CheckIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color(0xFFFF5252).copy(alpha = animatedAlpha),
            radius = size.minDimension / 2,
            style = Stroke(width = 4.dp.toPx())
        )
    }
}

@Composable
fun FileRankLabels(squareSize: Dp, isBoardFlipped: Boolean) {
    val files = if (isBoardFlipped) listOf("h", "g", "f", "e", "d", "c", "b", "a") else listOf("a", "b", "c", "d", "e", "f", "g", "h")
    val ranks = if (isBoardFlipped) listOf("1", "2", "3", "4", "5", "6", "7", "8") else listOf("8", "7", "6", "5", "4", "3", "2", "1")
    // File labels (bottom)
    Row(modifier = Modifier.fillMaxWidth().offset(y = 8 * squareSize)) {
        files.forEach { file ->
            Box(modifier = Modifier.width(squareSize), contentAlignment = Alignment.Center) {
                Text(file, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }
    }
    // Rank labels (left)
    Column(modifier = Modifier.fillMaxHeight().offset(x = (-20).dp)) {
        ranks.forEach { rank ->
            Box(modifier = Modifier.height(squareSize), contentAlignment = Alignment.Center) {
                Text(rank, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun GameControlsBar(
    isMyTurn: Boolean,
    hasUnreadMessages: Boolean,
    onResign: () -> Unit,
    onOfferDraw: () -> Unit,
    onChat: () -> Unit,
    onInfoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgedBox(
                badge = {
                    if (hasUnreadMessages) {
                        Badge(
                            modifier = Modifier.offset(x = (-8).dp, y = 6.dp),
                            containerColor = Color(0xFFFF5252)
                        )
                    }
                }
            ) {
                GameControlButton(
                    icon = Icons.Default.ChatBubble,
                    label = "Chat",
                    onClick = onChat,
                    tint = Color(0xFF4ECDC4)
                )
            }

            GameControlButton(
                icon = Icons.Default.Handshake,
                label = "Draw",
                onClick = onOfferDraw,
                enabled = isMyTurn,
                tint = Color(0xFFFFD700)
            )

            GameControlButton(
                icon = Icons.Default.Flag,
                label = "Resign",
                onClick = onResign,
                tint = Color(0xFFFF5252)
            )
            GameControlButton(icon = Icons.Default.Info, label = "Info", onClick = onInfoClick, tint = Color(0xFF2196F3))
        }
    }
}
// For the captured pieces
@Composable
fun CapturedPiecesRow(pieces: List<ChessPiece>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp) // Ensure a consistent height
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (pieces.isEmpty()) {
            Text(
                text = "No pieces captured",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.4f)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(pieces) { piece ->
                    Canvas(modifier = Modifier.size(20.dp)) {
                        drawChessPiece(pieceType = piece.type, pieceColor = piece.color)
                    }
                }
            }
        }
    }
}

// For the bottom sheet content
@Composable
fun GameInfoSheetContent(prizeInfo: PrizeInfo) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxWidth()) {
        item { Text("Game Information", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp)) }
        item { InfoRow("Bet Amount", prizeInfo.betAmount) }
        item { InfoRow("Prize Pool (2x)", prizeInfo.prizePool) }
        item { InfoRow("Platform Fee (10%)", prizeInfo.platformFee, isDeduction = true) }
        item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
        item { InfoRow("Net Winnings", prizeInfo.taxableAmount) }
        item { InfoRow("TDS (30% of Winnings)", prizeInfo.tdsDeducted, isDeduction = true) }
        item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
        item { InfoRow("Final Payout to Winner", prizeInfo.winningsPayable, isHighlight = true) }
    }
}

@Composable
fun InfoRow(label: String, amount: Float, isDeduction: Boolean = false, isHighlight: Boolean = false) {
    // Format the numeric amount into a currency string with the "₹" symbol.
    val formattedAmount = "₹${"%.2f".format(amount)}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (isHighlight) Color.White else Color.Gray)
        Text(
            // The sign is determined by the `isDeduction` flag, separate from the value itself.
            text = if (isDeduction) "- $formattedAmount" else formattedAmount,
            color = when {
                isHighlight -> Color(0xFF4CAF50)
                isDeduction -> Color(0xFFFF5252)
                else -> Color.White
            },
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun GameControlButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    tint: Color
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.3f
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(12.dp)
            .alpha(animatedAlpha)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun GameStatusIndicators(
    isCheck: Boolean,
    gameResult: GameResult
) {
    AnimatedVisibility(
        visible = isCheck && gameResult is GameResult.InProgress,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF5252).copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Check",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "CHECK!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF4ECDC4),
                    strokeWidth = 2.dp
                )
                Text(
                    "Loading game...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun GameOverOverlay(
    result: GameResult,
    playerColor: PieceColor,
    prizeInfo: PrizeInfo, // Changed from betAmount to prizeInfo
    onDismiss: () -> Unit
) {
    val (title, subtitle, icon, color) = when (result) {
        is GameResult.Win -> {
            if (result.winner == playerColor) {
                val payout = "%.2f".format(prizeInfo.winningsPayable)
                Quadruple(
                    "Victory!",
                    "You won ₹${payout}",
                    Icons.Default.EmojiEvents,
                    Color(0xFFFFD700)
                )
            } else {
                Quadruple(
                    "Defeat",
                    "Better luck next time",
                    Icons.Default.SentimentVeryDissatisfied,
                    Color(0xFFFF5252)
                )
            }
        }
        is GameResult.Draw -> {
            Quadruple(
                "Draw",
                "A fair battle",
                Icons.Default.Handshake,
                Color(0xFFFF9800)
            )
        }
        else -> Quadruple("", "", null, Color.Transparent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1a1a2e)
            ),
            border = BorderStroke(2.dp, color)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Text(
                    title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Text(
                    subtitle,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    )
                ) {
                    Text(
                        "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResignConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                    Icons.Default.Flag,
                    contentDescription = "Resign",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Resign Game?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Are you sure you want to resign? This action cannot be undone.",
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
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5252)
                        )
                    ) {
                        Text("Resign")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawOfferDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                    contentDescription = "Draw",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Offer Draw?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Send a draw offer to your opponent?",
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
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700)
                        )
                    ) {
                        Text("Offer Draw", color = Color.Black)
                    }
                }
            }
        }
    }
}

// --- NEW CHAT UI COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSheetContent(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    currentUserId: String
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // Header
        Text(
            "In-Game Chat",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatMessageItem(
                    message = msg,
                    isSentByCurrentUser = msg.userId == currentUserId
                )
            }
        }

        // Message input field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp // Add elevation to the input bar
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        onSendMessage(messageText)
                        messageText = ""
                        keyboardController?.hide()
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onSendMessage(messageText)
                        messageText = ""
                        keyboardController?.hide()
                    },
                    enabled = messageText.isNotBlank(),
                    shape = CircleShape,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, isSentByCurrentUser: Boolean) {
    val alignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val bubbleShape = if (isSentByCurrentUser) {
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    }
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }


    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(
            horizontalAlignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = if (isSentByCurrentUser) "You" else message.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
            )
            Box(
                modifier = Modifier
                    .background(color = backgroundColor, shape = bubbleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = message.message, color = textColor)
            }
            message.timestamp?.toDate()?.let {
                Text(
                    text = dateFormat.format(it),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}

// Helper data class
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)