//package `in`.chess.scholars.global.presentation.profile
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import `in`.chess.scholars.global.domain.model.GameHistory
//import `in`.chess.scholars.global.domain.model.GameResultStats
//import `in`.chess.scholars.global.presentation.statistics.StatisticsViewModel
//import `in`.chess.scholars.global.presentation.statistics.StatisticsUiState
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileScreen(
//    navController: NavController,
//    viewModel: StatisticsViewModel
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { viewModel.refreshStatistics() }) {
//                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF1a1a2e),
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White
//                )
//            )
//        },
//        containerColor = Color(0xFF16213e)
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF16213e),
//                            Color(0xFF0f3460)
//                        )
//                    )
//                )
//        ) {
//            when {
//                uiState.isLoading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//                uiState.error != null -> {
//                    Text(
//                        text = uiState.error!!,
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                else -> {
//                    ProfileContent(uiState, navController)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ProfileContent(uiState: StatisticsUiState, navController: NavController) {
//    LazyColumn(
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        item {
//            ProfileHeader(uiState)
//        }
//        item {
//            StatisticsGrid(uiState)
//        }
//        item {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { navController.navigate("statistics_detail") }, // Navigate to a new detailed screen
//                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f))
//            ) {
//                Row(
//                    modifier = Modifier.padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(Icons.Default.Analytics, contentDescription = "Detailed Stats", tint = Color(0xFF4CAF50))
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Text("View Detailed Statistics", color = Color.White, fontWeight = FontWeight.Medium)
//                    Spacer(modifier = Modifier.weight(1f))
//                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
//                }
//            }
//        }
//        item {
//            Text(
//                "Recent Games",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }
//        if (uiState.gameHistory.isEmpty()) {
//            item {
//                Text(
//                    "No games played yet.",
//                    color = Color.Gray,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 32.dp),
//                    textAlign = TextAlign.Center
//                )
//            }
//        } else {
//            items(uiState.gameHistory) { game ->
//                GameHistoryCard(game)
//            }
//        }
//    }
//}
//
//@Composable
//private fun ProfileHeader(uiState: StatisticsUiState) {
//    // In a real app, get user name from a UserViewModel or similar
//    val userName = uiState.userData?.displayName ?: "Player"
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.9f))
//    ) {
//        Column(
//            modifier = Modifier.padding(20.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//                    .background(Brush.linearGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF2196F3)))),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    userName.first().uppercase(),
//                    fontSize = 32.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//            }
//            Spacer(modifier = Modifier.height(12.dp))
//            Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "${uiState.currentRating}",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = Color(0xFFFFD700)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun StatisticsGrid(uiState: StatisticsUiState) {
//    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//        StatCard(
//            title = "Games",
//            value = uiState.totalGames.toString(),
//            icon = Icons.Default.Games,
//            color = Color(0xFF2196F3),
//            modifier = Modifier.weight(1f)
//        )
//        StatCard(
//            title = "Wins",
//            value = uiState.wins.toString(),
//            icon = Icons.Default.EmojiEvents,
//            color = Color(0xFF4CAF50),
//            modifier = Modifier.weight(1f)
//        )
//        StatCard(
//            title = "Win Rate",
//            value = "${uiState.winRate.toInt()}%",
//            icon = Icons.Default.TrendingUp,
//            color = Color(0xFFFF9800),
//            modifier = Modifier.weight(1f)
//        )
//    }
//}
//
//@Composable
//private fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
//    Card(
//        modifier = modifier,
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
//            Text(title, fontSize = 12.sp, color = Color.Gray)
//        }
//    }
//}
//
//@Composable
//private fun GameHistoryCard(game: GameHistory) {
//    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
//    val resultColor = when (game.result) {
//        GameResultStats.WIN -> Color(0xFF4CAF50)
//        GameResultStats.LOSS -> Color(0xFFFF5252)
//        GameResultStats.DRAW -> Color(0xFFFF9800)
//    }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.7f))
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text("vs ${game.opponent}", color = Color.White, fontWeight = FontWeight.Medium)
//                Text(dateFormat.format(Date(game.date)), color = Color.Gray, fontSize = 12.sp)
//            }
//            Column(horizontalAlignment = Alignment.End) {
//                Text(
//                    game.result.name,
//                    color = resultColor,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    "${if (game.ratingChange >= 0) "+" else ""}${game.ratingChange}",
//                    color = if (game.ratingChange >= 0) Color(0xFF4CAF50) else Color(0xFFFF5252)
//                )
//            }
//        }
//    }
//}
//


