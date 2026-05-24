package danny.productions.ltd.data.repository

import danny.productions.ltd.data.local.dao.AttendanceDao
import danny.productions.ltd.data.mapper.toDomain
import danny.productions.ltd.data.mapper.toEntity
import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AttendanceRepositoryImpl(private val attendanceDao: AttendanceDao) : AttendanceRepository {

    override suspend fun mark(attendance: Attendance): Result<Unit> = try {
        attendanceDao.insert(attendance.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun update(attendance: Attendance): Result<Unit> = try {
        attendanceDao.update(attendance.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getByStudentAndSession(studentId: String, sessionId: String): Attendance? =
        attendanceDao.getByStudentAndSession(studentId, sessionId)?.toDomain()

    override fun getBySession(sessionId: String): Flow<List<Attendance>> =
        attendanceDao.getBySession(sessionId).map { list -> list.map { it.toDomain() } }

    override fun getByStudent(studentId: String): Flow<List<Attendance>> =
        attendanceDao.getByStudent(studentId).map { list -> list.map { it.toDomain() } }

    override fun countPresentBySession(sessionId: String): Flow<Int> =
        attendanceDao.countPresentBySession(sessionId)

    override fun countBySession(sessionId: String): Flow<Int> =
        attendanceDao.countBySession(sessionId)

    override fun getByDateRange(startTime: Long, endTime: Long): Flow<List<Attendance>> =
        attendanceDao.getByDateRange(startTime, endTime).map { list -> list.map { it.toDomain() } }

    override fun getAllByTeacher(teacherId: String): Flow<List<Attendance>> =
        attendanceDao.getAllByTeacher(teacherId).map { list -> list.map { it.toDomain() } }
}
