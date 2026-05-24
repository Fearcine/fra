package danny.productions.ltd.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import danny.productions.ltd.data.local.entity.FaceDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(faceData: FaceDataEntity)

    @Query("SELECT * FROM face_data WHERE studentId = :studentId")
    suspend fun getByStudentId(studentId: String): List<FaceDataEntity>

    @Query("SELECT * FROM face_data")
    suspend fun getAll(): List<FaceDataEntity>

    @Query("SELECT * FROM face_data WHERE studentId = :studentId")
    fun getByStudentIdFlow(studentId: String): Flow<List<FaceDataEntity>>

    @Query("SELECT COUNT(*) FROM face_data WHERE studentId = :studentId")
    suspend fun countForStudent(studentId: String): Int

    @Query("DELETE FROM face_data WHERE studentId = :studentId")
    suspend fun deleteByStudentId(studentId: String)

    @Query("DELETE FROM face_data WHERE id = :id")
    suspend fun deleteById(id: String)
}