package `in`.chess.scholars.global.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.GameHistory
import `in`.chess.scholars.global.domain.model.GameResultStats
import `in`.chess.scholars.global.presentation.statistics.StatisticsUiState
import `in`.chess.scholars.global.presentation.statistics.StatisticsViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: StatisticsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isLoaded = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Premium animated background
        PremiumProfileBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                PremiumProfileTopBar(navController, viewModel)
            }
        ) { paddingValues ->
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Animated Profile Header
                    item {
                        AnimatedProfileHeader(uiState)
                    }

                    // Premium Statistics Grid
                    item {
                        PremiumStatisticsGrid(uiState)
                    }

                    // Performance Overview
                    item {
                        PerformanceOverviewCard(uiState)
                    }

                    // Achievements Section
                    item {
                        AchievementsSection(uiState)
                    }

                    // Navigation Cards
                    item {
                        NavigationCardsSection(navController)
                    }

                    // Recent Games Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Recent Battles",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            TextButton(onClick = { navController.navigate("game_history") }) {
                                Text(
                                    "View All",
                                    color = Color(0xFF4ECDC4),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Recent Games List
                    if (uiState.gameHistory.isEmpty()) {
                        item {
                            EmptyGamesCard()
                        }
                    } else {
                        items(uiState.gameHistory.take(5)) { game ->
                            PremiumGameHistoryCard(game)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumProfileBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Base gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0A0F),
                    Color(0xFF1A1A2E),
                    Color(0xFF16213E),
                    Color(0xFF0F3460)
                )
            )
        )

        // Animated wave patterns
        val waveHeight = 120.dp.toPx()
        val waveCount = 4

        for (i in 0 until waveCount) {
            val path = Path().apply {
                moveTo(0f, size.height * (0.2f + i * 0.15f))

                for (x in 0..size.width.toInt() step 8) {
                    val y = sin(x * 0.008f + waveOffset + i * PI / 2).toFloat() *
                            waveHeight / (i + 1) * (1 + sin(waveOffset * 0.5f) * 0.2f)
                    lineTo(x.toFloat(), size.height * (0.2f + i * 0.15f) + y)
                }

                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = when(i) {
                    0 -> Color(0xFF4ECDC4).copy(alpha = 0.12f)
                    1 -> Color(0xFF44A3A0).copy(alpha = 0.08f)
                    2 -> Color(0xFF2C7A7B).copy(alpha = 0.06f)
                    else -> Color(0xFF1B5E5F).copy(alpha = 0.04f)
                }
            )
        }

        // Floating geometric shapes
        val shapeCount = 25
        for (i in 0 until shapeCount) {
            val x = (sin(particleOffset * 2 * PI + i * 0.5f) * 0.3f + 0.5f) * size.width
            val y = (cos(particleOffset * 2 * PI + i * 0.3f) * 0.4f + 0.5f) * size.height
            val rotation = particleOffset * 360f + i * 30f
            //val size = 15.dp.toPx() * (1 + sin(particleOffset * 4 * PI + i) * 0.3f)

//            rotate(rotation, Offset(x, y)) {
//                drawRect(
//                    color = Color(0xFF4ECDC4).copy(alpha = 0.15f),
//                    topLeft = Offset(x - size/2, y - size/2),
//                    size = androidx.compose.ui.geometry.Size(size, size)
//                )
//            }
        }

        // Glowing orbs
        val orbCount = 15
        for (i in 0 until orbCount) {
            val x = (sin(particleOffset * PI + i * 1.2f) * 0.4f + 0.5f) * size.width
            val y = (cos(particleOffset * PI * 1.3f + i * 0.8f) * 0.3f + 0.6f) * size.height
            val radius = 3.dp.toPx() * (1 + sin(particleOffset * 6 * PI + i) * 0.5f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4ECDC4).copy(alpha = 0.6f),
                        Color(0xFF4ECDC4).copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    radius = (radius * 2).toFloat()
                ),
                radius = (radius * 2).toFloat(),
               // center = Offset(x, y)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumProfileTopBar(navController: NavController, viewModel: StatisticsViewModel) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "My Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(
                onClick = { viewModel.refreshStatistics() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF4ECDC4).copy(alpha = 0.2f))
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color(0xFF4ECDC4)
                )
            }

            IconButton(
                onClick = { /* Settings */ },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun AnimatedProfileHeader(uiState: StatisticsUiState) {
    val userName = uiState.userData?.displayName ?: "Chess Master"
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        delay(500)
        isExpanded = true
    }

    AnimatedVisibility(
        visible = isExpanded,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(800))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4).copy(alpha = 0.15f),
                                Color(0xFF44A3A0).copy(alpha = 0.15f),
                                Color(0xFF2C7A7B).copy(alpha = 0.15f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4).copy(alpha = glowAlpha),
                                Color(0xFF44A3A0).copy(alpha = glowAlpha * 0.7f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                // Decorative background pattern
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.05f)
                ) {
                    val patternSize = 60.dp.toPx()
                    for (x in 0..size.width.toInt() step patternSize.toInt()) {
                        for (y in 0..size.height.toInt() step patternSize.toInt()) {
                            drawCircle(
                                color = Color.White,
                                radius = patternSize / 6,
                                center = Offset(x.toFloat(), y.toFloat())
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Avatar
                    AnimatedProfileAvatar(userName, uiState.currentRating)

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Info
                    Text(
                        userName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Rating with animated star
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        AnimatedRatingIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.currentRating}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "ELO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Player Level
                    Spacer(modifier = Modifier.height(12.dp))
                    PlayerLevelIndicator(uiState)
                }
            }
        }
    }
}

