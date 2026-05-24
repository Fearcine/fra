package danny.productions.ltd.domain.face

import kotlin.math.sqrt

class FaceMatcher {

    companion object {
        const val MATCH_THRESHOLD = 0.55f // Cosine similarity threshold
    }

    fun computeCosineSimilarity(emb1: FloatArray, emb2: FloatArray): Float {
        if (emb1.size != emb2.size) return 0f
        
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f
        
        for (i in emb1.indices) {
            dotProduct += emb1[i] * emb2[i]
            norm1 += emb1[i] * emb1[i]
            norm2 += emb2[i] * emb2[i]
        }
        
        if (norm1 == 0f || norm2 == 0f) return 0f
        return dotProduct / (sqrt(norm1) * sqrt(norm2))
    }

    fun isMatch(emb1: FloatArray, emb2: FloatArray): Boolean {
        return computeCosineSimilarity(emb1, emb2) >= MATCH_THRESHOLD
    }
}
