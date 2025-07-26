package `in`.chess.scholars.global.presentation.game

//
///**
// * Draws a custom Rook chess piece.
// */
//fun DrawScope.drawRook(color: Color, outlineColor: Color) {
//    val strokeWidth = size.minDimension * 0.05f
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//        moveTo(w * 0.2f, h * 0.9f)
//        lineTo(w * 0.8f, h * 0.9f)
//        lineTo(w * 0.8f, h * 0.8f)
//        lineTo(w * 0.7f, h * 0.8f)
//        lineTo(w * 0.7f, h * 0.35f)
//        lineTo(w * 0.8f, h * 0.25f)
//        lineTo(w * 0.8f, h * 0.1f)
//        lineTo(w * 0.65f, h * 0.1f)
//        lineTo(w * 0.65f, h * 0.2f)
//        lineTo(w * 0.55f, h * 0.2f)
//        lineTo(w * 0.55f, h * 0.1f)
//        lineTo(w * 0.45f, h * 0.1f)
//        lineTo(w * 0.45f, h * 0.2f)
//        lineTo(w * 0.35f, h * 0.2f)
//        lineTo(w * 0.35f, h * 0.1f)
//        lineTo(w * 0.2f, h * 0.1f)
//        lineTo(w * 0.2f, h * 0.25f)
//        lineTo(w * 0.3f, h * 0.35f)
//        lineTo(w * 0.3f, h * 0.8f)
//        lineTo(w * 0.2f, h * 0.8f)
//        close()
//    }
//    drawPath(path, color)
//    drawPath(path, outlineColor, style = Stroke(width = strokeWidth))
//}
//
///**
// * Draws a custom Bishop chess piece.
// */
//fun DrawScope.drawBishop(color: Color, outlineColor: Color) {
//    val strokeWidth = size.minDimension * 0.05f
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//        moveTo(w * 0.5f, h * 0.1f) // Top point
//        quadraticBezierTo(w * 0.8f, h * 0.4f, w * 0.7f, h * 0.8f)
//        lineTo(w * 0.75f, h * 0.9f)
//        lineTo(w * 0.25f, h * 0.9f)
//        lineTo(w * 0.3f, h * 0.8f)
//        quadraticBezierTo(w * 0.2f, h * 0.4f, w * 0.5f, h * 0.1f)
//        close()
//    }
//    drawPath(path, color)
//    drawPath(path, outlineColor, style = Stroke(width = strokeWidth))
//
//    // Mitre cleft
//    drawLine(
//        color = outlineColor,
//        start = Offset(size.width * 0.5f, size.height * 0.15f),
//        end = Offset(size.width * 0.5f, size.height * 0.3f),
//        strokeWidth = strokeWidth / 2
//    )
//}
//
///**
// * Draws a custom Knight chess piece.
// */
//fun DrawScope.drawKnight(color: Color, outlineColor: Color) {
//    val strokeWidth = size.minDimension * 0.05f
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//        moveTo(w * 0.2f, h * 0.9f)
//        lineTo(w * 0.8f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.7f)
//        cubicTo(w * 0.8f, h * 0.5f, w * 0.6f, h * 0.5f, w * 0.55f, h * 0.4f)
//        lineTo(w * 0.75f, h * 0.2f)
//        quadraticBezierTo(w * 0.8f, h * 0.1f, w * 0.7f, h * 0.1f)
//        quadraticBezierTo(w * 0.6f, h * 0.15f, w * 0.5f, h * 0.25f)
//        lineTo(w * 0.4f, h * 0.15f)
//        lineTo(w * 0.3f, h * 0.3f)
//        lineTo(w * 0.25f, h * 0.2f)
//        lineTo(w * 0.2f, h * 0.4f)
//        cubicTo(w * 0.3f, h * 0.6f, w * 0.2f, h * 0.7f, w * 0.25f, h * 0.7f)
//        close()
//    }
//    drawPath(path, color)
//    drawPath(path, outlineColor, style = Stroke(width = strokeWidth))
//}
//
///**
// * Draws a custom King chess piece.
// */
//fun DrawScope.drawKing(color: Color, outlineColor: Color) {
//    val strokeWidth = size.minDimension * 0.05f
//    val w = size.width
//    val h = size.height
//
//    val path = Path().apply {
//        moveTo(w * 0.25f, h * 0.95f)
//        lineTo(w * 0.75f, h * 0.95f)
//        lineTo(w * 0.7f, h * 0.85f)
//        quadraticBezierTo(w * 0.5f, h * 0.75f, w * 0.6f, h * 0.5f)
//        lineTo(w * 0.6f, h * 0.45f)
//        lineTo(w * 0.4f, h * 0.45f)
//        lineTo(w * 0.4f, h * 0.5f)
//        quadraticBezierTo(w * 0.5f, h * 0.75f, w * 0.3f, h * 0.85f)
//        close()
//    }
//    drawPath(path, color)
//    drawPath(path, outlineColor, style = Stroke(width = strokeWidth))
//
//    val crownPath = Path().apply {
//        moveTo(w * 0.4f, h * 0.45f)
//        lineTo(w * 0.3f, h * 0.2f)
//        lineTo(w * 0.5f, h * 0.3f)
//        lineTo(w * 0.7f, h * 0.2f)
//        lineTo(w * 0.6f, h * 0.45f)
//        close()
//    }
//    drawPath(crownPath, color)
//    drawPath(crownPath, outlineColor, style = Stroke(width = strokeWidth))
//
//    // Cross on top
//    drawLine(outlineColor, Offset(w * 0.5f, h * 0.05f), Offset(w * 0.5f, h * 0.2f), strokeWidth)
//    drawLine(outlineColor, Offset(w * 0.4f, h * 0.12f), Offset(w * 0.6f, h * 0.12f), strokeWidth)
//}
//
///**
// * Draws a custom Queen chess piece.
// */
//fun DrawScope.drawQueen(color: Color, outlineColor: Color) {
//    val strokeWidth = size.minDimension * 0.05f
//    val w = size.width
//    val h = size.height
//
//    val path = Path().apply {
//        moveTo(w * 0.2f, h * 0.9f)
//        lineTo(w * 0.8f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.8f)
//        cubicTo(w * 0.9f, h * 0.6f, w * 0.6f, h * 0.5f, w * 0.6f, h * 0.4f)
//        lineTo(w * 0.4f, h * 0.4f)
//        cubicTo(w * 0.4f, h * 0.5f, w * 0.1f, h * 0.6f, w * 0.25f, h * 0.8f)
//        close()
//    }
//    drawPath(path, color)
//    drawPath(path, outlineColor, style = Stroke(width = strokeWidth))
//
//    val crownPath = Path().apply {
//        moveTo(w * 0.4f, h * 0.4f)
//        lineTo(w * 0.3f, h * 0.2f)
//        lineTo(w * 0.4f, h * 0.3f)
//        lineTo(w * 0.5f, h * 0.15f)
//        lineTo(w * 0.6f, h * 0.3f)
//        lineTo(w * 0.7f, h * 0.2f)
//        lineTo(w * 0.6f, h * 0.4f)
//        close()
//    }
//    drawPath(crownPath, color)
//    drawPath(crownPath, outlineColor, style = Stroke(width = strokeWidth))
//}
//
///**
// * Draws a custom Pawn chess piece.
// */
//fun DrawScope.drawPawn(color: Color, outlineColor: Color) {
//    val strokeWidth = size.minDimension * 0.05f
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//        moveTo(w * 0.25f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.9f)
//        lineTo(w * 0.7f, h * 0.8f)
//        lineTo(w * 0.65f, h * 0.6f)
//        lineTo(w * 0.35f, h * 0.6f)
//        lineTo(w * 0.3f, h * 0.8f)
//        close()
//    }
//    drawPath(path, color)
//    drawPath(path, outlineColor, style = Stroke(width = strokeWidth))
//
//    drawCircle(
//        color = color,
//        radius = size.minDimension * 0.2f,
//        center = Offset(center.x, size.height * 0.4f)
//    )
//    drawCircle(
//        color = outlineColor,
//        radius = size.minDimension * 0.2f,
//        center = Offset(center.x, size.height * 0.4f),
//        style = Stroke(width = strokeWidth)
//    )
//}











