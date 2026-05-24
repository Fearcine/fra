package danny.productions.ltd.domain.usecase.face

import danny.productions.ltd.domain.model.FaceAngle
import danny.productions.ltd.domain.model.FaceData
import danny.productions.ltd.domain.repository.FaceRepository
import java.util.UUID

class RegisterFaceUseCase(private val faceRepository: FaceRepository) {

    suspend operator fun invoke(
        studentId: String,
        embedding: FloatArray,
        angle: FaceAngle
    ): Result<FaceData> {
        if (embedding.isEmpty()) return Result.failure(IllegalArgumentException("Empty embedding"))

        val faceData = FaceData(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            embedding = embedding,
            angle = angle,
            createdAt = System.currentTimeMillis()
        )
        return faceRepository.register(faceData).map { faceData }
    }
}
