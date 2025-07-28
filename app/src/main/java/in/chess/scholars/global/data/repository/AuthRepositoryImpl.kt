package `in`.chess.scholars.global.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import `in`.chess.scholars.global.domain.model.UserData
import `in`.chess.scholars.global.domain.repository.AuthRepository
import `in`.chess.scholars.global.domain.repository.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase implementation of the AuthRepository.
 * Handles all authentication logic using Firebase Auth and Firestore.
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)

    override fun getAuthState(): StateFlow<Boolean> = _isLoggedIn

    override suspend fun signIn(email: String, password: String): DataResult<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            _isLoggedIn.value = true
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): DataResult<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                // Update Firebase Auth profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()

                // Create user document in Firestore
                val newUser = UserData(
                    uid = user.uid,
                    email = email,
                    displayName = displayName,
                    // FIX: Changed from System.currentTimeMillis() to Timestamp.now()
                    // This ensures new users are created with the correct, consistent data type.
                    createdAt = Timestamp.now()
                )
                firestore.collection("users").document(user.uid).set(newUser).await()
                _isLoggedIn.value = true
                DataResult.Success(Unit)
            } else {
                DataResult.Error(Exception("User creation failed."))
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
        _isLoggedIn.value = false
    }

    override suspend fun resetPassword(email: String): DataResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