//fun DrawScope.drawPawn(brush: Brush, outlineColor: Color) {
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//
//        // Base
//        moveTo(w * 0.3f, h * 0.9f)
//        lineTo(w * 0.7f, h * 0.9f)
//        lineTo(w * 0.65f, h * 0.75f)
//        lineTo(w * 0.6f, h * 0.6f)
//        lineTo(w * 0.4f, h * 0.6f)
//        lineTo(w * 0.35f, h * 0.75f)
//        close()
//
//        // Head
//        moveTo(w * 0.5f, h * 0.15f)
//        cubicTo(
//            w * 0.7f, h * 0.15f,
//            w * 0.75f, h * 0.35f,
//            w * 0.6f, h * 0.45f
//        )
//        lineTo(w * 0.4f, h * 0.45f)
//        cubicTo(
//            w * 0.25f, h * 0.35f,
//            w * 0.3f, h * 0.15f,
//            w * 0.5f, h * 0.15f
//        )
//    }
//
//    drawPath(path, brush)
//    drawPath(path, outlineColor, style = Stroke(width = size.minDimension * 0.03f))
//}
//
//fun DrawScope.drawRook(brush: Brush, outlineColor: Color) {
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//
//        // Base
//        moveTo(w * 0.25f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.9f)
//        lineTo(w * 0.7f, h * 0.8f)
//        lineTo(w * 0.65f, h * 0.75f)
//        lineTo(w * 0.65f, h * 0.35f)
//        lineTo(w * 0.35f, h * 0.35f)
//        lineTo(w * 0.35f, h * 0.75f)
//        lineTo(w * 0.3f, h * 0.8f)
//        close()
//
//        // Battlements
//        moveTo(w * 0.35f, h * 0.35f)
//        lineTo(w * 0.35f, h * 0.15f)
//        lineTo(w * 0.25f, h * 0.15f)
//        lineTo(w * 0.25f, h * 0.25f)
//        lineTo(w * 0.35f, h * 0.25f)
//
//        moveTo(w * 0.45f, h * 0.25f)
//        lineTo(w * 0.45f, h * 0.15f)
//        lineTo(w * 0.55f, h * 0.15f)
//        lineTo(w * 0.55f, h * 0.25f)
//
//        moveTo(w * 0.65f, h * 0.25f)
//        lineTo(w * 0.75f, h * 0.25f)
//        lineTo(w * 0.75f, h * 0.15f)
//        lineTo(w * 0.65f, h * 0.15f)
//        lineTo(w * 0.65f, h * 0.35f)
//    }
//
//    drawPath(path, brush)
//    drawPath(path, outlineColor, style = Stroke(width = size.minDimension * 0.03f))
//}
//
//fun DrawScope.drawKnight(brush: Brush, outlineColor: Color) {
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//
//        moveTo(w * 0.25f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.9f)
//        lineTo(w * 0.7f, h * 0.75f)
//        cubicTo(
//            w * 0.75f, h * 0.55f,
//            w * 0.65f, h * 0.45f,
//            w * 0.6f, h * 0.4f
//        )
//        lineTo(w * 0.75f, h * 0.25f)
//        cubicTo(
//            w * 0.8f, h * 0.15f,
//            w * 0.7f, h * 0.1f,
//            w * 0.6f, h * 0.15f
//        )
//        cubicTo(
//            w * 0.5f, h * 0.2f,
//            w * 0.45f, h * 0.25f,
//            w * 0.4f, h * 0.3f
//        )
//        lineTo(w * 0.35f, h * 0.25f)
//        lineTo(w * 0.3f, h * 0.35f)
//        lineTo(w * 0.25f, h * 0.3f)
//        lineTo(w * 0.2f, h * 0.45f)
//        cubicTo(
//            w * 0.25f, h * 0.6f,
//            w * 0.25f, h * 0.75f,
//            w * 0.3f, h * 0.75f
//        )
//        close()
//    }
//
//    drawPath(path, brush)
//    drawPath(path, outlineColor, style = Stroke(width = size.minDimension * 0.03f))
//
//    // Eye
//    drawCircle(
//        color = outlineColor,
//        radius = size.minDimension * 0.03f,
//        center = Offset(size.width * 0.45f, size.height * 0.35f)
//    )
//}
//
//fun DrawScope.drawBishop(brush: Brush, outlineColor: Color) {
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//
//        // Base
//        moveTo(w * 0.25f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.9f)
//        lineTo(w * 0.7f, h * 0.8f)
//
//        // Body
//        cubicTo(
//            w * 0.75f, h * 0.6f,
//            w * 0.65f, h * 0.4f,
//            w * 0.55f, h * 0.25f
//        )
//
//        // Top
//        lineTo(w * 0.55f, h * 0.15f)
//        cubicTo(
//            w * 0.55f, h * 0.1f,
//            w * 0.45f, h * 0.1f,
//            w * 0.45f, h * 0.15f
//        )
//        lineTo(w * 0.45f, h * 0.25f)
//
//        cubicTo(
//            w * 0.35f, h * 0.4f,
//            w * 0.25f, h * 0.6f,
//            w * 0.3f, h * 0.8f
//        )
//        close()
//    }
//
//    drawPath(path, brush)
//    drawPath(path, outlineColor, style = Stroke(width = size.minDimension * 0.03f))
//
//    // Mitre slit
//    drawLine(
//        color = outlineColor,
//        start = Offset(size.width * 0.5f, size.height * 0.2f),
//        end = Offset(size.width * 0.5f, size.height * 0.35f),
//        strokeWidth = size.minDimension * 0.02f
//    )
//}
//
//fun DrawScope.drawQueen(brush: Brush, outlineColor: Color) {
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//
//        // Base
//        moveTo(w * 0.2f, h * 0.9f)
//        lineTo(w * 0.8f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.8f)
//
//        // Body
//        cubicTo(
//            w * 0.85f, h * 0.6f,
//            w * 0.7f, h * 0.45f,
//            w * 0.65f, h * 0.4f
//        )
//
//        // Crown points
//        lineTo(w * 0.7f, h * 0.2f)
//        lineTo(w * 0.6f, h * 0.3f)
//        lineTo(w * 0.5f, h * 0.15f)
//        lineTo(w * 0.4f, h * 0.3f)
//        lineTo(w * 0.3f, h * 0.2f)
//
//        lineTo(w * 0.35f, h * 0.4f)
//        cubicTo(
//            w * 0.3f, h * 0.45f,
//            w * 0.15f, h * 0.6f,
//            w * 0.25f, h * 0.8f
//        )
//        close()
//    }
//
//    drawPath(path, brush)
//    drawPath(path, outlineColor, style = Stroke(width = size.minDimension * 0.03f))
//
//    // Crown jewels
//    val jewelPositions = listOf(0.3f, 0.5f, 0.7f)
//    jewelPositions.forEach { x ->
//        drawCircle(
//            color = Color(0xFFFF5252),
//            radius = size.minDimension * 0.02f,
//            center = Offset(size.width * x, size.height * 0.25f)
//        )
//    }
//}
//
//fun DrawScope.drawKing(brush: Brush, outlineColor: Color) {
//    val path = Path().apply {
//        val w = size.width
//        val h = size.height
//
//        // Base
//        moveTo(w * 0.25f, h * 0.9f)
//        lineTo(w * 0.75f, h * 0.9f)
//        lineTo(w * 0.7f, h * 0.85f)
//
//        // Body
//        cubicTo(
//            w * 0.65f, h * 0.7f,
//            w * 0.6f, h * 0.55f,
//            w * 0.6f, h * 0.45f
//        )
//
//        // Crown
//        lineTo(w * 0.65f, h * 0.35f)
//        lineTo(w * 0.6f, h * 0.25f)
//        lineTo(w * 0.55f, h * 0.3f)
//        lineTo(w * 0.5f, h * 0.2f)
//        lineTo(w * 0.45f, h * 0.3f)
//        lineTo(w * 0.4f, h * 0.25f)
//        lineTo(w * 0.35f, h * 0.35f)
//        lineTo(w * 0.4f, h * 0.45f)
//
//        cubicTo(
//            w * 0.4f, h * 0.55f,
//            w * 0.35f, h * 0.7f,
//            w * 0.3f, h * 0.85f
//        )
//        close()
//    }
//
//    drawPath(path, brush)
//    drawPath(path, outlineColor, style = Stroke(width = size.minDimension * 0.03f))
//
//    // Cross
//    drawLine(
//        color = outlineColor,
//        start = Offset(size.width * 0.5f, size.height * 0.1f),
//        end = Offset(size.width * 0.5f, size.height * 0.2f),
//        strokeWidth = size.minDimension * 0.03f
//    )
//    drawLine(
//        color = outlineColor,
//        start = Offset(size.width * 0.45f, size.height * 0.15f),
//        end = Offset(size.width * 0.55f, size.height * 0.15f),
//        strokeWidth = size.minDimension * 0.03f
//    )
//}


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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
        PieceType.PAWN -> drawPawn(color, outlineColor, size)
        PieceType.ROOK -> drawRook(color, outlineColor, size)
        PieceType.KNIGHT -> drawKnight(color, outlineColor, size)
        PieceType.BISHOP -> drawBishop(color, outlineColor, size)
        PieceType.QUEEN -> drawQueen(color, outlineColor, size)
        PieceType.KING -> drawKing(color, outlineColor, size)
    }
}

