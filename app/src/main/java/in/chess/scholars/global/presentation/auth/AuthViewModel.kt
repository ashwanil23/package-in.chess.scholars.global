package `in`.chess.scholars.global.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.usecases.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the authentication screens.
 */
data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val isPasswordResetSent: Boolean = false
)

/**
 * ViewModel responsible for handling all authentication logic and state.
 * It now also provides the logged-in user's data.
 */
class AuthViewModel(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserDataUseCase: GetUserDataUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    init {
        // Observe the authentication state from the repository.
        getAuthStateUseCase()
            .onEach { isLoggedIn ->
                _authState.value = _authState.value.copy(isLoggedIn = isLoggedIn)
                if (isLoggedIn) {
                    // If the user is logged in, fetch their data.
                    fetchUserData()
                } else {
                    // If logged out, clear user data.
                    _userData.value = null
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchUserData() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            viewModelScope.launch {
                getUserDataUseCase(userId).collect { result ->
                    if (result is DataResult.Success) {
                        _userData.value = result.data
                    }
                    // Optionally handle error case for user data fetching
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            when (val result = signInUseCase(email, password)) {
                is DataResult.Success -> {
                    _authState.value = _authState.value.copy(isLoading = false)
                }
                is DataResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "An unknown error occurred."
                    )
                }
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            when (val result = signUpUseCase(email, password, displayName)) {
                is DataResult.Success -> {
                    _authState.value = _authState.value.copy(isLoading = false)
                }
                is DataResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "An unknown error occurred."
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null, isPasswordResetSent = false)
            when (val result = resetPasswordUseCase(email)) {
                is DataResult.Success -> {
                    _authState.value = _authState.value.copy(isLoading = false, isPasswordResetSent = true)
                }
                is DataResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to send reset email."
                    )
                }
            }
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}
