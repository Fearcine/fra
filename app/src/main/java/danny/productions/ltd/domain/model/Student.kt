package danny.productions.ltd.domain.model

data class Student(
    val id: String,
    val rollNumber: String,
    val fullName: String,
    val department: String,
    val year: Int,
    val passwordHash: String,
    val registeredAt: Long
)