private fun DrawScope.drawPawn(fillColor: Color, strokeColor: Color, pieceSize: Float) {
    val scale = pieceSize / 100f
    val centerX = center.x
    val centerY = center.y

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Fill
            color = fillColor
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 2f * scale
            color = strokeColor
        }

        val path = Path().apply {
            // Base
            moveTo(centerX - 25f * scale, centerY + 40f * scale)
            lineTo(centerX + 25f * scale, centerY + 40f * scale)
            lineTo(centerX + 20f * scale, centerY + 30f * scale)
            lineTo(centerX + 15f * scale, centerY + 20f * scale)

            // Neck
            lineTo(centerX + 10f * scale, centerY + 5f * scale)
            cubicTo(
                centerX + 12f * scale, centerY - 5f * scale,
                centerX + 12f * scale, centerY - 10f * scale,
                centerX + 8f * scale, centerY - 15f * scale
            )

            // Head
            cubicTo(
                centerX + 15f * scale, centerY - 20f * scale,
                centerX + 15f * scale, centerY - 30f * scale,
                centerX, centerY - 35f * scale
            )
            cubicTo(
                centerX - 15f * scale, centerY - 30f * scale,
                centerX - 15f * scale, centerY - 20f * scale,
                centerX - 8f * scale, centerY - 15f * scale
            )

            // Other side neck
            cubicTo(
                centerX - 12f * scale, centerY - 10f * scale,
                centerX - 12f * scale, centerY - 5f * scale,
                centerX - 10f * scale, centerY + 5f * scale
            )

            // Other side base
            lineTo(centerX - 15f * scale, centerY + 20f * scale)
            lineTo(centerX - 20f * scale, centerY + 30f * scale)
            close()
        }

        canvas.drawPath(path, paint)
        canvas.drawPath(path, strokePaint)
    }
}

