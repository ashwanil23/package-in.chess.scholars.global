package `in`.chess.scholars.global.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import `in`.chess.scholars.global.domain.model.GameHistory
import `in`.chess.scholars.global.domain.model.GameResultStats
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.StatisticsRepository
import `in`.chess.scholars.global.domain.model.RatingPoint
import kotlinx.coroutines.tasks.await

/**
 * Firebase implementation of the StatisticsRepository.
 * Handles fetching aggregated game and rating data from Firestore.
 */
class StatisticsRepositoryImpl(
    private val firestore: FirebaseFirestore
) : StatisticsRepository {

    /**
     * Fetches a user's game history by querying the 'games' collection twice:
     * once for games where the user was player1, and once for player2.
     * The results are then combined, sorted, and mapped to the GameHistory model.
     */
    override suspend fun getGameHistory(uid: String): DataResult<List<GameHistory>> {
        return try {
            val gamesAsPlayer1 = firestore.collection("games")
                .whereEqualTo("player1", uid)
                .whereIn("status", listOf("finished", "draw"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(25)
                .get().await()

            val gamesAsPlayer2 = firestore.collection("games")
                .whereEqualTo("player2", uid)
                .whereIn("status", listOf("finished", "draw"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(25)
                .get().await()

            val allGames = (gamesAsPlayer1.documents + gamesAsPlayer2.documents)
                .distinctBy { it.id }
                .sortedByDescending { (it.getLong("createdAt") ?: 0) }

            val gameHistories = allGames.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val isPlayer1 = data["player1"] as? String == uid
                val status = data["status"] as? String ?: ""
                val winner = data["winner"] as? String ?: ""

                val result = when {
                    status == "draw" -> GameResultStats.DRAW
                    (winner == "WHITE" && isPlayer1) || (winner == "BLACK" && !isPlayer1) -> GameResultStats.WIN
                    else -> GameResultStats.LOSS
                }

                val opponentId = if (isPlayer1) data["player2"] as? String else data["player1"] as? String
                // In a real app, you might fetch opponent details here or pass the ID
                val opponentName = "Opponent" // Placeholder
                val opponentRating = 1200 // Placeholder

                GameHistory(
                    gameId = doc.id,
                    opponent = opponentName,
                    opponentRating = opponentRating,
                    result = result,
                    ratingChange = (data["ratingChange"] as? Long)?.toInt() ?: 0,
                    betAmount = (data["betAmount"] as? Double)?.toFloat() ?: 0f,
                    duration = (data["endedAt"] as? Long ?: 0L) - (data["createdAt"] as? Long ?: 0L),
                    date = data["createdAt"] as? Long ?: 0L
                )
            }

            DataResult.Success(gameHistories)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    /**
     * Fetches the rating history from the user's 'ratingHistory' subcollection.
     */
    override suspend fun getRatingHistory(uid: String): DataResult<List<RatingPoint>> {
        return try {
            val historySnapshot = firestore.collection("users").document(uid)
                .collection("ratingHistory")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get().await()

            val ratingHistory = historySnapshot.toObjects(RatingPoint::class.java)
            DataResult.Success(ratingHistory)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
