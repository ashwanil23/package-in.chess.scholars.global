package `in`.chess.scholars.global.domain.usecases

import `in`.chess.scholars.global.domain.model.GameHistory
import `in`.chess.scholars.global.domain.model.RatingPoint
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.StatisticsRepository


/**
 * Use case for getting the game history of a user.
 */
class GetGameHistoryUseCase(private val statisticsRepository: StatisticsRepository) {
    suspend operator fun invoke(uid: String): DataResult<List<GameHistory>> {
        return statisticsRepository.getGameHistory(uid)
    }
}

/**
 * Use case for getting the rating history of a user.
 */
class GetRatingHistoryUseCase(private val statisticsRepository: StatisticsRepository) {
    suspend operator fun invoke(uid: String): DataResult<List<RatingPoint>> {
        return statisticsRepository.getRatingHistory(uid)
    }
}
