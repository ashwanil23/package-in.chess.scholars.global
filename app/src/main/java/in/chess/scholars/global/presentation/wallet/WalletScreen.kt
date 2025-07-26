//package `in`.chess.scholars.global.presentation.wallet
//
//import `in`.chess.scholars.global.domain.model.KycStatus
//import android.app.Activity
//import androidx.compose.animation.animateContentSize
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import `in`.chess.scholars.global.domain.model.Transaction
//import `in`.chess.scholars.global.domain.model.TransactionStatus
//import `in`.chess.scholars.global.domain.model.TransactionType
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun WalletScreen(
//    navController: NavController,
//    viewModel: WalletViewModel
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//    val activity = context as? Activity
//
//    var showAddMoneyDialog by remember { mutableStateOf(false) }
//    var showWithdrawDialog by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Wallet", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
//            if (uiState.isLoading && uiState.transactions.isEmpty()) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            } else {
//                LazyColumn(
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    item {
//                        BalanceCard(
//                            balance = uiState.currentUserData?.balance ?: 0.0,
//                            onAddMoney = { showAddMoneyDialog = true },
//                            onWithdraw = { showWithdrawDialog = true }
//                        )
//                    }
//                    item {
//                        Text(
//                            "Transaction History",
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White
//                        )
//                    }
//                    if (uiState.transactions.isEmpty()) {
//                        item {
//                            EmptyState()
//                        }
//                    } else {
//                        items(uiState.transactions) { transaction ->
//                            TransactionCard(transaction)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    if (showAddMoneyDialog) {
//        AddMoneyDialog(
//            onDismiss = { showAddMoneyDialog = false },
//            onConfirm = { amount ->
//                showAddMoneyDialog = false
//                // In a real app, you'd get the transactionId from the payment SDK
//                val mockTxnId = "mock_txn_${UUID.randomUUID()}"
//                viewModel.deposit(amount, mockTxnId)
//            }
//        )
//    }
//
//    if (showWithdrawDialog) {
//        WithdrawDialog(
//            balance = uiState.currentUserData?.balance ?: 0.0,
//            kycStatus = uiState.currentUserData?.kycStatus ?: KycStatus.NOT_STARTED,
//            onDismiss = { showWithdrawDialog = false },
//            onConfirm = { amount ->
//                showWithdrawDialog = false
//                viewModel.withdraw(amount)
//            },
//            onKycRequired = {
//                showWithdrawDialog = false
//                navController.navigate("kyc")
//            }
//        )
//    }
//}
//
//@Composable
//private fun BalanceCard(balance: Double, onAddMoney: () -> Unit, onWithdraw: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .animateContentSize(animationSpec = tween(300)),
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.9f))
//    ) {
//        Column(
//            modifier = Modifier.padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Available Balance", fontSize = 16.sp, color = Color.Gray)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "₹${String.format("%.2f", balance)}",
//                fontSize = 48.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Button(
//                    onClick = onAddMoney,
//                    modifier = Modifier.weight(1f),
//                    shape = RoundedCornerShape(12.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
//                ) {
//                    Icon(Icons.Default.Add, contentDescription = "Add Money")
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Add Money")
//                }
//                OutlinedButton(
//                    onClick = onWithdraw,
//                    modifier = Modifier.weight(1f),
//                    shape = RoundedCornerShape(12.dp),
//                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
//                ) {
//                    Icon(Icons.Default.AccountBalance, contentDescription = "Withdraw")
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Withdraw", color = Color(0xFF4CAF50))
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun TransactionCard(transaction: Transaction) {
//    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
//    val (icon, color, sign) = when (transaction.type) {
//        TransactionType.DEPOSIT -> Triple(Icons.Default.ArrowDownward, Color(0xFF4CAF50), "+")
//        TransactionType.GAME_WIN -> Triple(Icons.Default.EmojiEvents, Color(0xFF4CAF50), "+")
//        TransactionType.WITHDRAWAL -> Triple(Icons.Default.ArrowUpward, Color(0xFF2196F3), "-")
//        TransactionType.GAME_ENTRY -> Triple(Icons.Default.Games, Color(0xFFFF5252), "-")
//        TransactionType.REFUND -> Triple(Icons.Default.Refresh, Color(0xFFFF9800), "+")
//    }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e).copy(alpha = 0.7f))
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .background(color.copy(alpha = 0.2f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(icon, contentDescription = transaction.type.name, tint = color)
//            }
//            Spacer(modifier = Modifier.width(12.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(transaction.description, color = Color.White, fontWeight = FontWeight.Medium)
//                Text(dateFormat.format(Date(transaction.timestamp)), color = Color.Gray, fontSize = 12.sp)
//            }
//            Column(horizontalAlignment = Alignment.End) {
//                Text("$sign ₹${transaction.amount}", color = color, fontWeight = FontWeight.Bold)
//                if (transaction.status == TransactionStatus.PENDING) {
//                    Text("Pending", color = Color(0xFFFF9800), fontSize = 12.sp)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun EmptyState() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(48.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("No Transactions Yet", fontSize = 18.sp, color = Color.Gray)
//        Text("Your transaction history will appear here.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
//    }
//}
//
//@Composable
//private fun AddMoneyDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
//    // Implementation for the Add Money dialog
//}
//
//@Composable
//private fun WithdrawDialog(
//    balance: Double,
//    kycStatus: KycStatus,
//    onDismiss: () -> Unit,
//    onConfirm: (Double) -> Unit,
//    onKycRequired: () -> Unit
//) {
//    // Implementation for the Withdraw dialog
//}































package `in`.chess.scholars.global.presentation.wallet

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    viewModel: WalletViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Premium background
        PremiumWalletBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                WalletTopBar(navController)
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main balance card
                item {
                    AnimatedBalanceCard(
                        balance = uiState.currentUserData?.balance ?: 0.0,
                        onAddMoney = { showAddMoneyDialog = true },
                        onWithdraw = { showWithdrawDialog = true }
                    )
                }

                // Financial stats
                item {
                    FinancialStatsSection(uiState)
                }

                // Transaction filters
                item {
                    TransactionFilters(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }

                // Transaction history header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Transaction History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(onClick = { /* Export transactions */ }) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Export",
                                tint = Color(0xFF4ECDC4)
                            )
                        }
                    }
                }

                // Transactions list
                if (uiState.isLoading && uiState.transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF4ECDC4)
                            )
                        }
                    }
                } else if (uiState.transactions.isEmpty()) {
                    item {
                        EmptyTransactionsState()
                    }
                } else {
                    val filteredTransactions = if (selectedFilter != null) {
                        uiState.transactions.filter { it.type == selectedFilter }
                    } else {
                        uiState.transactions
                    }

                    items(filteredTransactions) { transaction ->
                        PremiumTransactionCard(transaction)
                    }
                }
            }
        }

        // Dialogs
        if (showAddMoneyDialog) {
            PremiumAddMoneyDialog(
                onDismiss = { showAddMoneyDialog = false },
                onConfirm = { amount ->
                    showAddMoneyDialog = false
                    val mockTxnId = "TXN_${UUID.randomUUID()}"
                    viewModel.deposit(amount, mockTxnId)
                }
            )
        }

        if (showWithdrawDialog) {
            PremiumWithdrawDialog(
                balance = uiState.currentUserData?.balance ?: 0.0,
                kycStatus = uiState.currentUserData?.kycStatus ?: KycStatus.NOT_STARTED,
                onDismiss = { showWithdrawDialog = false },
                onConfirm = { amount ->
                    showWithdrawDialog = false
                    viewModel.withdraw(amount)
                },
                onKycRequired = {
                    showWithdrawDialog = false
                    navController.navigate("kyc")
                }
            )
        }
    }
}

