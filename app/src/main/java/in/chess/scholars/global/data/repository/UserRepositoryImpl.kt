package `in`.chess.scholars.global.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import `in`.chess.scholars.global.domain.model.KycStatus
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.UserRepository
import `in`.chess.scholars.global.domain.model.UserData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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
                val userData = snapshot.toObject(UserData::class.java)
                if (userData != null) {
                    trySend(DataResult.Success(userData))
                } else {
                    trySend(DataResult.Error(Exception("Failed to parse user data.")))
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