private fun DrawScope.drawRook(fillColor: Color, strokeColor: Color, pieceSize: Float) {
    val scale = pieceSize / 100f
    val centerX = center.x
    val centerY = center.y

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Fill
            color = fillColor
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 2f * scale
            color = strokeColor
        }

        val path = Path().apply {
            // Base
            moveTo(centerX - 30f * scale, centerY + 40f * scale)
            lineTo(centerX + 30f * scale, centerY + 40f * scale)
            lineTo(centerX + 25f * scale, centerY + 30f * scale)
            lineTo(centerX + 20f * scale, centerY + 20f * scale)

            // Tower body
            lineTo(centerX + 20f * scale, centerY - 10f * scale)

            // Battlements
            lineTo(centerX + 20f * scale, centerY - 25f * scale)
            lineTo(centerX + 12f * scale, centerY - 25f * scale)
            lineTo(centerX + 12f * scale, centerY - 15f * scale)
            lineTo(centerX + 6f * scale, centerY - 15f * scale)
            lineTo(centerX + 6f * scale, centerY - 25f * scale)
            lineTo(centerX - 6f * scale, centerY - 25f * scale)
            lineTo(centerX - 6f * scale, centerY - 15f * scale)
            lineTo(centerX - 12f * scale, centerY - 15f * scale)
            lineTo(centerX - 12f * scale, centerY - 25f * scale)
            lineTo(centerX - 20f * scale, centerY - 25f * scale)

            // Other side
            lineTo(centerX - 20f * scale, centerY - 10f * scale)
            lineTo(centerX - 20f * scale, centerY + 20f * scale)
            lineTo(centerX - 25f * scale, centerY + 30f * scale)
            close()
        }

        canvas.drawPath(path, paint)
        canvas.drawPath(path, strokePaint)
    }
}

