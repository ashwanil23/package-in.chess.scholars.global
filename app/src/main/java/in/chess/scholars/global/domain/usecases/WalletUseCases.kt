package `in`.chess.scholars.global.domain.usecases

import `in`.chess.scholars.global.domain.model.Transaction
import `in`.chess.scholars.global.domain.repository.DataResult
import `in`.chess.scholars.global.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the transaction history of a user.
 */
class GetTransactionsUseCase(private val walletRepository: WalletRepository) {
    operator fun invoke(uid: String): Flow<DataResult<List<Transaction>>> {
        return walletRepository.getTransactions(uid)
    }
}

/**
 * Use case for depositing money into a user's wallet.
 */
class DepositUseCase(private val walletRepository: WalletRepository) {
    suspend operator fun invoke(uid: String, amount: Double, transactionId: String): DataResult<Unit> {
        return walletRepository.deposit(uid, amount, transactionId)
    }
}

/**
 * Use case for withdrawing money from a user's wallet.
 */
class WithdrawUseCase(private val walletRepository: WalletRepository) {
    suspend operator fun invoke(uid: String, amount: Double): DataResult<Unit> {
        return walletRepository.withdraw(uid, amount)
    }
}
