package danny.productions.ltd.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

object CameraXBitmapUtils {
    fun toBitmap(imageProxy: ImageProxy): Bitmap {
        val bitmap = imageProxy.toBitmap()
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
        
        if (rotationDegrees == 0f) {
            return bitmap
        }

        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        val rotated = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        // If createBitmap returned a new object, the old one can be recycled.
        // Wait, 'imageProxy.toBitmap()' returns a newly allocated Bitmap that we own.
        // We shouldn't recycle it if createBitmap returns the same instance.
        if (rotated != bitmap) {
            bitmap.recycle()
        }
        return rotated
    }
}