private fun DrawScope.drawKnight(fillColor: Color, strokeColor: Color, pieceSize: Float) {
    val scale = pieceSize / 100f
    val centerX = center.x
    val centerY = center.y

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Fill
            color = fillColor
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 2f * scale
            color = strokeColor
        }

        val path = Path().apply {
            // Base
            moveTo(centerX - 25f * scale, centerY + 40f * scale)
            lineTo(centerX + 25f * scale, centerY + 40f * scale)
            lineTo(centerX + 20f * scale, centerY + 30f * scale)

            // Body curve
            cubicTo(
                centerX + 15f * scale, centerY + 20f * scale,
                centerX + 15f * scale, centerY + 10f * scale,
                centerX + 20f * scale, centerY
            )

            // Neck and head
            cubicTo(
                centerX + 25f * scale, centerY - 15f * scale,
                centerX + 20f * scale, centerY - 25f * scale,
                centerX + 10f * scale, centerY - 30f * scale
            )

            // Ears
            lineTo(centerX + 8f * scale, centerY - 35f * scale)
            lineTo(centerX + 3f * scale, centerY - 30f * scale)
            lineTo(centerX, centerY - 35f * scale)
            lineTo(centerX - 5f * scale, centerY - 28f * scale)

            // Face
            cubicTo(
                centerX - 10f * scale, centerY - 20f * scale,
                centerX - 15f * scale, centerY - 10f * scale,
                centerX - 15f * scale, centerY
            )

            // Chest
            cubicTo(
                centerX - 20f * scale, centerY + 5f * scale,
                centerX - 22f * scale, centerY + 15f * scale,
                centerX - 20f * scale, centerY + 30f * scale
            )

            close()
        }

        canvas.drawPath(path, paint)
        canvas.drawPath(path, strokePaint)

        // Eye
        canvas.drawCircle(
            Offset(centerX - 5f * scale, centerY - 15f * scale),
            2f * scale,
            Paint().apply {
                color = strokeColor
                isAntiAlias = true
            }
        )
    }
}

