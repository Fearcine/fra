package danny.productions.ltd.domain.usecase.qr

import danny.productions.ltd.domain.model.QRPayload

class ValidateQRUseCase {

    operator fun invoke(
        rawQrContent: String,
        secretKey: String
    ): ValidationResult {
        val parts = rawQrContent.split("|")
        if (parts.size != 7) return ValidationResult.InvalidFormat

        val sessionId = parts[0]
        val teacherDeviceId = parts[1]
        val hostIp = parts[2]
        val port = parts[3].toIntOrNull() ?: return ValidationResult.InvalidFormat
        val issuedAt = parts[4].toLongOrNull() ?: return ValidationResult.InvalidFormat
        val nonce = parts[5]
        val signature = parts[6]

        val jsonStr = "$sessionId|$teacherDeviceId|$hostIp|$port|$issuedAt|$nonce"
        
        if (!danny.productions.ltd.utils.QRSigner.verify(jsonStr, signature, secretKey)) {
            return ValidationResult.InvalidSignature
        }

        // Check if QR is too old (e.g., > 15 seconds)
        val now = System.currentTimeMillis()
        if (now - issuedAt > 15000) {
            return ValidationResult.Expired
        }

        val payload = QRPayload(sessionId, teacherDeviceId, hostIp, port, issuedAt, nonce)
        return ValidationResult.Valid(payload)
    }

    sealed class ValidationResult {
        data class Valid(val payload: QRPayload) : ValidationResult()
        data object Expired : ValidationResult()
        data object InvalidSignature : ValidationResult()
        data object InvalidFormat : ValidationResult()
    }
}
