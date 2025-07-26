package `in`.chess.scholars.global.presentation.matchmaking
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.UserData
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(
    navController: NavController,
    viewModel: MatchmakingViewModel,
    currentUser: UserData?, // Pass the current user's data
    betAmount: Float
) {
    val uiState by viewModel.uiState.collectAsState()

    // Start matchmaking when the screen is first composed
    LaunchedEffect(Unit) {
        currentUser?.let {
            viewModel.startMatchmaking(it.rating, betAmount)
        }
    }

    // Navigate to the game screen when a match is found
    LaunchedEffect(uiState.gameId) {
        if (uiState.gameId != null) {
            delay(1500) // Wait for the "Match Found" animation
            navController.navigate("game/${uiState.gameId}/$betAmount") {
                popUpTo("lobby") { inclusive = true }
            }
        }
    }

    // Handle back press to cancel matchmaking
    BackHandler {
        viewModel.cancelMatchmaking()
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            when {
                uiState.isSearching -> {
                    SearchingView(userRating = currentUser?.rating ?: 1200, betAmount = betAmount)
                }
                uiState.opponent != null -> {
                    MatchFoundView(currentUser = currentUser, opponent = uiState.opponent!!)
                }
                uiState.error != null -> {
                    ErrorView(message = uiState.error!!)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = {
                    viewModel.cancelMatchmaking()
                    navController.popBackStack()
                },
                border = BorderStroke(1.dp, Color(0xFFFF5252))
            ) {
                Text("Cancel", color = Color(0xFFFF5252))
            }
        }
    }
}

@Composable
private fun SearchingView(userRating: Int, betAmount: Float) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    SearchingAnimation(rotation)
    Spacer(modifier = Modifier.height(32.dp))
    Text("Finding Opponent...", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.height(8.dp))
    Text("Your Rating: $userRating", fontSize = 16.sp, color = Color.Gray)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Bet Amount: ₹${betAmount.toInt()}", fontSize = 18.sp, color = Color(0xFFFFD700))
}

@Composable
private fun MatchFoundView(currentUser: UserData?, opponent: UserData) {
    MatchFoundAnimation()
    Spacer(modifier = Modifier.height(32.dp))
    Text("Match Found!", fontSize = 28.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerCard(name = currentUser?.displayName ?: "You", rating = currentUser?.rating ?: 1200)
        Text("VS", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        PlayerCard(name = opponent.displayName, rating = opponent.rating)
    }
}

@Composable
private fun ErrorView(message: String) {
    Icon(
        Icons.Default.Error,
        contentDescription = "Error",
        tint = Color(0xFFFF5252),
        modifier = Modifier.size(64.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Matchmaking Error",
        fontSize = 20.sp,
        color = Color(0xFFFF5252),
        fontWeight = FontWeight.Bold
    )
    Text(
        text = message,
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 32.dp)
    )
}

@Composable
private fun SearchingAnimation(rotation: Float) {
    Canvas(
        modifier = Modifier
            .size(140.dp)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val strokeWidth = 8.dp.toPx()
        drawArc(
            color = Color(0xFF4CAF50),
            startAngle = 0f,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFFFFD700),
            startAngle = 180f,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun MatchFoundAnimation() {
    var scale by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(0f, 1f, animationSpec = spring(Spring.DampingRatioMediumBouncy)) { value, _ ->
            scale = value
        }
    }
    Box(
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50), CircleShape)
        )
        Icon(
            Icons.Default.Check,
            contentDescription = "Match Found",
            tint = Color.White,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun PlayerCard(name: String, rating: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Person, contentDescription = "Player", tint = Color.White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
            Text("Rating: $rating", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

