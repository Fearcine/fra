package danny.productions.ltd.domain.repository

import danny.productions.ltd.domain.model.FaceData
import danny.productions.ltd.domain.model.FaceAngle
import kotlinx.coroutines.flow.Flow

interface FaceRepository {
    suspend fun register(faceData: FaceData): Result<Unit>
    suspend fun getByStudentId(studentId: String): List<FaceData>
    suspend fun getAll(): List<FaceData>
    fun getByStudentIdFlow(studentId: String): Flow<List<FaceData>>
    suspend fun countForStudent(studentId: String): Int
    suspend fun deleteByStudentId(studentId: String): Result<Unit>
}
