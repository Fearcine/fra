package danny.productions.ltd.domain.usecase.qr

import danny.productions.ltd.domain.model.QRPayload
import danny.productions.ltd.domain.model.Session

class GenerateQRUseCase {

    operator fun invoke(
        session: Session,
        teacherDeviceId: String,
        hostIp: String,
        port: Int
    ): QRPayload {
        val now = System.currentTimeMillis()
        return QRPayload(
            sessionId = session.id,
            teacherDeviceId = teacherDeviceId,
            hostIp = hostIp,
            port = port,
            issuedAt = now,
            nonce = session.qrNonce
        )
    }
}
