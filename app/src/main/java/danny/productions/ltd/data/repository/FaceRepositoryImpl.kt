package danny.productions.ltd.data.repository

import danny.productions.ltd.data.local.dao.FaceDataDao
import danny.productions.ltd.data.mapper.toDomain
import danny.productions.ltd.data.mapper.toEntity
import danny.productions.ltd.domain.model.FaceData
import danny.productions.ltd.domain.repository.FaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FaceRepositoryImpl(private val faceDataDao: FaceDataDao) : FaceRepository {

    override suspend fun register(faceData: FaceData): Result<Unit> = try {
        faceDataDao.insert(faceData.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getByStudentId(studentId: String): List<FaceData> =
        faceDataDao.getByStudentId(studentId).map { it.toDomain() }

    override suspend fun getAll(): List<FaceData> =
        faceDataDao.getAll().map { it.toDomain() }

    override fun getByStudentIdFlow(studentId: String): Flow<List<FaceData>> =
        faceDataDao.getByStudentIdFlow(studentId).map { list -> list.map { it.toDomain() } }

    override suspend fun countForStudent(studentId: String): Int =
        faceDataDao.countForStudent(studentId)

    override suspend fun deleteByStudentId(studentId: String): Result<Unit> = try {
        faceDataDao.deleteByStudentId(studentId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
