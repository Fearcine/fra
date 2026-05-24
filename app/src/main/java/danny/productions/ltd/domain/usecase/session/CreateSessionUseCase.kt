package danny.productions.ltd.domain.usecase.session

import danny.productions.ltd.domain.model.Session
import danny.productions.ltd.domain.repository.SessionRepository
import java.util.UUID

class CreateSessionUseCase(private val sessionRepository: SessionRepository) {

    suspend operator fun invoke(
        lectureName: String,
        subject: String,
        teacherId: String,
        durationMillis: Long
    ): Result<Session> {
        if (lectureName.isBlank()) return Result.failure(IllegalArgumentException("Lecture name required"))
        if (subject.isBlank()) return Result.failure(IllegalArgumentException("Subject required"))
        if (durationMillis <= 0) return Result.failure(IllegalArgumentException("Duration must be positive"))

        val now = System.currentTimeMillis()
        val session = Session(
            id = UUID.randomUUID().toString(),
            lectureName = lectureName.trim(),
            subject = subject.trim(),
            teacherId = teacherId,
            startTime = now,
            expiryTime = now + durationMillis,
            qrNonce = UUID.randomUUID().toString(),
            isActive = true
        )
        return sessionRepository.create(session).map { session }
    }
}
