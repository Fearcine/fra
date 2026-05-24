package danny.productions.ltd.domain.usecase.attendance

import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetAttendanceUseCase(private val attendanceRepository: AttendanceRepository) {

    fun bySession(sessionId: String): Flow<List<Attendance>> =
        attendanceRepository.getBySession(sessionId)

    fun byStudent(studentId: String): Flow<List<Attendance>> =
        attendanceRepository.getByStudent(studentId)

    fun byDateRange(startTime: Long, endTime: Long): Flow<List<Attendance>> =
        attendanceRepository.getByDateRange(startTime, endTime)

    fun byTeacher(teacherId: String): Flow<List<Attendance>> =
        attendanceRepository.getAllByTeacher(teacherId)

    fun presentCount(sessionId: String): Flow<Int> =
        attendanceRepository.countPresentBySession(sessionId)

    fun totalCount(sessionId: String): Flow<Int> =
        attendanceRepository.countBySession(sessionId)
}
