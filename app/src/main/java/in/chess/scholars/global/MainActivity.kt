package `in`.chess.scholars.global

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChessBattleApp()
                }
            }
        }
    }
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
    val startDestination = if (authState.isLoggedIn) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
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