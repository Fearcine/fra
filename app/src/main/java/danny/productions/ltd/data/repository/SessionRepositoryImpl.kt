package danny.productions.ltd.data.repository

import danny.productions.ltd.data.local.dao.SessionDao
import danny.productions.ltd.data.mapper.toDomain
import danny.productions.ltd.data.mapper.toEntity
import danny.productions.ltd.domain.model.Session
import danny.productions.ltd.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(private val sessionDao: SessionDao) : SessionRepository {

    override suspend fun create(session: Session): Result<Unit> = try {
        sessionDao.insert(session.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getById(id: String): Session? = sessionDao.getById(id)?.toDomain()
    override suspend fun getByNonce(nonce: String): Session? = sessionDao.getByNonce(nonce)?.toDomain()
    override fun getByTeacherId(teacherId: String): Flow<List<Session>> = sessionDao.getByTeacherId(teacherId).map { list -> list.map { it.toDomain() } }
    override fun getActiveSessions(teacherId: String): Flow<List<Session>> = sessionDao.getActiveSessions(teacherId).map { list -> list.map { it.toDomain() } }

    override suspend fun deactivate(id: String): Result<Unit> = try {
        sessionDao.deactivate(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deactivateExpired(): Result<Unit> = try {
        sessionDao.deactivateExpired(System.currentTimeMillis())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun countByTeacher(teacherId: String): Flow<Int> = sessionDao.countByTeacher(teacherId)
}
