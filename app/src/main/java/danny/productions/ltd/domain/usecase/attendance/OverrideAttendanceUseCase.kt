package danny.productions.ltd.domain.usecase.attendance

import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.model.AttendanceStatus
import danny.productions.ltd.domain.model.VerificationMethod
import danny.productions.ltd.domain.repository.AttendanceRepository

class OverrideAttendanceUseCase(private val attendanceRepository: AttendanceRepository) {

    suspend operator fun invoke(
        studentId: String,
        sessionId: String,
        newStatus: AttendanceStatus
    ): Result<Unit> {
        val existing = attendanceRepository.getByStudentAndSession(studentId, sessionId)
            ?: return Result.failure(IllegalStateException("No attendance record found"))

        val updated = existing.copy(
            status = newStatus,
            verificationMethod = VerificationMethod.MANUAL_OVERRIDE,
            manuallyModified = true
        )
        return attendanceRepository.update(updated)
    }
}
