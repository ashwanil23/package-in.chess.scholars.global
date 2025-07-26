package `in`.chess.scholars.global.domain.usecases

import `in`.chess.scholars.global.domain.model.GameState
import `in`.chess.scholars.global.domain.repository.GameRepository
import `in`.chess.scholars.global.domain.model.GameResult
import `in`.chess.scholars.global.domain.model.Move
import `in`.chess.scholars.global.domain.repository.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Use case to get a real-time stream of the game state.
 */
class GetGameStreamUseCase(private val gameRepository: GameRepository) {
    operator fun invoke(gameId: String): Flow<DataResult<GameState>> {
        return gameRepository.getGameStream(gameId)
    }
}

/**
 * Use case to update the game state with a new move.
 */
class UpdateGameUseCase(private val gameRepository: GameRepository) {
    suspend operator fun invoke(gameId: String, move: Move): DataResult<Unit> {
        return gameRepository.updateGame(gameId, move)
    }
}

/**
 * Use case to end a game and record the result.
 */
class EndGameUseCase(private val gameRepository: GameRepository) {
    suspend operator fun invoke(gameId: String, result: GameResult): DataResult<Unit> {
        return gameRepository.endGame(gameId, result)
    }
}
