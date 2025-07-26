package `in`.chess.scholars.global.presentation.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.usecases.GetCurrentUserIdUseCase
import `in`.chess.scholars.global.domain.usecases.GetUserDataUseCase
import `in`.chess.scholars.global.domain.usecases.SubmitKycUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * UI state for the KYC screen.
 */
data class KycUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userData: UserData? = null,
    val submissionSuccess: Boolean = false
)

/**
 * ViewModel for the KYC screen.
 */
class KycViewModel(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val submitKycUseCase: SubmitKycUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(KycUiState())
    val uiState: StateFlow<KycUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val userId = getCurrentUserIdUseCase()
        if (userId == null) {
            _uiState.value = KycUiState(isLoading = false, error = "User not logged in.")
            return
        }

        getUserDataUseCase(userId)
            .onEach { result ->
                when (result) {
                    is DataResult.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, userData = result.data)
                    }
                    is DataResult.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun submitKyc(panNumber: String, aadharNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "User not found.")
                return@launch
            }

            when (val result = submitKycUseCase(userId, panNumber, aadharNumber)) {
                is DataResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, submissionSuccess = true)
                }
                is DataResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exception.message)
                }
            }
        }
    }
}
