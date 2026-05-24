package danny.productions.ltd.domain.model

data class Session(
    val id: String,
    val lectureName: String,
    val subject: String,
    val teacherId: String,
    val startTime: Long,
    val expiryTime: Long,
    val qrNonce: String,
    val isActive: Boolean
)