@Composable
private fun AnimatedProfileAvatar(userName: String, rating: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {
        // Outer rotating ring
        Box(
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation)
                .drawBehind {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44A3A0),
                                Color(0xFF2C7A7B),
                                Color(0xFF4ECDC4)
                            )
                        ),
                        radius = size.width / 2,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
        )

        // Main avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4ECDC4),
                            Color(0xFF44A3A0),
                            Color(0xFF2C7A7B)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                userName.firstOrNull()?.toString()?.uppercase() ?: "?",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Rating tier indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        rating < 1200 -> Color(0xFF8BC34A)
                        rating < 1600 -> Color(0xFF2196F3)
                        rating < 2000 -> Color(0xFF9C27B0)
                        else -> Color(0xFFFFD700)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when {
                    rating < 1200 -> Icons.Default.School
                    rating < 1600 -> Icons.Default.Star
                    rating < 2000 -> Icons.Default.LocalPolice
                    else -> Icons.Default.EmojiEvents
                },
                contentDescription = "Tier",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AnimatedRatingIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.size(28.dp)) {
        Icon(
            Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFD700),
            modifier = Modifier
                .size(28.dp)
                .alpha(0.7f + sparkle * 0.3f)
                .scale(0.9f + sparkle * 0.1f)
        )
    }
}

@Composable
private fun PlayerLevelIndicator(uiState: StatisticsUiState) {
    val level = (uiState.currentRating / 100) + 1
    val progress = ((uiState.currentRating % 100) / 100f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Level $level",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4ECDC4)
            )
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = "Level",
                tint = Color(0xFF4ECDC4),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44A3A0)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun PremiumStatisticsGrid(uiState: StatisticsUiState) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(800))
    ) {
        Column {
            Text(
                "Performance Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                item {
                    PremiumStatCard(
                        title = "Total Games",
                        value = uiState.totalGames.toString(),
                        icon = Icons.Default.Games,
                        color = Color(0xFF2196F3),
                        subtitle = "Battles fought"
                    )
                }
                item {
                    PremiumStatCard(
                        title = "Victories",
                        value = uiState.wins.toString(),
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFF4CAF50),
                        subtitle = "Games won"
                    )
                }
                item {
                    PremiumStatCard(
                        title = "Win Rate",
                        value = "${uiState.winRate.toInt()}%",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFFFF9800),
                        subtitle = "Success rate"
                    )
                }
                item {
                    PremiumStatCard(
                        title = "Best Streak",
                        value = "12", // You can add this to your UI state
                        icon = Icons.Default.LocalFireDepartment,
                        color = Color(0xFFFF5722),
                        subtitle = "Consecutive wins"
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    subtitle: String
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.15f),
                            color.copy(alpha = 0.05f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1f, 1f)
                    )
                )
                .border(
                    1.dp,
                    color.copy(alpha = 0.3f + shimmer * 0.2f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            // Animated background effect
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f)
            ) {
                val center = Offset(size.width * 0.8f, size.height * 0.2f)
                drawCircle(
                    color = color,
                    radius = 40.dp.toPx() * (0.8f + shimmer * 0.4f),
                    center = center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceOverviewCard(uiState: StatisticsUiState) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9C27B0).copy(alpha = 0.15f),
                            Color(0xFF673AB7).copy(alpha = 0.15f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0xFF9C27B0).copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Performance",
                            tint = Color(0xFF9C27B0),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Performance Analysis",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        // Performance metrics
                        PerformanceMetric("Average Game Duration", "15 min")
                        PerformanceMetric("Best Opening", "Queen's Gambit")
                        PerformanceMetric("Favorite Time Control", "10+0")
                        PerformanceMetric("Peak Rating", "1650")
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceMetric(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementsSection(uiState: StatisticsUiState) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1200)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(800))
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Achievements",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextButton(onClick = { /* View all achievements */ }) {
                    Text(
                        "View All",
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(getAchievements(uiState)) { achievement ->
                    AchievementCard(achievement)
                }
            }
        }
    }
}

data class Achievement(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val isUnlocked: Boolean,
    val progress: Float = 1f
)

