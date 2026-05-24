package danny.productions.ltd.domain.model

data class Teacher(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val deviceId: String,
    val createdAt: Long
)
