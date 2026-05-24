package danny.productions.ltd.domain.repository

import danny.productions.ltd.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun mark(attendance: Attendance): Result<Unit>
    suspend fun update(attendance: Attendance): Result<Unit>
    suspend fun getByStudentAndSession(studentId: String, sessionId: String): Attendance?
    fun getBySession(sessionId: String): Flow<List<Attendance>>
    fun getByStudent(studentId: String): Flow<List<Attendance>>
    fun countPresentBySession(sessionId: String): Flow<Int>
    fun countBySession(sessionId: String): Flow<Int>
    fun getByDateRange(startTime: Long, endTime: Long): Flow<List<Attendance>>
    fun getAllByTeacher(teacherId: String): Flow<List<Attendance>>
}
