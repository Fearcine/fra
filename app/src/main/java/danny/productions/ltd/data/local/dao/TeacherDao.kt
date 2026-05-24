package danny.productions.ltd.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import danny.productions.ltd.data.local.entity.TeacherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(teacher: TeacherEntity)

    @Query("SELECT * FROM teachers WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TeacherEntity?

    @Query("SELECT * FROM teachers")
    fun getAll(): Flow<List<TeacherEntity>>

    @Query("SELECT COUNT(*) FROM teachers")
    suspend fun count(): Int

    @Query("DELETE FROM teachers WHERE id = :id")
    suspend fun deleteById(id: String)
}
