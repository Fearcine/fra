package danny.productions.ltd.domain.usecase.attendance

import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ExportAttendanceUseCase(private val attendanceRepository: AttendanceRepository) {

    suspend fun getSessionAttendance(sessionId: String): List<Attendance> {
        return attendanceRepository.getBySession(sessionId).first()
    }

    suspend fun getTeacherAttendance(teacherId: String): List<Attendance> {
        return attendanceRepository.getAllByTeacher(teacherId).first()
    }

    suspend fun getDateRangeAttendance(startTime: Long, endTime: Long): List<Attendance> {
        return attendanceRepository.getByDateRange(startTime, endTime).first()
    }
}
