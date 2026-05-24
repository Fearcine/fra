package danny.productions.ltd.domain.usecase.session

import danny.productions.ltd.domain.model.Session
import danny.productions.ltd.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class GetActiveSessionsUseCase(private val sessionRepository: SessionRepository) {

    operator fun invoke(teacherId: String): Flow<List<Session>> {
        return sessionRepository.getActiveSessions(teacherId)
    }
}
