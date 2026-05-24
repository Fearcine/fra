package danny.productions.ltd.domain.usecase.face

import danny.productions.ltd.domain.repository.FaceRepository
import kotlin.math.sqrt

class VerifyFaceUseCase(private val faceRepository: FaceRepository) {

    companion object {
        const val DEFAULT_THRESHOLD = 0.6f
    }

    suspend operator fun invoke(
        studentId: String,
        embedding: FloatArray,
        threshold: Float = DEFAULT_THRESHOLD
    ): Result<VerifyResult> {
        val storedFaces = faceRepository.getByStudentId(studentId)
        if (storedFaces.isEmpty()) return Result.failure(IllegalStateException("No face data registered"))

        var bestScore = 0f
        for (face in storedFaces) {
            val score = cosineSimilarity(embedding, face.embedding)
            if (score > bestScore) bestScore = score
        }

        return if (bestScore >= threshold) {
            Result.success(VerifyResult(matched = true, confidence = bestScore))
        } else {
            Result.success(VerifyResult(matched = false, confidence = bestScore))
        }
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator == 0f) 0f else dotProduct / denominator
    }

    data class VerifyResult(
        val matched: Boolean,
        val confidence: Float
    )
}
