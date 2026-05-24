package danny.productions.ltd.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import danny.productions.ltd.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(student: StudentEntity)

    @Update
    suspend fun update(student: StudentEntity)

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): StudentEntity?

    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber LIMIT 1")
    suspend fun getByRollNumber(rollNumber: String): StudentEntity?

    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAll(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE fullName LIKE '%' || :query || '%' OR rollNumber LIKE '%' || :query || '%' OR department LIKE '%' || :query || '%' ORDER BY fullName ASC")
    fun search(query: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    fun countFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students")
    suspend fun count(): Int

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: String)
}
