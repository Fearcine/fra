package danny.productions.ltd.domain.usecase.student

import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.domain.repository.StudentRepository
import danny.productions.ltd.utils.PasswordHasher
import java.util.UUID

class AddStudentUseCase(private val studentRepository: StudentRepository) {

    suspend operator fun invoke(
        rollNumber: String,
        fullName: String,
        department: String,
        year: Int,
        password: String
    ): Result<Student> {
        if (rollNumber.isBlank()) return Result.failure(IllegalArgumentException("Roll number required"))
        if (fullName.isBlank()) return Result.failure(IllegalArgumentException("Name required"))
        if (department.isBlank()) return Result.failure(IllegalArgumentException("Department required"))
        if (year < 1 || year > 6) return Result.failure(IllegalArgumentException("Invalid year"))
        if (password.length < 4) return Result.failure(IllegalArgumentException("Password too short"))

        val existing = studentRepository.getByRollNumber(rollNumber.trim())
        if (existing != null) return Result.failure(IllegalStateException("Roll number already exists"))

        val student = Student(
            id = UUID.randomUUID().toString(),
            rollNumber = rollNumber.trim(),
            fullName = fullName.trim(),
            department = department.trim(),
            year = year,
            passwordHash = PasswordHasher.hash(password),
            registeredAt = System.currentTimeMillis()
        )
        return studentRepository.add(student).map { student }
    }
}
