package `in`.chess.scholars.global.presentation.matchmaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.MatchmakingState
import `in`.chess.scholars.global.domain.usecases.CancelMatchmakingUseCase
import `in`.chess.scholars.global.domain.usecases.FindMatchUseCase
import `in`.chess.scholars.global.domain.usecases.GetUserDataUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Matchmaking/Lobby screen.
 */
data class MatchmakingUiState(
    val status: String = "Initializing...",
    val opponent: UserData? = null,
    val gameId: String? = null,
    val error: String? = null,
    val isSearching: Boolean = false
)

/**
 * ViewModel for the matchmaking process.
 */
class MatchmakingViewModel(
    private val findMatchUseCase: FindMatchUseCase,
    private val cancelMatchmakingUseCase: CancelMatchmakingUseCase,
    private val getUserDataUseCase: GetUserDataUseCase // To fetch opponent details
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchmakingUiState())
    val uiState: StateFlow<MatchmakingUiState> = _uiState.asStateFlow()

    private var matchmakingJob: Job? = null

    /**
     * Starts the matchmaking process.
     */
    fun startMatchmaking(userRating: Int, betAmount: Float) {
        // Cancel any existing job to avoid multiple searches
        matchmakingJob?.cancel()

        matchmakingJob = findMatchUseCase(userRating, betAmount)
            .onEach { state ->
                when (state) {
                    is MatchmakingState.Idle -> {
                        _uiState.value = MatchmakingUiState(status = "Idle", isSearching = false)
                    }
                    is MatchmakingState.Searching -> {
                        _uiState.value = MatchmakingUiState(
                            status = "Searching for opponent in range: ${state.ratingRange}",
                            isSearching = true
                        )
                    }
                    is MatchmakingState.MatchFound -> {
                        // When a match is found, we get the opponent's ID.
                        // We then use the GetUserDataUseCase to fetch their full profile.
                        fetchOpponentData(state.opponentId, state.gameId)
                    }
                    is MatchmakingState.Error -> {
                        _uiState.value = MatchmakingUiState(
                            error = state.message,
                            isSearching = false
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Fetches the opponent's data once a match is made.
     */
    private fun fetchOpponentData(opponentId: String, gameId: String) {
        viewModelScope.launch {
            getUserDataUseCase(opponentId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _uiState.value = MatchmakingUiState(
                            status = "Match Found!",
                            opponent = result.data,
                            gameId = gameId,
                            isSearching = false
                        )
                        // Stop listening after finding the opponent
                        matchmakingJob?.cancel()
                    }
                    is DataResult.Error -> {
                        _uiState.value = MatchmakingUiState(
                            error = "Could not retrieve opponent data.",
                            isSearching = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Cancels the matchmaking process.
     */
    fun cancelMatchmaking() {
        viewModelScope.launch {
            cancelMatchmakingUseCase()
            matchmakingJob?.cancel()
            _uiState.value = MatchmakingUiState(status = "Cancelled", isSearching = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Ensure matchmaking is cancelled if the ViewModel is destroyed
        cancelMatchmaking()
    }
}