private fun DrawScope.drawBishop(fillColor: Color, strokeColor: Color, pieceSize: Float) {
    val scale = pieceSize / 100f
    val centerX = center.x
    val centerY = center.y

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Fill
            color = fillColor
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 2f * scale
            color = strokeColor
        }

        val path = Path().apply {
            // Base
            moveTo(centerX - 25f * scale, centerY + 40f * scale)
            lineTo(centerX + 25f * scale, centerY + 40f * scale)
            lineTo(centerX + 20f * scale, centerY + 30f * scale)

            // Body
            cubicTo(
                centerX + 18f * scale, centerY + 20f * scale,
                centerX + 15f * scale, centerY,
                centerX + 10f * scale, centerY - 15f * scale
            )

            // Hat
            cubicTo(
                centerX + 8f * scale, centerY - 20f * scale,
                centerX + 5f * scale, centerY - 25f * scale,
                centerX, centerY - 30f * scale
            )

            // Top knob
            cubicTo(
                centerX - 2f * scale, centerY - 32f * scale,
                centerX - 2f * scale, centerY - 35f * scale,
                centerX, centerY - 37f * scale
            )
            cubicTo(
                centerX + 2f * scale, centerY - 35f * scale,
                centerX + 2f * scale, centerY - 32f * scale,
                centerX, centerY - 30f * scale
            )

            // Other side
            cubicTo(
                centerX - 5f * scale, centerY - 25f * scale,
                centerX - 8f * scale, centerY - 20f * scale,
                centerX - 10f * scale, centerY - 15f * scale
            )
            cubicTo(
                centerX - 15f * scale, centerY,
                centerX - 18f * scale, centerY + 20f * scale,
                centerX - 20f * scale, centerY + 30f * scale
            )

            close()
        }

        canvas.drawPath(path, paint)
        canvas.drawPath(path, strokePaint)

        // Mitre slit
        canvas.drawLine(
            Offset(centerX, centerY - 25f * scale),
            Offset(centerX, centerY - 10f * scale),
            strokePaint
        )
    }
}

