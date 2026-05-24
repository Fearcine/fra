package danny.productions.ltd.domain.model

data class Attendance(
    val id: String,
    val studentId: String,
    val sessionId: String,
    val timestamp: Long,
    val status: AttendanceStatus,
    val verificationMethod: VerificationMethod,
    val confidenceScore: Float,
    val manuallyModified: Boolean
)
