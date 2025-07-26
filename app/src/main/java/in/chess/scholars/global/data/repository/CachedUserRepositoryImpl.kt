package `in`.chess.scholars.global.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import `in`.chess.scholars.global.data.cache.LocalCacheManager
import `in`.chess.scholars.global.domain.model.KycStatus
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Enhanced UserRepository implementation with local caching for 120 FPS performance
 */
class CachedUserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val cacheManager: LocalCacheManager
) : UserRepository {

    override fun getUserData(uid: String): Flow<DataResult<UserData>> = channelFlow {
        // First, emit cached data immediately for instant UI update
        val cachedData = cacheManager.getCachedUserData(uid)
        if (cachedData != null) {
            send(DataResult.Success(cachedData))
        }

        // Then set up real-time listener for fresh data
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
                    // Update cache with fresh data
                    launch {
                        cacheManager.cacheUserData(userData)
                    }
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
            // Update Firestore
            firestore.collection("users").document(userData.uid).set(userData).await()

            // Update cache immediately for instant UI feedback
            cacheManager.cacheUserData(userData)

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
                // Update user document
                transaction.update(userRef, mapOf(
                    "panNumber" to pan,
                    "aadharNumber" to aadhar,
                    "kycStatus" to KycStatus.IN_PROGRESS.name
                ))

                // Create KYC request
                transaction.set(kycRequestRef, mapOf(
                    "userId" to uid,
                    "panNumber" to pan,
                    "aadharNumber" to aadhar,
                    "status" to "PENDING",
                    "submittedAt" to System.currentTimeMillis()
                ))
            }.await()

            // Update cached user data
            val cachedUser = cacheManager.getCachedUserData(uid)
            cachedUser?.let {
                cacheManager.cacheUserData(it.copy(kycStatus = KycStatus.IN_PROGRESS))
            }

            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}