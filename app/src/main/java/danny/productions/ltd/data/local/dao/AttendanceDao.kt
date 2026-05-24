package danny.productions.ltd.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import danny.productions.ltd.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(attendance: AttendanceEntity)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getBySession(sessionId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getByStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND sessionId = :sessionId LIMIT 1")
    suspend fun getByStudentAndSession(studentId: String, sessionId: String): AttendanceEntity?

    @Query("SELECT COUNT(*) FROM attendance WHERE sessionId = :sessionId AND status = 'PRESENT'")
    fun countPresentBySession(sessionId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE sessionId = :sessionId")
    fun countBySession(sessionId: String): Flow<Int>

    @Query("SELECT * FROM attendance WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getByDateRange(startTime: Long, endTime: Long): Flow<List<AttendanceEntity>>

    @Query("""
        SELECT a.* FROM attendance a 
        INNER JOIN sessions s ON a.sessionId = s.id 
        WHERE s.teacherId = :teacherId 
        ORDER BY a.timestamp DESC
    """)
    fun getAllByTeacher(teacherId: String): Flow<List<AttendanceEntity>>
}
