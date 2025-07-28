package `in`.chess.scholars.global.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import `in`.chess.scholars.global.data.repository.*
import `in`.chess.scholars.global.domain.repository.*
import `in`.chess.scholars.global.domain.usecases.*

/**
 * A simple manual dependency injection container, now as a class.
 * This allows us to manage its lifecycle within the Application class.
 */
class DIContainer {

    // Firebase Services
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Repositories
    private val authRepository: AuthRepository by lazy { AuthRepositoryImpl(firebaseAuth, firestore) }
    private val userRepository: UserRepository by lazy { UserRepositoryImpl(firestore) }
    private val matchmakingRepository: MatchmakingRepository by lazy { MatchmakingRepositoryImpl(firebaseAuth, firestore) }
    private val gameRepository: GameRepository by lazy { GameRepositoryImpl(firestore) }
    private val walletRepository: WalletRepository by lazy { WalletRepositoryImpl(firestore) }
    private val statisticsRepository: StatisticsRepository by lazy { StatisticsRepositoryImpl(firestore) }

    // Use Cases
    val getAuthStateUseCase: GetAuthStateUseCase by lazy { GetAuthStateUseCase(authRepository) }
    val signInUseCase: SignInUseCase by lazy { SignInUseCase(authRepository) }
    val signUpUseCase: SignUpUseCase by lazy { SignUpUseCase(authRepository) }
    val signOutUseCase: SignOutUseCase by lazy { SignOutUseCase(authRepository) }
    val resetPasswordUseCase: ResetPasswordUseCase by lazy { ResetPasswordUseCase(authRepository) }
    val getCurrentUserIdUseCase: GetCurrentUserIdUseCase by lazy { GetCurrentUserIdUseCase(authRepository) }
    val getUserDataUseCase: GetUserDataUseCase by lazy { GetUserDataUseCase(userRepository) }
    val updateUserDataUseCase: UpdateUserDataUseCase by lazy { UpdateUserDataUseCase(userRepository) }
    val submitKycUseCase: SubmitKycUseCase by lazy { SubmitKycUseCase(userRepository) }
    val findMatchUseCase: FindMatchUseCase by lazy { FindMatchUseCase(matchmakingRepository) }
    val cancelMatchmakingUseCase: CancelMatchmakingUseCase by lazy { CancelMatchmakingUseCase(matchmakingRepository) }
    val getGameStreamUseCase: GetGameStreamUseCase by lazy { GetGameStreamUseCase(gameRepository) }
    val updateGameUseCase: UpdateGameUseCase by lazy { UpdateGameUseCase(gameRepository) }
    val endGameUseCase: EndGameUseCase by lazy { EndGameUseCase(gameRepository) }
    val getTransactionsUseCase: GetTransactionsUseCase by lazy { GetTransactionsUseCase(walletRepository) }
    val depositUseCase: DepositUseCase by lazy { DepositUseCase(walletRepository) }
    val withdrawUseCase: WithdrawUseCase by lazy { WithdrawUseCase(walletRepository) }
    val getGameHistoryUseCase: GetGameHistoryUseCase by lazy { GetGameHistoryUseCase(statisticsRepository) }
    val getRatingHistoryUseCase: GetRatingHistoryUseCase by lazy { GetRatingHistoryUseCase(statisticsRepository) }
    val getChatStreamUseCase: GetChatStreamUseCase by lazy { GetChatStreamUseCase(gameRepository) }
    val sendMessageUseCase: SendMessageUseCase by lazy { SendMessageUseCase(gameRepository) }
}