private fun DrawScope.drawQueen(fillColor: Color, strokeColor: Color, pieceSize: Float) {
    val scale = pieceSize / 100f
    val centerX = center.x
    val centerY = center.y

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Fill
            color = fillColor
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 2f * scale
            color = strokeColor
        }

        val path = Path().apply {
            // Base
            moveTo(centerX - 30f * scale, centerY + 40f * scale)
            lineTo(centerX + 30f * scale, centerY + 40f * scale)
            lineTo(centerX + 25f * scale, centerY + 30f * scale)

            // Body
            cubicTo(
                centerX + 22f * scale, centerY + 15f * scale,
                centerX + 20f * scale, centerY,
                centerX + 15f * scale, centerY - 10f * scale
            )

            // Crown points
            lineTo(centerX + 20f * scale, centerY - 25f * scale)
            lineTo(centerX + 12f * scale, centerY - 20f * scale)
            lineTo(centerX + 10f * scale, centerY - 30f * scale)
            lineTo(centerX + 5f * scale, centerY - 20f * scale)
            lineTo(centerX, centerY - 35f * scale)
            lineTo(centerX - 5f * scale, centerY - 20f * scale)
            lineTo(centerX - 10f * scale, centerY - 30f * scale)
            lineTo(centerX - 12f * scale, centerY - 20f * scale)
            lineTo(centerX - 20f * scale, centerY - 25f * scale)

            // Other side
            lineTo(centerX - 15f * scale, centerY - 10f * scale)
            cubicTo(
                centerX - 20f * scale, centerY,
                centerX - 22f * scale, centerY + 15f * scale,
                centerX - 25f * scale, centerY + 30f * scale
            )

            close()
        }

        canvas.drawPath(path, paint)
        canvas.drawPath(path, strokePaint)

        // Crown jewels
        val jewelPositions = listOf(-10f, 0f, 10f)
        jewelPositions.forEach { offset ->
            canvas.drawCircle(
                Offset(centerX + offset * scale, centerY - 27f * scale),
                2f * scale,
                Paint().apply {
                    color = Color(0xFFFF5252)
                    isAntiAlias = true
                }
            )
        }
    }
}

