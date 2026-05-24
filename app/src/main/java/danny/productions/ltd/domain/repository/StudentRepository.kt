package danny.productions.ltd.domain.repository

import danny.productions.ltd.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    suspend fun add(student: Student): Result<Unit>
    suspend fun update(student: Student): Result<Unit>
    suspend fun getById(id: String): Student?
    suspend fun getByRollNumber(rollNumber: String): Student?
    fun getAll(): Flow<List<Student>>
    fun search(query: String): Flow<List<Student>>
    fun countFlow(): Flow<Int>
    suspend fun count(): Int
    suspend fun delete(id: String): Result<Unit>
}
