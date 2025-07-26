package `in`.chess.scholars.global.domain.usecases

import `in`.chess.scholars.global.domain.repository.MatchmakingRepository
import `in`.chess.scholars.global.domain.repository.MatchmakingState
import kotlinx.coroutines.flow.Flow

/**
 * Use case for finding a match.
 */
class FindMatchUseCase(private val matchmakingRepository: MatchmakingRepository) {
    operator fun invoke(userRating: Int, betAmount: Float): Flow<MatchmakingState> {
        return matchmakingRepository.findMatch(userRating, betAmount)
    }
}

/**
 * Use case for canceling matchmaking.
 */
class CancelMatchmakingUseCase(private val matchmakingRepository: MatchmakingRepository) {
    suspend operator fun invoke() {
        matchmakingRepository.cancelMatchmaking()
    }
}
