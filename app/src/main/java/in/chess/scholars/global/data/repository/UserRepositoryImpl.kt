package `in`.chess.scholars.global.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import `in`.chess.scholars.global.domain.model.KycStatus
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.UserRepository
import `in`.chess.scholars.global.domain.model.UserData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Firebase implementation of the UserRepository.
 * Handles all data operations related to user profiles in Firestore.
 */
class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUserData(uid: String): Flow<DataResult<UserData>> = callbackFlow {
        val userDocument = firestore.collection("users").document(uid)
        val listener = userDocument.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResult.Error(error))
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                try {
                    // FIX: Manually deserialize to handle inconsistent 'createdAt' types.
                    // This will prevent the app from crashing whether the field is a Long or a Timestamp.
                    val data = snapshot.data
                    if (data != null) {
                        val createdAtRaw = data["createdAt"]
                        val createdAtTimestamp = when (createdAtRaw) {
                            is Timestamp -> createdAtRaw
                            is Long -> Timestamp(Date(createdAtRaw))
                            else -> null
                        }

                        val userData = UserData(
                            uid = snapshot.id,
                            email = data["email"] as? String ?: "",
                            displayName = data["displayName"] as? String ?: "",
                            phoneNumber = data["phoneNumber"] as? String ?: "",
                            rating = (data["rating"] as? Long)?.toInt() ?: 1200,
                            balance = data["balance"] as? Double ?: 0.0,
                            gamesPlayed = (data["gamesPlayed"] as? Long)?.toInt() ?: 0,
                            wins = (data["wins"] as? Long)?.toInt() ?: 0,
                            draws = (data["draws"] as? Long)?.toInt() ?: 0,
                            losses = (data["losses"] as? Long)?.toInt() ?: 0,
                            createdAt = createdAtTimestamp,
                            kycStatus = KycStatus.valueOf(data["kycStatus"] as? String ?: "NOT_STARTED"),
                            panNumber = data["panNumber"] as? String,
                            aadharNumber = data["aadharNumber"] as? String,
                            verified = data["verified"] as? Boolean ?: false
                        )
                        trySend(DataResult.Success(userData))
                    } else {
                        trySend(DataResult.Error(Exception("User data is null.")))
                    }
                } catch (e: Exception) {
                    trySend(DataResult.Error(Exception("Failed to parse user data: ${e.message}")))
                }
            } else {
                trySend(DataResult.Error(Exception("User document not found.")))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateUserData(userData: UserData): DataResult<Unit> {
        return try {
            firestore.collection("users").document(userData.uid).set(userData).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun submitKyc(uid: String, pan: String, aadhar: String): DataResult<Unit> {
        return try {
            val userRef = firestore.collection("users").document(uid)
            val kycRequestRef = firestore.collection("kyc_requests").document(uid)

            firestore.runTransaction { transaction ->
                // 1. Update the user's document
                transaction.update(userRef, mapOf(
                    "panNumber" to pan,
                    "aadharNumber" to aadhar,
                    "kycStatus" to KycStatus.IN_PROGRESS
                ))

                // 2. Create a separate request for admin review
                transaction.set(kycRequestRef, mapOf(
                    "userId" to uid,
                    "panNumber" to pan,
                    "aadharNumber" to aadhar,
                    "status" to "PENDING",
                    "submittedAt" to System.currentTimeMillis()
                ))
            }.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
