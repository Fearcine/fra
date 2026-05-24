package danny.productions.ltd.domain.repository

import danny.productions.ltd.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun create(session: Session): Result<Unit>
    suspend fun getById(id: String): Session?
    suspend fun getByNonce(nonce: String): Session?
    fun getByTeacherId(teacherId: String): Flow<List<Session>>
    fun getActiveSessions(teacherId: String): Flow<List<Session>>
    suspend fun deactivate(id: String): Result<Unit>
    suspend fun deactivateExpired(): Result<Unit>
    fun countByTeacher(teacherId: String): Flow<Int>
}