private fun DrawScope.drawKing(fillColor: Color, strokeColor: Color, pieceSize: Float) {
    val scale = pieceSize / 100f
    val centerX = center.x
    val centerY = center.y

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Fill
            color = fillColor
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 2f * scale
            color = strokeColor
        }

        val path = Path().apply {
            // Base
            moveTo(centerX - 30f * scale, centerY + 40f * scale)
            lineTo(centerX + 30f * scale, centerY + 40f * scale)
            lineTo(centerX + 25f * scale, centerY + 30f * scale)

            // Body
            cubicTo(
                centerX + 22f * scale, centerY + 20f * scale,
                centerX + 20f * scale, centerY + 5f * scale,
                centerX + 18f * scale, centerY - 5f * scale
            )

            // Crown base
            lineTo(centerX + 15f * scale, centerY - 15f * scale)
            lineTo(centerX + 12f * scale, centerY - 18f * scale)
            lineTo(centerX + 8f * scale, centerY - 15f * scale)
            lineTo(centerX + 5f * scale, centerY - 20f * scale)
            lineTo(centerX, centerY - 15f * scale)
            lineTo(centerX - 5f * scale, centerY - 20f * scale)
            lineTo(centerX - 8f * scale, centerY - 15f * scale)
            lineTo(centerX - 12f * scale, centerY - 18f * scale)
            lineTo(centerX - 15f * scale, centerY - 15f * scale)

            // Other side
            lineTo(centerX - 18f * scale, centerY - 5f * scale)
            cubicTo(
                centerX - 20f * scale, centerY + 5f * scale,
                centerX - 22f * scale, centerY + 20f * scale,
                centerX - 25f * scale, centerY + 30f * scale
            )

            close()
        }

        canvas.drawPath(path, paint)
        canvas.drawPath(path, strokePaint)

        // Cross on top
        val crossPaint = Paint().apply {
            isAntiAlias = true
            style = PaintingStyle.Stroke
            strokeWidth = 3f * scale
            color = strokeColor
        }

        // Vertical line
        canvas.drawLine(
            Offset(centerX, centerY - 30f * scale),
            Offset(centerX, centerY - 15f * scale),
            crossPaint
        )

        // Horizontal line
        canvas.drawLine(
            Offset(centerX - 5f * scale, centerY - 25f * scale),
            Offset(centerX + 5f * scale, centerY - 25f * scale),
            crossPaint
        )
    }
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
    val files = listOf("a", "b", "c", "d", "e", "f", "g", "h")
    val ranks = listOf("8", "7", "6", "5", "4", "3", "2", "1")

    // File labels (bottom)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .offset(y = squareSize * 8 - 20.dp)
    ) {
        files.forEachIndexed { index, file ->
            val displayIndex = if (isBoardFlipped) 7 - index else index
            Box(
                modifier = Modifier
                    .width(squareSize)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    files[displayIndex],
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Rank labels (left)
    Column(
        modifier = Modifier
            .width(20.dp)
            .fillMaxHeight()
            .offset(x = (-20).dp)
    ) {
        ranks.forEachIndexed { index, rank ->
            val displayIndex = if (isBoardFlipped) 7 - index else index
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(squareSize),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    ranks[displayIndex],
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GameControlsBar(
    isMyTurn: Boolean,
    onResign: () -> Unit,
    onOfferDraw: () -> Unit,
    onChat: () -> Unit
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
            GameControlButton(
                icon = Icons.Default.ChatBubble,
                label = "Chat",
                onClick = onChat,
                tint = Color(0xFF4ECDC4)
            )

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
        }
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
    betAmount: Float,
    onDismiss: () -> Unit
) {
    val (title, subtitle, icon, color) = when (result) {
        is GameResult.Win -> {
            if (result.winner == playerColor) {
                Quadruple(
                    "Victory!",
                    "You won ₹${betAmount * 2}",
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

// Helper data class
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)