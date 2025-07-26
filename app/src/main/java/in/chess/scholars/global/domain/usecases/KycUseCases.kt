package `in`.chess.scholars.global.domain.usecases

import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.UserRepository

/**
 * Use case for submitting KYC documents.
 */
class SubmitKycUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String, pan: String, aadhar: String): DataResult<Unit> {
        return userRepository.submitKyc(uid, pan, aadhar)
    }
}
