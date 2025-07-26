package `in`.chess.scholars.global.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.presentation.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var showBetDialog by remember { mutableStateOf(false) }
    var selectedGameMode by remember { mutableStateOf("QUICK") }
    val userData by authViewModel.userData.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Premium animated background
        PremiumBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                HomeTopBar(navController, userData)
            },
            bottomBar = {
                PremiumBottomBar(navController)
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Welcome Section
                item {
                    WelcomeSection(userData)
                }

                // Balance Card with animation
                item {
                    AnimatedBalanceCard(userData, navController)
                }

                // Quick Stats
                item {
                    QuickStatsRow(userData)
                }

                // Game Modes Section
                item {
                    Text(
                        "Choose Your Battle",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    GameModesSection { mode ->
                        selectedGameMode = mode
                        showBetDialog = true
                    }
                }

                // Special Features
                item {
                    SpecialFeaturesSection(navController)
                }

                // Active Tournaments
                item {
                    ActiveTournamentsSection(navController)
                }
            }
        }

        // Bet Dialog
        if (showBetDialog) {
            PremiumBetDialog(
                onDismiss = { showBetDialog = false },
                onConfirm = { amount ->
                    showBetDialog = false
                    navController.navigate("lobby?mode=$selectedGameMode&bet=$amount")
                }
            )
        }
    }
}

