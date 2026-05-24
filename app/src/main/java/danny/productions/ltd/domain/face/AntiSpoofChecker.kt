package danny.productions.ltd.domain.face

import android.graphics.Bitmap

interface AntiSpoofChecker {
    /**
     * Checks if the face in the bitmap is a real, live person (not a photo/screen).
     * MVP: Always returns true. Real implementation requires deep learning liveness model.
     */
    suspend fun isRealFace(bitmap: Bitmap): Boolean
}

class DefaultAntiSpoofChecker : AntiSpoofChecker {
    override suspend fun isRealFace(bitmap: Bitmap): Boolean {
        // TODO: Implement actual liveness detection (e.g., MiniFASNet)
        return true
    }
}
