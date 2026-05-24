package danny.productions.ltd.data.repository

import danny.productions.ltd.data.local.dao.TeacherDao
import danny.productions.ltd.data.mapper.toDomain
import danny.productions.ltd.data.mapper.toEntity
import danny.productions.ltd.data.preferences.PreferencesManager
import danny.productions.ltd.domain.model.Teacher
import danny.productions.ltd.domain.repository.AuthRepository
import danny.productions.ltd.utils.PasswordHasher
import java.util.UUID

class AuthRepositoryImpl(
    private val teacherDao: TeacherDao,
    private val studentDao: danny.productions.ltd.data.local.dao.StudentDao,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun loginTeacher(email: String, password: String): Result<Teacher> {
        val entity = teacherDao.getByEmail(email)
            ?: return Result.failure(IllegalArgumentException("Teacher not found"))
        if (!PasswordHasher.verify(password, entity.passwordHash)) {
            return Result.failure(IllegalArgumentException("Invalid password"))
        }
        setLoggedInTeacher(entity.id)
        return Result.success(entity.toDomain())
    }

    override suspend fun loginStudent(rollNumber: String, password: String): Result<String> {
        val entity = studentDao.getByRollNumber(rollNumber)
            ?: return Result.failure(IllegalArgumentException("Student not found"))
        if (!PasswordHasher.verify(password, entity.passwordHash)) {
            return Result.failure(IllegalArgumentException("Invalid password"))
        }
        setLoggedInStudent(entity.id)
        return Result.success(entity.id)
    }

    override suspend fun registerTeacher(name: String, email: String, password: String): Result<Teacher> {
        val existing = teacherDao.getByEmail(email)
        if (existing != null) return Result.failure(IllegalStateException("Email already registered"))

        val teacher = Teacher(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            passwordHash = PasswordHasher.hash(password),
            deviceId = preferencesManager.getDeviceId(),
            createdAt = System.currentTimeMillis()
        )
        return try {
            teacherDao.insert(teacher.toEntity())
            setLoggedInTeacher(teacher.id)
            Result.success(teacher)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLoggedInTeacherId(): String? {
        val id = preferencesManager.getLoggedInTeacherId() ?: return null
        // Validate against DB in case Auto Backup restored preferences but DB was wiped
        return if (teacherDao.getById(id) != null) id else {
            setLoggedInTeacher(null)
            null
        }
    }
    
    override suspend fun getLoggedInStudentId(): String? {
        val id = preferencesManager.getLoggedInStudentId() ?: return null
        return if (studentDao.getById(id) != null) id else {
            setLoggedInStudent(null)
            null
        }
    }
    override suspend fun setLoggedInTeacher(teacherId: String?) = preferencesManager.setLoggedInTeacherId(teacherId)
    override suspend fun setLoggedInStudent(studentId: String?) = preferencesManager.setLoggedInStudentId(studentId)

    override suspend fun logout() {
        preferencesManager.setLoggedInTeacherId(null)
        preferencesManager.setLoggedInStudentId(null)
    }
}
