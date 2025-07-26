package `in`.chess.scholars.global.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.GameHistory
import `in`.chess.scholars.global.domain.model.RatingPoint
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.usecases.GetCurrentUserIdUseCase
import `in`.chess.scholars.global.domain.usecases.GetGameHistoryUseCase
import `in`.chess.scholars.global.domain.usecases.GetRatingHistoryUseCase
import `in`.chess.scholars.global.domain.usecases.GetUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * UI state for the Statistics and Profile screens.
 */
data class StatisticsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userData: UserData? = null,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentRating: Int = 1200,
    val highestRating: Int = 1200,
    val lowestRating: Int = 1200,
    val winRate: Float = 0f,
    val gameHistory: List<GameHistory> = emptyList(),
    val ratingHistory: List<RatingPoint> = emptyList()
)

/**
 * ViewModel for the user's statistics and profile screens.
 */
class StatisticsViewModel(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getGameHistoryUseCase: GetGameHistoryUseCase,
    private val getRatingHistoryUseCase: GetRatingHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadAllStatistics()
    }

    fun refreshStatistics() {
        loadAllStatistics()
    }

    private fun loadAllStatistics() {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState(isLoading = true)

            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                _uiState.value = StatisticsUiState(isLoading = false, error = "User not found.")
                return@launch
            }

            // Fetch all data concurrently
            val userDataResult: DataResult<UserData> = getUserDataUseCase(userId).first() // Get the first result and stop collecting
            val gameHistoryResult = getGameHistoryUseCase(userId)
            val ratingHistoryResult = getRatingHistoryUseCase(userId)

            // Process results
            var finalState = _uiState.value.copy(isLoading = false) // Start with current state

            // Process User Data
            if (userDataResult is DataResult.Success) {
                val userData = userDataResult.data
                finalState = finalState.copy(
                    userData = userData,
                    totalGames = userData.gamesPlayed,
                    wins = userData.wins,
                    losses = userData.losses,
                    draws = userData.draws,
                    currentRating = userData.rating,
                    winRate = if (userData.gamesPlayed > 0) (userData.wins.toFloat() / userData.gamesPlayed) * 100 else 0f
                )
            }else if (userDataResult is DataResult.Error) {
                finalState = finalState.copy(error = userDataResult.exception.message)
            }


            // Process Game History
            if (gameHistoryResult is DataResult.Success) {
                finalState = finalState.copy(gameHistory = gameHistoryResult.data)
            } else if (gameHistoryResult is DataResult.Error) {
                finalState = finalState.copy(error = gameHistoryResult.exception.message)
            }

            // Process Rating History
            if (ratingHistoryResult is DataResult.Success) {
                val ratingHistory = ratingHistoryResult.data
                finalState = finalState.copy(
                    ratingHistory = ratingHistory,
                    highestRating = ratingHistory.maxOfOrNull { it.rating } ?: finalState.currentRating,
                    lowestRating = ratingHistory.minOfOrNull { it.rating } ?: finalState.currentRating
                )
            } else if (ratingHistoryResult is DataResult.Error) {
                finalState = finalState.copy(error = ratingHistoryResult.exception.message)
            }

            _uiState.value = finalState
        }
    }
}
