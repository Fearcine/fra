package danny.productions.ltd.domain.repository

import danny.productions.ltd.domain.model.Teacher

interface AuthRepository {
    suspend fun loginTeacher(email: String, password: String): Result<Teacher>
    suspend fun loginStudent(rollNumber: String, password: String): Result<String>
    suspend fun registerTeacher(name: String, email: String, password: String): Result<Teacher>
    suspend fun getLoggedInTeacherId(): String?
    suspend fun getLoggedInStudentId(): String?
    suspend fun setLoggedInTeacher(teacherId: String?)
    suspend fun setLoggedInStudent(studentId: String?)
    suspend fun logout()
}
