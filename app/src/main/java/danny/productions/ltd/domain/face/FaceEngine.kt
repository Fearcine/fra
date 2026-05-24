package danny.productions.ltd.domain.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect

class FaceEngine(private val context: Context) {

    private val detector = MLKitFaceDetector()
    private val interpreter = FaceNetInterpreter(context)

    fun init() {
        interpreter.init()
    }

    suspend fun extractFaceEmbedding(bitmap: Bitmap): FloatArray? {
        val faces = detector.detectFaces(bitmap)
        if (faces.isEmpty() || faces.size > 1) return null // Need exactly one face

        val bounds = faces[0].boundingBox
        val cropped = cropBitmap(bitmap, bounds) ?: return null
        return interpreter.getEmbedding(cropped)
    }

    private fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap? {
        return try {
            // Add padding to the face bounding box (e.g., 20%) to include the whole head/chin
            val paddingX = (rect.width() * 0.2f).toInt()
            val paddingY = (rect.height() * 0.2f).toInt()

            val left = (rect.left - paddingX).coerceAtLeast(0)
            val top = (rect.top - paddingY).coerceAtLeast(0)
            val right = (rect.right + paddingX).coerceAtMost(bitmap.width)
            val bottom = (rect.bottom + paddingY).coerceAtMost(bitmap.height)

            val width = right - left
            val height = bottom - top

            if (width <= 0 || height <= 0) return null
            Bitmap.createBitmap(bitmap, left, top, width, height)
        } catch (e: Exception) {
            null
        }
    }

    fun close() {
        detector.close()
        interpreter.close()
    }
}