private fun getAchievements(uiState: StatisticsUiState): List<Achievement> {
    return listOf(
        Achievement(
            title = "First Victory",
            description = "Win your first game",
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFF4CAF50),
            isUnlocked = uiState.wins > 0
        ),
        Achievement(
            title = "Chess Enthusiast",
            description = "Play 10 games",
            icon = Icons.Default.Games,
            color = Color(0xFF2196F3),
            isUnlocked = uiState.totalGames >= 10,
            progress = minOf(uiState.totalGames / 10f, 1f)
        ),
        Achievement(
            title = "Winning Streak",
            description = "Win 5 games in a row",
            icon = Icons.Default.LocalFireDepartment,
            color = Color(0xFFFF5722),
            isUnlocked = false, // You can implement streak logic
            progress = 0.6f
        ),
        Achievement(
            title = "Rating Climber",
            description = "Reach 1500 rating",
            icon = Icons.Default.TrendingUp,
            color = Color(0xFF9C27B0),
            isUnlocked = uiState.currentRating >= 1500,
            progress = minOf(uiState.currentRating / 1500f, 1f)
        )
    )
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (achievement.isUnlocked) {
                        Brush.linearGradient(
                            colors = listOf(
                                achievement.color.copy(alpha = 0.2f * glowIntensity),
                                achievement.color.copy(alpha = 0.1f * glowIntensity)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.1f),
                                Color.Gray.copy(alpha = 0.05f)
                            )
                        )
                    }
                )
                .border(
                    1.dp,
                    if (achievement.isUnlocked) {
                        achievement.color.copy(alpha = 0.5f * glowIntensity)
                    } else {
                        Color.Gray.copy(alpha = 0.3f)
                    },
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (achievement.isUnlocked) {
                                achievement.color.copy(alpha = 0.3f)
                            } else {
                                Color.Gray.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        achievement.icon,
                        contentDescription = achievement.title,
                        tint = if (achievement.isUnlocked) achievement.color else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        achievement.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (achievement.isUnlocked) Color.White else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        achievement.description,
                        fontSize = 12.sp,
                        color = if (achievement.isUnlocked) Color.White.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }

                if (!achievement.isUnlocked && achievement.progress < 1f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(achievement.progress)
                                .background(achievement.color.copy(alpha = 0.7f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationCardsSection(navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1500)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(800))
    ) {
        Column {
            Text(
                "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NavigationCard(
                    title = "Detailed Stats",
                    subtitle = "View complete analytics",
                    icon = Icons.Default.Analytics,
                    color = Color(0xFF4ECDC4),
                    onClick = { navController.navigate("statistics_detail") },
                    modifier = Modifier.weight(1f)
                )
                NavigationCard(
                    title = "Game History",
                    subtitle = "Review past games",
                    icon = Icons.Default.History,
                    color = Color(0xFF9C27B0),
                    onClick = { navController.navigate("game_history") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NavigationCard(
                    title = "Settings",
                    subtitle = "Customize preferences",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF607D8B),
                    onClick = { /* Navigate to settings */ },
                    modifier = Modifier.weight(1f)
                )
                NavigationCard(
                    title = "Friends",
                    subtitle = "Connect & compete",
                    icon = Icons.Default.People,
                    color = Color(0xFFFF9800),
                    onClick = { /* Navigate to friends */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.15f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    1.dp,
                    color.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyGamesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF37474F).copy(alpha = 0.3f),
                            Color(0xFF263238).copy(alpha = 0.3f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0xFF37474F).copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.SportsEsports,
                    contentDescription = "No games",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No battles yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    "Start playing to see your game history here",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumGameHistoryCard(game: GameHistory) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val resultColor = when (game.result) {
        GameResultStats.WIN -> Color(0xFF4CAF50)
        GameResultStats.LOSS -> Color(0xFFFF5252)
        GameResultStats.DRAW -> Color(0xFFFF9800)
    }

    val resultIcon = when (game.result) {
        GameResultStats.WIN -> Icons.Default.EmojiEvents
        GameResultStats.LOSS -> Icons.Default.Close
        GameResultStats.DRAW -> Icons.Default.Remove
    }

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            resultColor.copy(alpha = 0.1f),
                            resultColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    1.dp,
                    resultColor.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(resultColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                resultIcon,
                                contentDescription = game.result.name,
                                tint = resultColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "vs ${game.opponent}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                "${dateFormat.format(Date(game.date))} • ${timeFormat.format(Date(game.date))}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            game.result.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = resultColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (game.ratingChange >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = "Rating change",
                                tint = if (game.ratingChange >= 0) Color(0xFF4CAF50) else Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${if (game.ratingChange >= 0) "+" else ""}${game.ratingChange}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (game.ratingChange >= 0) Color(0xFF4CAF50) else Color(0xFFFF5252)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.2f))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GameDetailItem("Duration", "12:34")
                            GameDetailItem("Opening", "Sicilian")
                            GameDetailItem("Moves", "42")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}