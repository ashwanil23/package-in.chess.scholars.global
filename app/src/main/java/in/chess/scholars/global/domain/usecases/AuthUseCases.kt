package `in`.chess.scholars.global.domain.usecases


import `in`.chess.scholars.global.domain.repository.AuthRepository
import `in`.chess.scholars.global.domain.repository.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Use case to get the real-time authentication state.
 */
class GetAuthStateUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<Boolean> = authRepository.getAuthState()
}

/**
 * Use case for signing in a user.
 */
class SignInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): DataResult<Unit> {
        return authRepository.signIn(email, password)
    }
}

/**
 * Use case for registering a new user.
 */
class SignUpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, displayName: String): DataResult<Unit> {
        return authRepository.signUp(email, password, displayName)
    }
}

/**
 * Use case for signing out the current user.
 */
class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}

/**
 * Use case for sending a password reset email.
 */
class ResetPasswordUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): DataResult<Unit> {
        return authRepository.resetPassword(email)
    }
}

/**
 * Use case for getting the current user's ID.
 */
class GetCurrentUserIdUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): String? {
        return authRepository.getCurrentUserId()
    }
}

