package `in`.chess.scholars.global.domain.usecases

import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting a user's data in real-time.
 */
class GetUserDataUseCase(private val userRepository: UserRepository) {
    operator fun invoke(uid: String): Flow<DataResult<UserData>> {
        return userRepository.getUserData(uid)
    }
}

/**
 * Use case for updating a user's data.
 */
class UpdateUserDataUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userData: UserData): DataResult<Unit> {
        return userRepository.updateUserData(userData)
    }
}