@Composable
private fun PremiumWalletBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
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

        // Animated coin patterns
        val coinRadius = 30.dp.toPx()
        val coinCount = 15

        for (i in 0 until coinCount) {
            val x = (sin(waveOffset * 0.7f + i * PI / 3) + 1) * size.width / 2
            val y = (i * size.height / coinCount) + sin(waveOffset + i) * 50
            val alpha = (sin(waveOffset * 2 + i) + 1) / 4 + 0.1f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = alpha),
                        Color(0xFFFFA000).copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(x.toFloat(), y),
                    radius = coinRadius * 2
                ),
                center = Offset(x.toFloat(), y),
                radius = coinRadius
            )
        }

        // Grid pattern overlay
        val gridSize = 50.dp.toPx()
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color(0xFF4ECDC4).copy(alpha = 0.03f),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color(0xFF4ECDC4).copy(alpha = 0.03f),
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletTopBar(navController: NavController) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = "Wallet",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "My Wallet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Show wallet settings */ }) {
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
private fun AnimatedBalanceCard(
    balance: Double,
    onAddMoney: () -> Unit,
    onWithdraw: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 280.dp else 200.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = 0.9f),
                                Color(0xFFFFA000).copy(alpha = 0.9f),
                                Color(0xFFFF6F00).copy(alpha = 0.9f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
            )

            // Pattern overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val patternRadius = 80.dp.toPx()
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = patternRadius,
                    center = Offset(size.width * 0.9f, size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = patternRadius * 1.5f,
                    center = Offset(size.width * 0.1f, size.height * 0.8f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Balance display
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                "Available Balance",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )

                            AnimatedBalanceCounter(balance)

                            Text(
                                "Indian Rupees",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        // Animated coin icon
                        AnimatedCoinStack()
                    }
                }

                // Action buttons
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Divider(color = Color.White.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PremiumActionButton(
                                text = "Add Money",
                                icon = Icons.Default.Add,
                                onClick = onAddMoney,
                                modifier = Modifier.weight(1f),
                                isPrimary = true
                            )

                            PremiumActionButton(
                                text = "Withdraw",
                                icon = Icons.Default.AccountBalance,
                                onClick = onWithdraw,
                                modifier = Modifier.weight(1f),
                                isPrimary = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedBalanceCounter(balance: Double) {
    var displayBalance by remember { mutableStateOf(0.0) }

    LaunchedEffect(balance) {
        val steps = 20
        val stepDelay = 30L
        val increment = (balance - displayBalance) / steps

        repeat(steps) {
            displayBalance += increment
            delay(stepDelay)
        }
        displayBalance = balance
    }

    Text(
        text = "₹${String.format("%.2f", displayBalance)}",
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun AnimatedCoinStack() {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Coin stack
        repeat(3) { index ->
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = "Coin",
                tint = Color.White.copy(alpha = 0.9f - index * 0.2f),
                modifier = Modifier
                    .size(60.dp - index * 8.dp)
                    .offset(y = (-index * 8 - bounce).dp)
                    .rotate(rotation + index * 30f)
            )
        }
    }
}

@Composable
private fun FinancialStatsSection(uiState: WalletUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FinancialStatCard(
            title = "Total Deposits",
            amount = uiState.totalDeposits,
            icon = Icons.Default.ArrowDownward,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )

        FinancialStatCard(
            title = "Total Winnings",
            amount = uiState.totalWinnings,
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFFFFD700),
            modifier = Modifier.weight(1f)
        )

        FinancialStatCard(
            title = "Withdrawn",
            amount = uiState.totalWithdrawals,
            icon = Icons.Default.ArrowUpward,
            color = Color(0xFF2196F3),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FinancialStatCard(
    title: String,
    amount: Double,
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "₹${String.format("%.0f", amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                title,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransactionFilters(
    selectedFilter: TransactionType?,
    onFilterSelected: (TransactionType?) -> Unit
) {
    val filters = listOf(
        null to "All",
        TransactionType.DEPOSIT to "Deposits",
        TransactionType.WITHDRAWAL to "Withdrawals",
        TransactionType.GAME_WIN to "Winnings",
        TransactionType.GAME_ENTRY to "Game Entry"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        filters.forEach { (type, label) ->
            item {
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { onFilterSelected(type) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4ECDC4),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.1f),
                        labelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.White.copy(alpha = 0.2f),
                        selectedBorderColor = Color(0xFF4ECDC4),
                        borderWidth = 1.dp,
                        enabled = true,
                        selected = selectedFilter == type
                    )
                )
            }
        }
    }
}

