package danny.productions.ltd.domain.usecase.attendance

import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.model.AttendanceStatus
import danny.productions.ltd.domain.model.VerificationMethod
import danny.productions.ltd.domain.repository.AttendanceRepository
import java.util.UUID

class MarkAttendanceUseCase(private val attendanceRepository: AttendanceRepository) {

    suspend operator fun invoke(
        studentId: String,
        sessionId: String,
        verificationMethod: VerificationMethod,
        confidenceScore: Float
    ): Result<Attendance> {
        val existing = attendanceRepository.getByStudentAndSession(studentId, sessionId)
        if (existing != null) return Result.failure(IllegalStateException("Attendance already marked"))

        val attendance = Attendance(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            sessionId = sessionId,
            timestamp = System.currentTimeMillis(),
            status = AttendanceStatus.PRESENT,
            verificationMethod = verificationMethod,
            confidenceScore = confidenceScore,
            manuallyModified = false
        )
        return attendanceRepository.mark(attendance).map { attendance }
    }
}
