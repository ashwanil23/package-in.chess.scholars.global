package `in`.chess.scholars.global.domain.repository

import android.app.GameState
import `in`.chess.scholars.global.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * A generic wrapper class to handle success and error states for data operations.
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
}

/**
 * Repository for handling user authentication.
 * Defines the contract for all auth-related operations.
 */
interface AuthRepository {
    /** Emits the current authentication state (logged in or out). */
    fun getAuthState(): Flow<Boolean>

    /** Signs in a user with email and password. */
    suspend fun signIn(email: String, password: String): DataResult<Unit>

    /** Registers a new user. */
    suspend fun signUp(email: String, password: String, displayName: String): DataResult<Unit>

    /** Signs out the current user. */
    suspend fun signOut()

    /** Sends a password reset email. */
    suspend fun resetPassword(email: String): DataResult<Unit>

    /** Gets the current authenticated user's ID. */
    fun getCurrentUserId(): String?
}

/**
 * Repository for managing user data.
 * Handles fetching and updating user profiles and stats from the data source.
 */
interface UserRepository {
    /** Gets a real-time stream of a user's data. */
    fun getUserData(uid: String): Flow<DataResult<UserData>>

    /** Updates a user's profile information. */
    suspend fun updateUserData(userData: UserData): DataResult<Unit>

    /** Submits user's KYC information. */
    suspend fun submitKyc(uid: String, pan: String, aadhar: String): DataResult<Unit>
}

/**
 * Repository for managing all chess game-related data.
 */
interface GameRepository {
    /** Gets a real-time stream of a game's state. */
    fun getGameStream(gameId: String): Flow<DataResult<`in`.chess.scholars.global.domain.model.GameState>> // GameState would be a new model representing the full game state

    /** Creates a new game document in the data source. */
    suspend fun createGame(player1Id: String, player2Id: String, betAmount: Float): DataResult<String>

    /** Updates a game with a new move or state change. */
    suspend fun updateGame(gameId: String, move: Move): DataResult<Unit>

    /** Finalizes a game, updating its status to complete. */
    suspend fun endGame(gameId: String, result: GameResult): DataResult<Unit>
}

/**
 * Repository for handling the matchmaking process.
 */
interface MatchmakingRepository {
    /**
     * Starts the matchmaking process and emits the current state.
     * @param userRating The rating of the user searching for a match.
     * @param betAmount The bet amount for the game.
     * @return A Flow that emits MatchmakingState updates.
     */
    fun findMatch(userRating: Int, betAmount: Float): Flow<MatchmakingState>

    /** Cancels the ongoing matchmaking search. */
    suspend fun cancelMatchmaking()
}

/**
 * Sealed class representing the state of the matchmaking process.
 */
sealed class MatchmakingState {
    object Idle : MatchmakingState()
    data class Searching(val ratingRange: String) : MatchmakingState()
    data class MatchFound(val gameId: String, val opponentId: String) : MatchmakingState()
    data class Error(val message: String) : MatchmakingState()
}


/**
 * Repository for managing the user's wallet and transactions.
 */
interface WalletRepository {
    /** Gets a real-time stream of the user's transaction history. */
    fun getTransactions(uid: String): Flow<DataResult<List<Transaction>>>

    /**
     * Initiates a deposit into the user's wallet.
     * In a real app, this would involve a payment gateway.
     */
    suspend fun deposit(uid: String, amount: Double, transactionId: String): DataResult<Unit>

    /**
     * Initiates a withdrawal from the user's wallet.
     * This would create a withdrawal request for processing.
     */
    suspend fun withdraw(uid: String, amount: Double): DataResult<Unit>
}

/**
 * Repository for fetching and processing user statistics.
 */
interface StatisticsRepository {
    /** Fetches the complete game history for a user. */
    suspend fun getGameHistory(uid: String): DataResult<List<GameHistory>>

    /** Fetches the rating history for a user (for graphs). */
    suspend fun getRatingHistory(uid: String): DataResult<List<RatingPoint>>
}