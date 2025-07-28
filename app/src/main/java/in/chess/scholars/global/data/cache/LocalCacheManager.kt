package `in`.chess.scholars.global.data.cache

import android.content.Context
import androidx.room.*
import com.google.firebase.Timestamp
import `in`.chess.scholars.global.domain.model.GameHistory
import `in`.chess.scholars.global.domain.model.GameResultStats
import `in`.chess.scholars.global.domain.model.GameState
import `in`.chess.scholars.global.domain.model.KycStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import `in`.chess.scholars.global.domain.model.Transaction
import `in`.chess.scholars.global.domain.model.TransactionStatus
import `in`.chess.scholars.global.domain.model.TransactionType
import `in`.chess.scholars.global.domain.model.UserData
import java.util.Date

/**
 * Room Database for local caching
 */
@Database(
    entities = [
        CachedUserData::class,
        CachedGameHistory::class,
        CachedTransaction::class,
        CachedGameState::class,
        CacheMetadata::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ChessDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao

    companion object {
        @Volatile
        private var INSTANCE: ChessDatabase? = null

        fun getDatabase(context: Context): ChessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChessDatabase::class.java,
                    "chess_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Data Access Object for cache operations
 */
@Dao
interface CacheDao {
    // User data operations
    @Query("SELECT * FROM cached_user_data WHERE uid = :uid")
    suspend fun getUserData(uid: String): CachedUserData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(userData: CachedUserData)

    @Query("DELETE FROM cached_user_data WHERE uid = :uid")
    suspend fun deleteUserData(uid: String)

    // Game history operations
    @Query("SELECT * FROM cached_game_history WHERE userId = :userId ORDER BY date DESC LIMIT 5")
    suspend fun getRecentGameHistory(userId: String): List<CachedGameHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistory(games: List<CachedGameHistory>)

    @Query("DELETE FROM cached_game_history WHERE userId = :userId")
    suspend fun deleteGameHistory(userId: String)

    // Transaction operations
    @Query("SELECT * FROM cached_transactions WHERE userId = :userId ORDER BY timestamp DESC LIMIT 20")
    suspend fun getRecentTransactions(userId: String): List<CachedTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<CachedTransaction>)

    // Game state operations
    @Query("SELECT * FROM cached_game_state WHERE gameId = :gameId")
    suspend fun getGameState(gameId: String): CachedGameState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameState: CachedGameState)

    @Query("DELETE FROM cached_game_state WHERE gameId = :gameId")
    suspend fun deleteGameState(gameId: String)

    // Cache metadata operations
    @Query("SELECT * FROM cache_metadata WHERE key = :key")
    suspend fun getCacheMetadata(key: String): CacheMetadata?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheMetadata(metadata: CacheMetadata)

    @Query("DELETE FROM cache_metadata WHERE timestamp < :expiryTime")
    suspend fun deleteExpiredMetadata(expiryTime: Long)
}

/**
 * Entity classes for Room database
 */
@Entity(tableName = "cached_user_data")
data class CachedUserData(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val phoneNumber: String,
    val rating: Int,
    val balance: Double,
    val gamesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val createdAt: Long,
    val kycStatus: String,
    val verified: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_game_history")
data class CachedGameHistory(
    @PrimaryKey val gameId: String,
    val userId: String,
    val opponent: String,
    val opponentRating: Int,
    val result: String,
    val ratingChange: Int,
    val betAmount: Float,
    val duration: Long,
    val date: Long,
    val openingPlayed: String
)

@Entity(tableName = "cached_transactions")
data class CachedTransaction(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val amount: Double,
    val description: String,
    val timestamp: Long,
    val status: String,
    val referenceId: String,
    val balanceAfter: Double,
    val isCredit: Boolean
)

@Entity(tableName = "cached_game_state")
data class CachedGameState(
    @PrimaryKey val gameId: String,
    val boardState: String, // JSON encoded board state
    val currentPlayer: String,
    val moves: String, // JSON encoded moves list
    val status: String,
    val lastMoveTime: Long,
    val player1Id: String,
    val player2Id: String,
    val betAmount: Float
)

@Entity(tableName = "cache_metadata")
data class CacheMetadata(
    @PrimaryKey val key: String,
    val timestamp: Long,
    val expiryDuration: Long
)

/**
 * Type converters for complex data types
 */
class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromKycStatus(status: KycStatus): String = status.name

    @TypeConverter
    fun toKycStatus(status: String): KycStatus = KycStatus.valueOf(status)
}

/**
 * Cache Manager for handling all caching operations
 */
class LocalCacheManager(
    private val context: Context,
    private val database: ChessDatabase = ChessDatabase.getDatabase(context)
) {
    private val cacheDao = database.cacheDao()
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val USER_DATA_CACHE_DURATION = 5L // 5 minutes
        private const val GAME_HISTORY_CACHE_DURATION = 10L // 10 minutes
        private const val TRANSACTION_CACHE_DURATION = 5L // 5 minutes
        private const val GAME_STATE_CACHE_DURATION = 1L // 1 minute for real-time game
    }

    /**
     * User data caching
     */
    suspend fun cacheUserData(userData: UserData) {
        val cachedData = CachedUserData(
            uid = userData.uid,
            email = userData.email,
            displayName = userData.displayName,
            phoneNumber = userData.phoneNumber,
            rating = userData.rating,
            balance = userData.balance,
            gamesPlayed = userData.gamesPlayed,
            wins = userData.wins,
            draws = userData.draws,
            losses = userData.losses,
            // FIX: Convert Timestamp? to Long for Room DB storage.
            // This resolves the "Argument type mismatch" error.
            createdAt = userData.createdAt?.toDate()?.time ?: 0L,
            kycStatus = userData.kycStatus.name,
            verified = userData.verified
        )

        cacheDao.insertUserData(cachedData)
        updateCacheMetadata("user_${userData.uid}", USER_DATA_CACHE_DURATION)
    }

    suspend fun getCachedUserData(uid: String): UserData? {
        if (!isCacheValid("user_$uid")) {
            cacheDao.deleteUserData(uid)
            return null
        }

        return cacheDao.getUserData(uid)?.let { cached ->
            UserData(
                uid = cached.uid,
                email = cached.email,
                displayName = cached.displayName,
                phoneNumber = cached.phoneNumber,
                rating = cached.rating,
                balance = cached.balance,
                gamesPlayed = cached.gamesPlayed,
                wins = cached.wins,
                draws = cached.draws,
                losses = cached.losses,
                // FIX: Convert Long from Room DB back to a Timestamp object.
                createdAt = Timestamp(Date(cached.createdAt)),
                kycStatus = KycStatus.valueOf(cached.kycStatus),
                panNumber = null,
                aadharNumber = null,
                verified = cached.verified
            )
        }
    }

    /**
     * Game history caching
     */
    suspend fun cacheGameHistory(userId: String, games: List<GameHistory>) {
        val cachedGames = games.map { game ->
            CachedGameHistory(
                gameId = game.gameId,
                userId = userId,
                opponent = game.opponent,
                opponentRating = game.opponentRating,
                result = game.result.name,
                ratingChange = game.ratingChange,
                betAmount = game.betAmount,
                duration = game.duration,
                date = game.date,
                openingPlayed = game.openingPlayed
            )
        }

        cacheDao.deleteGameHistory(userId)
        cacheDao.insertGameHistory(cachedGames.take(5)) // Cache only last 5 games
        updateCacheMetadata("games_$userId", GAME_HISTORY_CACHE_DURATION)
    }

    suspend fun getCachedGameHistory(userId: String): List<GameHistory>? {
        if (!isCacheValid("games_$userId")) {
            cacheDao.deleteGameHistory(userId)
            return null
        }

        return cacheDao.getRecentGameHistory(userId).map { cached ->
            GameHistory(
                gameId = cached.gameId,
                opponent = cached.opponent,
                opponentRating = cached.opponentRating,
                result = GameResultStats.valueOf(cached.result),
                ratingChange = cached.ratingChange,
                betAmount = cached.betAmount,
                duration = cached.duration,
                date = cached.date,
                openingPlayed = cached.openingPlayed
            )
        }
    }

    /**
     * Transaction caching
     */
    suspend fun cacheTransactions(userId: String, transactions: List<Transaction>) {
        val cachedTransactions = transactions.map { transaction ->
            CachedTransaction(
                id = transaction.id,
                userId = userId,
                type = transaction.type.name,
                amount = transaction.amount,
                description = transaction.description,
                timestamp = transaction.timestamp,
                status = transaction.status.name,
                referenceId = transaction.referenceId,
                balanceAfter = transaction.balanceAfter,
                isCredit = transaction.isCredit
            )
        }

        cacheDao.insertTransactions(cachedTransactions.take(20))
        updateCacheMetadata("transactions_$userId", TRANSACTION_CACHE_DURATION)
    }

    suspend fun getCachedTransactions(userId: String): List<Transaction>? {
        if (!isCacheValid("transactions_$userId")) {
            return null
        }

        return cacheDao.getRecentTransactions(userId).map { cached ->
            Transaction(
                id = cached.id,
                userId = cached.userId,
                type = TransactionType.valueOf(cached.type),
                amount = cached.amount,
                description = cached.description,
                timestamp = cached.timestamp,
                status = TransactionStatus.valueOf(cached.status),
                referenceId = cached.referenceId,
                balanceAfter = cached.balanceAfter,
                isCredit = cached.isCredit
            )
        }
    }

    /**
     * Game state caching for real-time games
     */
    suspend fun cacheGameState(gameState: GameState) {
        val cachedState = CachedGameState(
            gameId = gameState.gameId,
            boardState = json.encodeToString(gameState.moves), // Simplified for now
            currentPlayer = gameState.status,
            moves = json.encodeToString(gameState.moves),
            status = gameState.status,
            lastMoveTime = gameState.endedAt?.toDate()?.time ?: System.currentTimeMillis(),
            player1Id = gameState.player1Id,
            player2Id = gameState.player2Id,
            betAmount = 0f // You'll need to add this to GameState
        )

        cacheDao.insertGameState(cachedState)
        updateCacheMetadata("game_${gameState.gameId}", GAME_STATE_CACHE_DURATION)
    }

    suspend fun getCachedGameState(gameId: String): GameState? {
        if (!isCacheValid("game_$gameId")) {
            cacheDao.deleteGameState(gameId)
            return null
        }

        return cacheDao.getGameState(gameId)?.let { cached ->
            GameState(
                gameId = cached.gameId,
                player1Id = cached.player1Id,
                player2Id = cached.player2Id,
                moves = try {
                    json.decodeFromString(cached.moves)
                } catch (e: Exception) {
                    emptyList()
                },
                status = cached.status,
                winner = null,
                drawReason = null,
                createdAt = null,
                endedAt = Timestamp(Date(cached.lastMoveTime))
            )
        }
    }

    /**
     * Cache validation and cleanup
     */
    private suspend fun isCacheValid(key: String): Boolean {
        val metadata = cacheDao.getCacheMetadata(key) ?: return false
        val expiryTime = metadata.timestamp + TimeUnit.MINUTES.toMillis(metadata.expiryDuration)
        return System.currentTimeMillis() < expiryTime
    }

    private suspend fun updateCacheMetadata(key: String, durationMinutes: Long) {
        cacheDao.insertCacheMetadata(
            CacheMetadata(
                key = key,
                timestamp = System.currentTimeMillis(),
                expiryDuration = durationMinutes
            )
        )
    }

    suspend fun clearExpiredCache() {
        val currentTime = System.currentTimeMillis()
        cacheDao.deleteExpiredMetadata(currentTime)
    }

    suspend fun clearAllCache() {
        database.clearAllTables()
    }

    /**
     * Performance monitoring
     */
    fun getCacheStats(): Flow<CacheStats> = flow {
        // Implement cache statistics gathering
        emit(CacheStats(
            totalSize = 0L,
            hitRate = 0f,
            missRate = 0f,
            averageLoadTime = 0L
        ))
    }
}

data class CacheStats(
    val totalSize: Long,
    val hitRate: Float,
    val missRate: Float,
    val averageLoadTime: Long
)