//package `in`.chess.scholars.global.presentation.game.ui
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.scaleIn
//import androidx.compose.animation.scaleOut
//import androidx.compose.animation.slideInVertically
//import androidx.compose.animation.slideOutVertically
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.imePadding
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.widthIn
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ChatBubble
//import androidx.compose.material.icons.filled.EmojiEvents
//import androidx.compose.material.icons.filled.Flag
//import androidx.compose.material.icons.filled.Handshake
//import androidx.compose.material.icons.filled.Info
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Badge
//import androidx.compose.material3.BadgedBox
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import `in`.chess.scholars.global.domain.model.ChatMessage
//import `in`.chess.scholars.global.domain.model.GameResult
//import `in`.chess.scholars.global.domain.model.PieceColor
//import `in`.chess.scholars.global.domain.model.PieceType
//import `in`.chess.scholars.global.presentation.game.PrizeInfo
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.Locale
//import kotlin.math.min
//
//// This file now contains all the UI Composables for the Game Screen.
//
//@Composable
//fun CheckIndicator() {
//    val infiniteTransition = rememberInfiniteTransition()
//    val animatedAlpha by infiniteTransition.animateFloat(
//        initialValue = 0.3f,
//        targetValue = 0.8f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(500),
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        drawCircle(
//            color = Color(0xFFFF5252).copy(alpha = animatedAlpha),
//            radius = size.minDimension / 2,
//            style = Stroke(width = 4.dp.toPx())
//        )
//    }
//}
//
//@Composable
//fun FileRankLabels(squareSize: Dp, isBoardFlipped: Boolean) {
//    val files = listOf("a", "b", "c", "d", "e", "f", "g", "h")
//    val ranks = listOf("8", "7", "6", "5", "4", "3", "2", "1")
//
//    // File labels (bottom)
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(20.dp)
//            .offset(y = squareSize * 8 - 20.dp)
//    ) {
//        files.forEachIndexed { index, file ->
//            val displayIndex = if (isBoardFlipped) 7 - index else index
//            Box(
//                modifier = Modifier
//                    .width(squareSize)
//                    .fillMaxHeight(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    files[displayIndex],
//                    color = Color.White.copy(alpha = 0.5f),
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//
//    // Rank labels (left)
//    Column(
//        modifier = Modifier
//            .width(20.dp)
//            .fillMaxHeight()
//            .offset(x = (-20).dp)
//    ) {
//        ranks.forEachIndexed { index, rank ->
//            val displayIndex = if (isBoardFlipped) 7 - index else index
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(squareSize),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    ranks[displayIndex],
//                    color = Color.White.copy(alpha = 0.5f),
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun GameControlButton(
//    icon: ImageVector,
//    label: String,
//    onClick: () -> Unit,
//    enabled: Boolean = true,
//    tint: Color
//) {
//    val animatedAlpha by animateFloatAsState(
//        targetValue = if (enabled) 1f else 0.3f
//    )
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .clip(RoundedCornerShape(12.dp))
//            .clickable(enabled = enabled) { onClick() }
//            .padding(12.dp)
//            .alpha(animatedAlpha)
//    ) {
//        Icon(
//            icon,
//            contentDescription = label,
//            tint = tint,
//            modifier = Modifier.size(24.dp)
//        )
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(
//            label,
//            color = Color.White.copy(alpha = 0.7f),
//            fontSize = 12.sp
//        )
//    }
//}
//
//@Composable
//fun GameStatusIndicators(
//    isCheck: Boolean,
//    gameResult: GameResult
//) {
//    AnimatedVisibility(
//        visible = isCheck && gameResult is GameResult.InProgress,
//        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
//        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 32.dp, vertical = 16.dp),
//            contentAlignment = Alignment.TopCenter
//        ) {
//            Card(
//                shape = RoundedCornerShape(12.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = Color(0xFFFF5252).copy(alpha = 0.9f)
//                )
//            ) {
//                Row(
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Icon(
//                        Icons.Default.Warning,
//                        contentDescription = "Check",
//                        tint = Color.White,
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Text(
//                        "CHECK!",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 16.sp
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun LoadingOverlay() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.5f)),
//        contentAlignment = Alignment.Center
//    ) {
//        Card(
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = Color.Black.copy(alpha = 0.8f)
//            )
//        ) {
//            Row(
//                modifier = Modifier.padding(24.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(24.dp),
//                    color = Color(0xFF4ECDC4),
//                    strokeWidth = 2.dp
//                )
//                Text(
//                    "Loading game...",
//                    color = Color.White,
//                    fontSize = 16.sp
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun GameOverOverlay(
//    result: GameResult,
//    playerColor: PieceColor,
//    prizeInfo: PrizeInfo,
//    onDismiss: () -> Unit
//) {
//    val (title, subtitle, icon, color) = when (result) {
//        is GameResult.Win -> {
//            if (result.winner == playerColor) {
//                val payout = "%.2f".format(prizeInfo.winningsPayable)
//                Quadruple(
//                    "Victory!",
//                    "You won ₹${payout}",
//                    Icons.Default.EmojiEvents,
//                    Color(0xFFFFD700)
//                )
//            } else {
//                Quadruple(
//                    "Defeat",
//                    "Better luck next time",
//                    Icons.Default.SentimentVeryDissatisfied,
//                    Color(0xFFFF5252)
//                )
//            }
//        }
//        is GameResult.Draw -> {
//            Quadruple(
//                "Draw",
//                "A fair battle",
//                Icons.Default.Handshake,
//                Color(0xFFFF9800)
//            )
//        }
//        else -> Quadruple("", "", null, Color.Transparent)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.8f))
//            .clickable(onClick = onDismiss),
//        contentAlignment = Alignment.Center
//    ) {
//        Card(
//            shape = RoundedCornerShape(24.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = Color(0xFF1a1a2e)
//            ),
//            border = BorderStroke(2.dp, color)
//        ) {
//            Column(
//                modifier = Modifier.padding(40.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                if (icon != null) {
//                    Box(
//                        modifier = Modifier
//                            .size(80.dp)
//                            .clip(CircleShape)
//                            .background(color.copy(alpha = 0.2f)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            icon,
//                            contentDescription = title,
//                            tint = color,
//                            modifier = Modifier.size(48.dp)
//                        )
//                    }
//                }
//
//                Text(
//                    title,
//                    fontSize = 32.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = color
//                )
//
//                Text(
//                    subtitle,
//                    fontSize = 18.sp,
//                    color = Color.White.copy(alpha = 0.7f),
//                    textAlign = TextAlign.Center
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Button(
//                    onClick = onDismiss,
//                    shape = RoundedCornerShape(12.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = color
//                    )
//                ) {
//                    Text(
//                        "Continue",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ResignConfirmationDialog(
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        modifier = Modifier.clip(RoundedCornerShape(24.dp))
//    ) {
//        Surface(
//            shape = RoundedCornerShape(24.dp),
//            color = Color(0xFF1a1a2e)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    Icons.Default.Flag,
//                    contentDescription = "Resign",
//                    tint = Color(0xFFFF5252),
//                    modifier = Modifier.size(48.dp)
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    "Resign Game?",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//
//                Text(
//                    "Are you sure you want to resign? This action cannot be undone.",
//                    fontSize = 14.sp,
//                    color = Color.White.copy(alpha = 0.7f),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(12.dp),
//                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
//                    ) {
//                        Text("Cancel", color = Color.White)
//                    }
//
//                    Button(
//                        onClick = onConfirm,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFFF5252)
//                        )
//                    ) {
//                        Text("Resign")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DrawOfferDialog(
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        modifier = Modifier.clip(RoundedCornerShape(24.dp))
//    ) {
//        Surface(
//            shape = RoundedCornerShape(24.dp),
//            color = Color(0xFF1a1a2e)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    Icons.Default.Handshake,
//                    contentDescription = "Draw",
//                    tint = Color(0xFFFFD700),
//                    modifier = Modifier.size(48.dp)
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    "Offer Draw?",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//
//                Text(
//                    "Send a draw offer to your opponent?",
//                    fontSize = 14.sp,
//                    color = Color.White.copy(alpha = 0.7f),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(12.dp),
//                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
//                    ) {
//                        Text("Cancel", color = Color.White)
//                    }
//
//                    Button(
//                        onClick = onConfirm,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFFFD700)
//                        )
//                    ) {
//                        Text("Offer Draw", color = Color.Black)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OpponentDrawOfferDialog(
//    onAccept: () -> Unit,
//    onDecline: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDecline,
//        modifier = Modifier.clip(RoundedCornerShape(24.dp))
//    ) {
//        Surface(
//            shape = RoundedCornerShape(24.dp),
//            color = Color(0xFF1a1a2e)
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    Icons.Default.Handshake,
//                    contentDescription = "Draw Offer",
//                    tint = Color(0xFFFFD700),
//                    modifier = Modifier.size(48.dp)
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    "Draw Offer",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//
//                Text(
//                    "Your opponent has offered a draw.",
//                    fontSize = 14.sp,
//                    color = Color.White.copy(alpha = 0.7f),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    Button(
//                        onClick = onDecline,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFFF5252)
//                        )
//                    ) {
//                        Text("Decline")
//                    }
//
//                    Button(
//                        onClick = onAccept,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF4CAF50)
//                        )
//                    ) {
//                        Text("Accept")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatSheetContent(
//    messages: List<ChatMessage>,
//    onSendMessage: (String) -> Unit,
//    currentUserId: String
//) {
//    var messageText by remember { mutableStateOf("") }
//    val listState = rememberLazyListState()
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val coroutineScope = rememberCoroutineScope()
//
//    // Scroll to bottom when new messages arrive
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            coroutineScope.launch {
//                listState.animateScrollToItem(messages.size - 1)
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .imePadding()
//    ) {
//        // Header
//        Text(
//            "In-Game Chat",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        // Messages list
//        LazyColumn(
//            state = listState,
//            modifier = Modifier
//                .weight(1f)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(messages) { msg ->
//                ChatMessageItem(
//                    message = msg,
//                    isSentByCurrentUser = msg.userId == currentUserId
//                )
//            }
//        }
//
//        // Message input field
//        Surface(
//            modifier = Modifier.fillMaxWidth(),
//            shadowElevation = 8.dp
//        ){
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                OutlinedTextField(
//                    value = messageText,
//                    onValueChange = { messageText = it },
//                    placeholder = { Text("Type a message...") },
//                    modifier = Modifier.weight(1f),
//                    shape = RoundedCornerShape(24.dp),
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
//                    keyboardActions = KeyboardActions(onSend = {
//                        onSendMessage(messageText)
//                        messageText = ""
//                        keyboardController?.hide()
//                    })
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Button(
//                    onClick = {
//                        onSendMessage(messageText)
//                        messageText = ""
//                        keyboardController?.hide()
//                    },
//                    enabled = messageText.isNotBlank(),
//                    shape = CircleShape,
//                    contentPadding = PaddingValues(12.dp),
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Icon(Icons.Default.Send, contentDescription = "Send")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatMessageItem(message: ChatMessage, isSentByCurrentUser: Boolean) {
//    val alignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
//    val backgroundColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
//    val textColor = if (isSentByCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
//    val bubbleShape = if (isSentByCurrentUser) {
//        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
//    } else {
//        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
//    }
//    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
//
//
//    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
//        Column(
//            horizontalAlignment = if (isSentByCurrentUser) Alignment.End else Alignment.Start,
//            modifier = Modifier.widthIn(max = 300.dp)
//        ) {
//            Text(
//                text = if (isSentByCurrentUser) "You" else message.displayName,
//                style = MaterialTheme.typography.labelSmall,
//                color = Color.Gray,
//                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
//            )
//            Box(
//                modifier = Modifier
//                    .background(color = backgroundColor, shape = bubbleShape)
//                    .padding(horizontal = 12.dp, vertical = 8.dp)
//            ) {
//                Text(text = message.message, color = textColor)
//            }
//            message.timestamp?.toDate()?.let {
//                Text(
//                    text = dateFormat.format(it),
//                    style = MaterialTheme.typography.labelSmall,
//                    color = Color.Gray,
//                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun GameInfoSheetContent(prizeInfo: PrizeInfo) {
//    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxWidth()) {
//        item {
//            Text("Game Information", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
//        }
//        item { InfoRow("Bet Amount", "₹${prizeInfo.betAmount}") }
//        item { InfoRow("Prize Pool (2x)", "₹${prizeInfo.prizePool}") }
//        item { InfoRow("Platform Fee (4%)", "- ₹${"%.2f".format(prizeInfo.platformFee)}", isDeduction = true) }
//        item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
//        item { InfoRow("Net Winnings", "₹${"%.2f".format(prizeInfo.taxableAmount)}") }
//        item { InfoRow("TDS (30% of Winnings)", "- ₹${"%.2f".format(prizeInfo.tdsDeducted)}", isDeduction = true) }
//        item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }
//        item {
//            InfoRow("Final Payout to Winner", "₹${"%.2f".format(prizeInfo.winningsPayable)}", isHighlight = true)
//        }
//    }
//}
//
//@Composable
//fun InfoRow(label: String, value: String, isDeduction: Boolean = false, isHighlight: Boolean = false) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(label, color = if (isHighlight) Color.White else Color.Gray)
//        Text(
//            value,
//            color = when {
//                isHighlight -> Color(0xFF4CAF50)
//                isDeduction -> Color(0xFFFF5252)
//                else -> Color.White
//            },
//            fontWeight = if(isHighlight) FontWeight.Bold else FontWeight.Normal
//        )
//    }
//}
//
//// Helper data class
//private data class Quadruple<A, B, C, D>(
//    val first: A,
//    val second: B,
//    val third: C,
//    val fourth: D
//)
