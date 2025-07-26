package `in`.chess.scholars.global.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.WalletRepository
import `in`.chess.scholars.global.domain.model.Transaction
import `in`.chess.scholars.global.domain.model.TransactionStatus
import `in`.chess.scholars.global.domain.model.TransactionType
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase implementation of the WalletRepository.
 * Handles all data operations related to a user's wallet and transactions in Firestore.
 */
class WalletRepositoryImpl(
    private val firestore: FirebaseFirestore
) : WalletRepository {

    /**
     * Retrieves a real-time stream of a user's transaction history from Firestore.
     */
    override fun getTransactions(uid: String): Flow<DataResult<List<Transaction>>> = callbackFlow {
        val transactionsCollection = firestore.collection("users").document(uid)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50) // Get the last 50 transactions

        val listener = transactionsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResult.Error(error))
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val transactions = snapshot.toObjects(Transaction::class.java)
                trySend(DataResult.Success(transactions))
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Processes a deposit by updating the user's balance and adding a transaction record.
     * This uses a Firestore Transaction to ensure atomicity.
     */
    override suspend fun deposit(uid: String, amount: Double, transactionId: String): DataResult<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val userRef = firestore.collection("users").document(uid)
                val userDoc = transaction.get(userRef)

                val currentBalance = userDoc.getDouble("balance") ?: 0.0
                val newBalance = currentBalance + amount

                // 1. Update user's balance
                transaction.update(userRef, "balance", newBalance)

                // 2. Create a new transaction document
                val transactionRef = userRef.collection("transactions").document()
                val depositTransaction = Transaction(
                    id = transactionRef.id,
                    userId = uid,
                    type = TransactionType.DEPOSIT,
                    amount = amount,
                    description = "Deposit via Payment Gateway",
                    status = TransactionStatus.COMPLETED,
                    referenceId = transactionId,
                    balanceAfter = newBalance,
                    isCredit = true
                )
                transaction.set(transactionRef, depositTransaction)

            }.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    /**
     * Processes a withdrawal request. This creates a pending withdrawal document for admin approval
     * and a corresponding pending transaction in the user's history.
     */
    override suspend fun withdraw(uid: String, amount: Double): DataResult<Unit> {
        return try {
            // Create a main withdrawal request document for admin processing
            val withdrawalRequestRef = firestore.collection("withdrawals").document()
            val withdrawalData = mapOf(
                "userId" to uid,
                "amount" to amount,
                "status" to "PENDING",
                "requestedAt" to System.currentTimeMillis()
            )
            withdrawalRequestRef.set(withdrawalData).await()


            // Create a pending transaction record in the user's subcollection
            val userRef = firestore.collection("users").document(uid)
            val transactionRef = userRef.collection("transactions").document()
            val withdrawalTransaction = Transaction(
                id = transactionRef.id,
                userId = uid,
                type = TransactionType.WITHDRAWAL,
                amount = amount,
                description = "Withdrawal Request",
                status = TransactionStatus.PENDING,
                referenceId = withdrawalRequestRef.id,
                balanceAfter = 0.0, // Balance will be updated upon approval
                isCredit = false
            )
            transactionRef.set(withdrawalTransaction).await()

            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
