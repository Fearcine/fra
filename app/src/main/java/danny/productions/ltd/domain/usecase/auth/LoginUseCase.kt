package danny.productions.ltd.domain.usecase.auth

import danny.productions.ltd.domain.model.Teacher
import danny.productions.ltd.domain.model.UserRole
import danny.productions.ltd.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend fun teacherLogin(email: String, password: String): Result<Teacher> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email required"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password required"))
        return authRepository.loginTeacher(email.trim(), password)
    }

    suspend fun studentLogin(rollNumber: String, password: String): Result<String> {
        if (rollNumber.isBlank()) return Result.failure(IllegalArgumentException("Roll number required"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password required"))
        return authRepository.loginStudent(rollNumber.trim(), password)
    }
}
