package danny.productions.ltd.domain.usecase.auth

import danny.productions.ltd.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
