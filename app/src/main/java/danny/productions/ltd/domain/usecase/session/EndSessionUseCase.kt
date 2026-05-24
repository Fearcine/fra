package danny.productions.ltd.domain.usecase.session

import danny.productions.ltd.domain.repository.SessionRepository

class EndSessionUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return sessionRepository.deactivate(sessionId)
    }
}
