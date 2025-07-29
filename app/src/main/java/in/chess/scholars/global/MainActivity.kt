package `in`.chess.scholars.global

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import `in`.chess.scholars.global.presentation.*
import `in`.chess.scholars.global.presentation.auth.AuthViewModel
import `in`.chess.scholars.global.presentation.auth.ForgotPasswordScreen
import `in`.chess.scholars.global.presentation.auth.LoginScreen
import `in`.chess.scholars.global.presentation.auth.RegisterScreen
import `in`.chess.scholars.global.presentation.game.ChessGameViewModel
import `in`.chess.scholars.global.presentation.game.GameScreen
import `in`.chess.scholars.global.presentation.home.HomeScreen
import `in`.chess.scholars.global.presentation.kyc.KycScreen
import `in`.chess.scholars.global.presentation.kyc.KycViewModel
import `in`.chess.scholars.global.presentation.matchmaking.LobbyScreen
import `in`.chess.scholars.global.presentation.matchmaking.MatchmakingViewModel
import `in`.chess.scholars.global.presentation.profile.ProfileScreen
import `in`.chess.scholars.global.presentation.rules.ChessRulesScreen
import `in`.chess.scholars.global.presentation.statistics.StatisticsScreen
import `in`.chess.scholars.global.presentation.statistics.StatisticsViewModel
import `in`.chess.scholars.global.presentation.wallet.WalletScreen
import `in`.chess.scholars.global.presentation.wallet.WalletViewModel
import `in`.chess.scholars.global.ui.theme.ScholarsChessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            ScholarsChessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // CORRECTED: Added a check for Google Play Services availability
                    CheckGooglePlayServices {
                        ChessBattleApp()
                    }
                }
            }
        }
    }
}

// CORRECTED: New Composable to handle Google Play Services check
@Composable
fun CheckGooglePlayServices(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    var playServicesStatus by remember { mutableStateOf<Int?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (status != ConnectionResult.SUCCESS) {
            playServicesStatus = status
            showErrorDialog = true
        } else {
            playServicesStatus = ConnectionResult.SUCCESS
        }
    }

    if (playServicesStatus == ConnectionResult.SUCCESS) {
        content()
    } else if (showErrorDialog && activity != null) {
        GooglePlayServicesErrorDialog(
            activity = activity,
            errorCode = playServicesStatus,
            onDismiss = {
                showErrorDialog = false
                activity.finish() // Close the app if services are not available
            }
        )
    } else {
        // Show a loading or placeholder screen while checking
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Checking services...", color = Color.White)
        }
    }
}

@Composable
fun GooglePlayServicesErrorDialog(activity: Activity, errorCode: Int?, onDismiss: () -> Unit) {
    if (errorCode == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Google Play Services Error") },
        text = { Text("This app requires Google Play Services to function correctly. Please install or update it.") },
        confirmButton = {
            TextButton(
                onClick = {
                    // Attempt to show the user a dialog to resolve the error.
                    GoogleApiAvailability.getInstance().showErrorDialogFragment(activity, errorCode, 0)
                    onDismiss()
                }
            ) {
                Text("Resolve")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close App")
            }
        }
    )
}


@Composable
fun ChessBattleApp() {
    val navController = rememberNavController()

    // Get the application instance and the DI container
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication
    val container = application.container

    // Use the custom factories to create ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(container))
    val matchmakingViewModel: MatchmakingViewModel = viewModel(factory = MatchmakingViewModelFactory(container))
    val walletViewModel: WalletViewModel = viewModel(factory = WalletViewModelFactory(container))
    val statisticsViewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModelFactory(container))
    val kycViewModel: KycViewModel = viewModel(factory = KycViewModelFactory(container))
    val gameViewModel: ChessGameViewModel = viewModel(factory = GameViewModelFactory(container))

    val authState by authViewModel.authState.collectAsState()

    // CORRECTED: Use a key for startDestination to ensure NavHost recomposes when auth state changes after initial composition.
    NavHost(navController = navController, startDestination = if (authState.isLoggedIn) "home" else "login") {
        // Authentication Flow
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController, authViewModel) }
        composable("forgot_password") { ForgotPasswordScreen(navController, authViewModel) }

        // Main App Flow
        composable("home") { HomeScreen(navController, authViewModel) }
        composable("lobby?mode={mode}&bet={bet}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "QUICK"
            val betAmount = backStackEntry.arguments?.getString("bet")?.toFloatOrNull() ?: 100f
            val currentUserData by authViewModel.userData.collectAsState()
            LobbyScreen(navController, matchmakingViewModel, currentUserData, betAmount)
        }
        composable("game/{gameId}/{betAmount}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            val betAmount = backStackEntry.arguments?.getString("betAmount")?.toFloatOrNull() ?: 0f
            GameScreen(gameId, betAmount, navController, gameViewModel)
        }
        composable("wallet") { WalletScreen(navController, walletViewModel) }
        composable("profile") { ProfileScreen(navController, statisticsViewModel) }
        composable("statistics") { StatisticsScreen(navController, statisticsViewModel) }
        composable("statistics_detail") { StatisticsScreen(navController, statisticsViewModel) }
        composable("kyc") { KycScreen(navController, kycViewModel) }
        composable("rules") { ChessRulesScreen(navController) }
    }
}
