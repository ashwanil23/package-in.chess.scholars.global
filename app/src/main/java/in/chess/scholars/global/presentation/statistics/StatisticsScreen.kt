package `in`.chess.scholars.global.presentation.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.GameHistory
import `in`.chess.scholars.global.domain.model.GameResultStats
import `in`.chess.scholars.global.domain.model.RatingPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Rating", "History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Statistics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshStatistics() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1a1a2e),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF16213e)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1a1a2e)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF16213e),
                                Color(0xFF0f3460)
                            )
                        )
                    )
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    uiState.error != null -> Text(uiState.error!!, modifier = Modifier.align(Alignment.Center))
                    else -> {
                        when (selectedTab) {
                            0 -> OverviewTab(uiState)
                            1 -> RatingTab(uiState)
                            2 -> HistoryTab(uiState)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(uiState: StatisticsUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WinRateChart(uiState)
        }
        // Add other overview cards here, e.g., streaks, performance vs colors, etc.
    }
}

@Composable
private fun RatingTab(uiState: StatisticsUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            RatingProgressGraph(uiState.ratingHistory)
        }
    }
}

@Composable
private fun HistoryTab(uiState: StatisticsUiState) {
    if (uiState.gameHistory.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No game history found.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.gameHistory) { game ->
                GameHistoryCard(game)
            }
        }
    }
}

@Composable
private fun WinRateChart(uiState: StatisticsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Performance Breakdown", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            if (uiState.totalGames == 0) {
                Text("Play some games to see your stats!", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                val winAngle by animateFloatAsState(targetValue = (uiState.wins.toFloat() / uiState.totalGames) * 360f, animationSpec = tween(1000))
                val drawAngle by animateFloatAsState(targetValue = (uiState.draws.toFloat() / uiState.totalGames) * 360f, animationSpec = tween(1000))
                val lossAngle by animateFloatAsState(targetValue = (uiState.losses.toFloat() / uiState.totalGames) * 360f, animationSpec = tween(1000))

                Box(modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(Color(0xFF4CAF50), -90f, winAngle, false, style = Stroke(20.dp.toPx(), cap = StrokeCap.Butt))
                        drawArc(Color(0xFFFF9800), -90f + winAngle, drawAngle, false, style = Stroke(20.dp.toPx(), cap = StrokeCap.Butt))
                        drawArc(Color(0xFFFF5252), -90f + winAngle + drawAngle, lossAngle, false, style = Stroke(20.dp.toPx(), cap = StrokeCap.Butt))
                    }
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text("${uiState.winRate.toInt()}%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Win Rate", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingProgressGraph(ratingHistory: List<RatingPoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Rating Progress", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            if (ratingHistory.isEmpty()) {
                Text("Play more games to see your progress.", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    val path = Path()
                    val minRating = ratingHistory.minOf { it.rating }
                    val maxRating = ratingHistory.maxOf { it.rating }
                    val ratingRange = (maxRating - minRating).toFloat().coerceAtLeast(1f)

                    ratingHistory.forEachIndexed { index, point ->
                        val x = (index.toFloat() / (ratingHistory.size - 1)) * size.width
                        val y = size.height - ((point.rating - minRating) / ratingRange) * size.height
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, Color(0xFF4CAF50), style = Stroke(3.dp.toPx()))
                }
            }
        }
    }
}

@Composable
private fun GameHistoryCard(game: GameHistory) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val resultColor = when (game.result) {
        GameResultStats.WIN -> Color(0xFF4CAF50)
        GameResultStats.LOSS -> Color(0xFFFF5252)
        GameResultStats.DRAW -> Color(0xFFFF9800)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("vs ${game.opponent}", color = Color.White, fontWeight = FontWeight.Medium)
                Text(dateFormat.format(Date(game.date)), color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(game.result.name, color = resultColor, fontWeight = FontWeight.Bold)
                Text(
                    "${if (game.ratingChange >= 0) "+" else ""}${game.ratingChange}",
                    color = if (game.ratingChange >= 0) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )
            }
        }
    }
}