@Composable
private fun PremiumTransactionCard(transaction: Transaction) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }

    val (icon, color) = when (transaction.type) {
        TransactionType.DEPOSIT -> Icons.Default.ArrowDownward to Color(0xFF4CAF50)
        TransactionType.GAME_WIN -> Icons.Default.EmojiEvents to Color(0xFFFFD700)
        TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward to Color(0xFF2196F3)
        TransactionType.GAME_ENTRY -> Icons.Default.Games to Color(0xFFFF5252)
        TransactionType.REFUND -> Icons.Default.Refresh to Color(0xFFFF9800)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f),
                    color.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with animated background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = 0.2f),
                                color.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = transaction.type.name,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Transaction details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        dateFormat.format(Date(transaction.timestamp)),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    if (transaction.status == TransactionStatus.PENDING) {
                        Badge(
                            containerColor = Color(0xFFFF9800).copy(alpha = 0.2f),
                            contentColor = Color(0xFFFF9800)
                        ) {
                            Text("Pending", fontSize = 10.sp)
                        }
                    }
                }
            }

            // Amount
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (transaction.isCredit) "+" else "-"} ₹${String.format("%.2f", transaction.amount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isCredit) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )

                Text(
                    "Balance: ₹${String.format("%.2f", transaction.balanceAfter)}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun EmptyTransactionsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4ECDC4).copy(alpha = 0.1f),
                            Color(0xFF4ECDC4).copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = "No transactions",
                tint = Color(0xFF4ECDC4),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "No Transactions Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            "Your transaction history will appear here",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun PremiumActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1a1a2e)
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        },
        border = if (!isPrimary) BorderStroke(2.dp, Color.White) else null
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumAddMoneyDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var selectedAmount by remember { mutableStateOf<Double?>(null) }
    var customAmount by remember { mutableStateOf("") }
    val predefinedAmounts = listOf(100.0, 500.0, 1000.0, 5000.0, 10000.0)

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
                // Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50),
                                    Color(0xFF45B649)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Money",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Add Money to Wallet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Choose amount to add",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Predefined amounts grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    predefinedAmounts.chunked(2).forEach { rowAmounts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowAmounts.forEach { amount ->
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
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF4CAF50),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White.copy(alpha = 0.1f),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom amount input
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            customAmount = it
                            selectedAmount = null
                        }
                    },
                    label = { Text("Custom Amount") },
                    prefix = { Text("₹") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
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
                            val amount = selectedAmount ?: customAmount.toDoubleOrNull()
                            amount?.let { onConfirm(it) }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedAmount != null || customAmount.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    ) {
                        Text("Add Money")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumWithdrawDialog(
    balance: Double,
    kycStatus: KycStatus,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
    onKycRequired: () -> Unit
) {
    if (kycStatus != KycStatus.VERIFIED) {
        KycRequiredDialog(
            onDismiss = onDismiss,
            onProceed = onKycRequired
        )
    } else {
        var withdrawAmount by remember { mutableStateOf("") }

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
                    // Header
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2196F3),
                                        Color(0xFF1976D2)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = "Withdraw",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Withdraw Money",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        "Available: ₹${String.format("%.2f", balance)}",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Amount input
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) {
                                withdrawAmount = it
                            }
                        },
                        label = { Text("Withdrawal Amount") },
                        prefix = { Text("₹") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = withdrawAmount.toDoubleOrNull()?.let { it > balance } ?: false
                    )

                    if (withdrawAmount.toDoubleOrNull()?.let { it > balance } == true) {
                        Text(
                            "Insufficient balance",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Withdrawal Information",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "• Minimum withdrawal: ₹100\n• Processing time: 24-48 hours\n• Funds will be credited to your registered bank account",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
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
                                withdrawAmount.toDoubleOrNull()?.let { amount ->
                                    if (amount <= balance && amount >= 100) {
                                        onConfirm(amount)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = withdrawAmount.toDoubleOrNull()?.let {
                                it <= balance && it >= 100
                            } ?: false,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        ) {
                            Text("Withdraw")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KycRequiredDialog(
    onDismiss: () -> Unit,
    onProceed: () -> Unit
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
                    Icons.Default.VerifiedUser,
                    contentDescription = "KYC Required",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "KYC Verification Required",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Complete your KYC to enable withdrawals",
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
                        Text("Later", color = Color.White)
                    }

                    Button(
                        onClick = onProceed,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Text("Complete KYC")
                    }
                }
            }
        }
    }
}