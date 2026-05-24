package danny.productions.ltd.data.repository

import danny.productions.ltd.data.local.dao.StudentDao
import danny.productions.ltd.data.mapper.toDomain
import danny.productions.ltd.data.mapper.toEntity
import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudentRepositoryImpl(private val studentDao: StudentDao) : StudentRepository {

    override suspend fun add(student: Student): Result<Unit> = try {
        studentDao.insert(student.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun update(student: Student): Result<Unit> = try {
        studentDao.update(student.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getById(id: String): Student? = studentDao.getById(id)?.toDomain()
    override suspend fun getByRollNumber(rollNumber: String): Student? = studentDao.getByRollNumber(rollNumber)?.toDomain()
    override fun getAll(): Flow<List<Student>> = studentDao.getAll().map { list -> list.map { it.toDomain() } }
    override fun search(query: String): Flow<List<Student>> = studentDao.search(query).map { list -> list.map { it.toDomain() } }
    override fun countFlow(): Flow<Int> = studentDao.countFlow()
    override suspend fun count(): Int = studentDao.count()

    override suspend fun delete(id: String): Result<Unit> = try {
        studentDao.deleteById(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
