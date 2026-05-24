package danny.productions.ltd.domain.model

data class AttendanceRequest(
    val type: String = "ATTENDANCE_MARK",
    val studentId: String,
    val sessionId: String,
    val nonce: String,
    val embedding: FloatArray,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttendanceRequest) return false
        return studentId == other.studentId && sessionId == other.sessionId
    }

    override fun hashCode(): Int = 31 * studentId.hashCode() + sessionId.hashCode()
}

data class AttendanceResponse(
    val type: String,
    val message: String,
    val status: String? = null,
    val confidence: Float? = null
) {
    companion object {
        const val ACK = "ATTENDANCE_ACK"
        const val NACK = "ATTENDANCE_NACK"
    }
}
