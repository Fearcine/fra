package danny.productions.ltd.domain.model

data class QRPayload(
    val sessionId: String,
    val teacherDeviceId: String,
    val hostIp: String,
    val port: Int,
    val issuedAt: Long,
    val nonce: String
) {
    fun toJson(): String {
        return "$sessionId|$teacherDeviceId|$hostIp|$port|$issuedAt|$nonce"
    }
}
