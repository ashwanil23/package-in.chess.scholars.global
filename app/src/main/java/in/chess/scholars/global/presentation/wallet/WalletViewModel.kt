package `in`.chess.scholars.global.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.usecases.DepositUseCase
import `in`.chess.scholars.global.domain.usecases.GetTransactionsUseCase
import `in`.chess.scholars.global.domain.usecases.GetUserDataUseCase
import `in`.chess.scholars.global.domain.usecases.WithdrawUseCase
import `in`.chess.scholars.global.domain.model.Transaction
import `in`.chess.scholars.global.domain.model.TransactionType
import `in`.chess.scholars.global.domain.usecases.GetCurrentUserIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Wallet screen.
 */
data class WalletUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentUserData: UserData? = null,
    val transactions: List<Transaction> = emptyList(),
    val totalDeposits: Double = 0.0,
    val totalWinnings: Double = 0.0,
    val totalWithdrawals: Double = 0.0
)

/**
 * ViewModel for the Wallet screen.
 */
class WalletViewModel(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val depositUseCase: DepositUseCase,
    private val withdrawUseCase: WithdrawUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        val userId = getCurrentUserIdUseCase()
        if (userId == null) {
            _uiState.value = WalletUiState(isLoading = false, error = "User not logged in.")
            return
        }

        // Combine two flows: one for user data (balance) and one for transactions.
        // This ensures the UI state is updated whenever either data source changes.
        getUserDataUseCase(userId)
            .combine(getTransactionsUseCase(userId)) { userDataResult, transactionsResult ->
                // Handle the result for user data
                val newUserData = if (userDataResult is DataResult.Success) userDataResult.data else _uiState.value.currentUserData

                // Handle the result for transactions
                val newTransactions = if (transactionsResult is DataResult.Success) transactionsResult.data else _uiState.value.transactions

                // Calculate stats from the new transaction list
                val totalDeposits = newTransactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount }
                val totalWinnings = newTransactions.filter { it.type == TransactionType.GAME_WIN }.sumOf { it.amount }
                val totalWithdrawals = newTransactions.filter { it.type == TransactionType.WITHDRAWAL }.sumOf { it.amount }

                // Determine error state
                val error = if (userDataResult is DataResult.Error) {
                    userDataResult.exception.message
                } else if (transactionsResult is DataResult.Error) {
                    transactionsResult.exception.message
                } else {
                    null
                }

                // Update the final UI state
                _uiState.value = WalletUiState(
                    isLoading = false,
                    error = error,
                    currentUserData = newUserData,
                    transactions = newTransactions,
                    totalDeposits = totalDeposits,
                    totalWinnings = totalWinnings,
                    totalWithdrawals = totalWithdrawals
                )
            }.launchIn(viewModelScope)
    }

    /**
     * Initiates a deposit.
     */
    fun deposit(amount: Double, transactionId: String) {
        viewModelScope.launch {
            val userId = getCurrentUserIdUseCase() ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = depositUseCase(userId, amount, transactionId)) {
                is DataResult.Success -> {
                    // Data will refresh automatically via the listener
                }
                is DataResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.exception.message)
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    /**
     * Initiates a withdrawal.
     */
    fun withdraw(amount: Double) {
        viewModelScope.launch {
            val userId = getCurrentUserIdUseCase() ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = withdrawUseCase(userId, amount)) {
                is DataResult.Success -> {
                    // Data will refresh automatically via the listener
                }
                is DataResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.exception.message)
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
