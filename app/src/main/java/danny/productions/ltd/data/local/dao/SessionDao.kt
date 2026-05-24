package danny.productions.ltd.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import danny.productions.ltd.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE teacherId = :teacherId ORDER BY startTime DESC")
    fun getByTeacherId(teacherId: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE isActive = 1 AND teacherId = :teacherId ORDER BY startTime DESC")
    fun getActiveSessions(teacherId: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE qrNonce = :nonce LIMIT 1")
    suspend fun getByNonce(nonce: String): SessionEntity?

    @Query("UPDATE sessions SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: String)

    @Query("UPDATE sessions SET isActive = 0 WHERE expiryTime < :currentTime AND isActive = 1")
    suspend fun deactivateExpired(currentTime: Long)

    @Query("SELECT COUNT(*) FROM sessions WHERE teacherId = :teacherId")
    fun countByTeacher(teacherId: String): Flow<Int>
}