@Composable
private fun ActiveTournamentsSection(navController: NavController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Active Tournaments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = { /* View all tournaments */ }) {
                Text("View All", color = Color(0xFF4ECDC4))
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(3) { index ->
                TournamentCard(
                    title = when(index) {
                        0 -> "Weekend Warrior"
                        1 -> "Grandmaster Challenge"
                        else -> "Blitz Championship"
                    },
                    prize = when(index) {
                        0 -> "₹50,000"
                        1 -> "₹1,00,000"
                        else -> "₹25,000"
                    },
                    participants = when(index) {
                        0 -> "128/256"
                        1 -> "64/64"
                        else -> "200/512"
                    },
                    timeLeft = when(index) {
                        0 -> "2 days"
                        1 -> "Starting soon"
                        else -> "5 hours"
                    },
                    onClick = { /* Navigate to tournament */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TournamentCard(
    title: String,
    prize: String,
    participants: String,
    timeLeft: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2a2a4e).copy(alpha = 0.9f)
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFD700).copy(alpha = 0.5f),
                    Color(0xFFFFA000).copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background pattern
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.05f)
            ) {
                val path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(size.width * 0.7f, 0f)
                    lineTo(size.width, size.height * 0.4f)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color(0xFFFFD700)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            timeLeft,
                            fontSize = 12.sp,
                            color = Color(0xFF4ECDC4)
                        )
                    }

                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Tournament",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Prize Pool",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            prize,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Participants",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            participants,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumBottomBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.9f),
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val items = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("statistics", "Stats", Icons.Default.QueryStats),
            Triple("wallet", "Wallet", Icons.Default.AccountBalanceWallet),
            Triple("profile", "Profile", Icons.Default.Person)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 12.sp
                    )
                },
                selected = route == "home",
                onClick = {
                    if (route != "home") {
                        navController.navigate(route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4ECDC4),
                    selectedTextColor = Color(0xFF4ECDC4),
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    unselectedTextColor = Color.White.copy(alpha = 0.5f),
                    indicatorColor = Color(0xFF4ECDC4).copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun PremiumButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF2C7A7B)
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        },
        border = if (!isPrimary) BorderStroke(1.dp, Color.White) else null
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumBetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var selectedAmount by remember { mutableStateOf<Float?>(null) }
    var customAmount by remember { mutableStateOf("") }
    val predefinedAmounts = listOf(100f, 500f, 1000f, 5000f)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.clip(RoundedCornerShape(24.dp))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1a1a2e)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = "Bet",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Select Bet Amount",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Choose your stake wisely",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Predefined amounts
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(predefinedAmounts) { amount ->
                        FilterChip(
                            selected = selectedAmount == amount,
                            onClick = {
                                selectedAmount = amount
                                customAmount = ""
                            },
                            label = {
                                Text(
                                    "₹${amount.toInt()}",
                                    fontWeight = if (selectedAmount == amount) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4ECDC4),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom amount
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            customAmount = it
                            selectedAmount = null
                        }
                    },
                    label = { Text("Custom Amount") },
                    prefix = { Text("₹") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4ECDC4),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF4ECDC4)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                        onClick = {
                            val amount = selectedAmount ?: customAmount.toFloatOrNull()
                            amount?.let { onConfirm(it) }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedAmount != null || customAmount.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4ECDC4),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
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
                    Color(0xFF16213E)
                )
            )
        )

        // Animated wave patterns
        val waveHeight = 150.dp.toPx()
        val waveCount = 3

        for (i in 0 until waveCount) {
            val path = Path().apply {
                moveTo(0f, size.height * (0.3f + i * 0.2f))

                for (x in 0..size.width.toInt() step 10) {
                    val y = sin(x * 0.01f + waveOffset + i * PI / 3).toFloat() * waveHeight / (i + 1)
                    lineTo(x.toFloat(), size.height * (0.3f + i * 0.2f) + y)
                }

                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = when(i) {
                    0 -> Color(0xFF4ECDC4).copy(alpha = 0.1f)
                    1 -> Color(0xFF44A3A0).copy(alpha = 0.05f)
                    else -> Color(0xFF2C7A7B).copy(alpha = 0.03f)
                }
            )
        }

        // Floating particles
        val particleCount = 30
        for (i in 0 until particleCount) {
            val x = (sin(waveOffset * 0.5f + i) + 1) * size.width / 2
            val y = (cos(waveOffset * 0.3f + i * 2) + 1) * size.height / 2
            val radius = 2.dp.toPx() * (sin(waveOffset + i) + 2)

            drawCircle(
                color = Color(0xFF4ECDC4).copy(alpha = 0.3f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(navController: NavController, userData: UserData?) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Animated Logo
                AnimatedLogo()
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Chess Battle",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Online",
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                }
            }
        },
        actions = {
            // Notification Badge
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color(0xFFFF5252)
                    ) {
                        Text("3", fontSize = 10.sp)
                    }
                }
            ) {
                IconButton(onClick = { /* Handle notifications */ }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }

            // Profile Avatar
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4ECDC4),
                                    Color(0xFF44A3A0)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        userData?.displayName?.firstOrNull()?.toString() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .rotate(rotation)
            .background(
                Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF4ECDC4),
                        Color(0xFF44A3A0),
                        Color(0xFF2C7A7B),
                        Color(0xFF4ECDC4)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Games,
            contentDescription = "Logo",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun WelcomeSection(userData: UserData?) {
    val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Column {
        Text(
            "$greeting, ${userData?.displayName ?: "Champion"}!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            "Ready to conquer the board?",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun AnimatedBalanceCard(userData: UserData?, navController: NavController) {
    var isExpanded by remember { mutableStateOf(false) }
    val animatedSize by animateDpAsState(
        targetValue = if (isExpanded) 180.dp else 120.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedSize)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
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
                            Color(0xFF4ECDC4).copy(alpha = 0.9f),
                            Color(0xFF44A3A0).copy(alpha = 0.9f),
                            Color(0xFF2C7A7B).copy(alpha = 0.9f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
        ) {
            // Decorative pattern
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f)
            ) {
                val patternSize = 40.dp.toPx()
                for (x in 0..size.width.toInt() step patternSize.toInt()) {
                    for (y in 0..size.height.toInt() step patternSize.toInt()) {
                        drawCircle(
                            color = Color.White,
                            radius = patternSize / 4,
                            center = Offset(x.toFloat(), y.toFloat())
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Total Balance",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            "₹${String.format("%.2f", userData?.balance ?: 0.0)}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Animated coin icon
                    AnimatedCoinIcon()
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PremiumButton(
                            text = "Add Money",
                            icon = Icons.Default.Add,
                            onClick = { navController.navigate("wallet") },
                            modifier = Modifier.weight(1f)
                        )
                        PremiumButton(
                            text = "Withdraw",
                            icon = Icons.Default.AccountBalance,
                            onClick = { navController.navigate("wallet") },
                            modifier = Modifier.weight(1f),
                            isPrimary = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedCoinIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.MonetizationOn,
            contentDescription = "Coin",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun QuickStatsRow(userData: UserData?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            title = "Games",
            value = "${userData?.gamesPlayed ?: 0}",
            icon = Icons.Default.Games,
            color = Color(0xFF2196F3),
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Wins",
            value = "${userData?.wins ?: 0}",
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Rating",
            value = "${userData?.rating ?: 1200}",
            icon = Icons.Default.Star,
            color = Color(0xFFFFD700),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                title,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun GameModesSection(onModeSelected: (String) -> Unit) {
    Column {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                GameModeCard(
                    title = "Quick Match",
                    subtitle = "5 min • Real Money",
                    description = "Fast-paced battles",
                    icon = Icons.Default.FlashOn,
                    gradientColors = listOf(
                        Color(0xFF4ECDC4),
                        Color(0xFF44A3A0)
                    ),
                    onClick = { onModeSelected("QUICK") }
                )
            }
            item {
                GameModeCard(
                    title = "Tournament",
                    subtitle = "Win Big Prizes",
                    description = "Compete with the best",
                    icon = Icons.Default.EmojiEvents,
                    gradientColors = listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFFA000)
                    ),
                    onClick = { onModeSelected("TOURNAMENT") }
                )
            }
            item {
                GameModeCard(
                    title = "Blitz",
                    subtitle = "3 min • High Stakes",
                    description = "Lightning fast",
                    icon = Icons.Default.Bolt,
                    gradientColors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFFF1744)
                    ),
                    onClick = { onModeSelected("BLITZ") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(140.dp),
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
                        colors = gradientColors.map { it.copy(alpha = 0.9f) }
                    )
                )
        ) {
            // Decorative elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = 100.dp.toPx(),
                    center = Offset(size.width * 0.8f, size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = 80.dp.toPx(),
                    center = Offset(size.width * 0.1f, size.height * 0.9f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        subtitle,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialFeaturesSection(navController: NavController) {
    Column {
        Text(
            "Special Features",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                title = "2 Player Offline",
                subtitle = "Pass and Play",
                icon = Icons.Default.School,
                color = Color(0xFF9C27B0),
                onClick = { navController.navigate("game/offline_game/0") }, // Use the new ID
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                title = "Learn",
                subtitle = "Chess Rules",
                icon = Icons.Default.MenuBook,
                color = Color(0xFF2196F3),
                onClick = { navController.navigate("rules") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureCard(
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
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
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