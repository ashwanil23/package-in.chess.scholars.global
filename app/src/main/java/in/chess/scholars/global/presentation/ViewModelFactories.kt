package `in`.chess.scholars.global.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.chess.scholars.global.di.DIContainer
import `in`.chess.scholars.global.presentation.auth.AuthViewModel
import `in`.chess.scholars.global.presentation.game.ChessGameViewModel
import `in`.chess.scholars.global.presentation.kyc.KycViewModel
import `in`.chess.scholars.global.presentation.matchmaking.MatchmakingViewModel
import `in`.chess.scholars.global.presentation.statistics.StatisticsViewModel
import `in`.chess.scholars.global.presentation.wallet.WalletViewModel

/**
 * Factory for creating AuthViewModel instances.
 * It takes the DIContainer to provide the necessary use cases to the ViewModel.
 */
class AuthViewModelFactory(private val container: DIContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                getAuthStateUseCase = container.getAuthStateUseCase,
                signInUseCase = container.signInUseCase,
                signUpUseCase = container.signUpUseCase,
                signOutUseCase = container.signOutUseCase,
                resetPasswordUseCase = container.resetPasswordUseCase,
                getCurrentUserIdUseCase = container.getCurrentUserIdUseCase,
                getUserDataUseCase = container.getUserDataUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory for creating MatchmakingViewModel instances.
 */
class MatchmakingViewModelFactory(private val container: DIContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchmakingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchmakingViewModel(
                findMatchUseCase = container.findMatchUseCase,
                cancelMatchmakingUseCase = container.cancelMatchmakingUseCase,
                getUserDataUseCase = container.getUserDataUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory for creating WalletViewModel instances.
 */
class WalletViewModelFactory(private val container: DIContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(
                getCurrentUserIdUseCase = container.getCurrentUserIdUseCase,
                getUserDataUseCase = container.getUserDataUseCase,
                getTransactionsUseCase = container.getTransactionsUseCase,
                depositUseCase = container.depositUseCase,
                withdrawUseCase = container.withdrawUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory for creating StatisticsViewModel instances.
 */
class StatisticsViewModelFactory(private val container: DIContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(
                getCurrentUserIdUseCase = container.getCurrentUserIdUseCase,
                getUserDataUseCase = container.getUserDataUseCase,
                getGameHistoryUseCase = container.getGameHistoryUseCase,
                getRatingHistoryUseCase = container.getRatingHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory for creating KycViewModel instances.
 */
class KycViewModelFactory(private val container: DIContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KycViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KycViewModel(
                getCurrentUserIdUseCase = container.getCurrentUserIdUseCase,
                getUserDataUseCase = container.getUserDataUseCase,
                submitKycUseCase = container.submitKycUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory for creating ChessGameViewModel instances.
 */
class GameViewModelFactory(private val container: DIContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChessGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChessGameViewModel(
                getGameStreamUseCase = container.getGameStreamUseCase,
                updateGameUseCase = container.updateGameUseCase,
                endGameUseCase = container.endGameUseCase,
                getUserDataUseCase = container.getUserDataUseCase,
                getCurrentUserIdUseCase = container.getCurrentUserIdUseCase,
                getChatStreamUseCase = container.getChatStreamUseCase,
                sendMessageUseCase = container.sendMessageUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
