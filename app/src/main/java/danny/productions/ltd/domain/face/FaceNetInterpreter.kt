package danny.productions.ltd.domain.face

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class FaceNetInterpreter(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val inputSize = 112 // MobileFaceNet standard
    private val outputSize = 192 // MobileFaceNet standard

    fun init() {
        try {
            val model = loadModelFile("mobile_face_net.tflite")
            val options = Interpreter.Options().apply {
                numThreads = 4
            }
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            android.util.Log.e("FRA", "Failed to load model", e)
        }
    }

    suspend fun getEmbedding(bitmap: Bitmap): FloatArray? {
        if (interpreter == null) return null

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
        val inputBuffer = convertBitmapToByteBuffer(scaledBitmap)
        val outputBuffer = Array(1) { FloatArray(outputSize) }

        return try {
            interpreter?.run(inputBuffer, outputBuffer)
            outputBuffer[0]
        } catch (e: Exception) {
            null
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                // Normalize to [-1, 1]
                byteBuffer.putFloat(((value shr 16 and 0xFF) - 127.5f) / 128.0f)
                byteBuffer.putFloat(((value shr 8 and 0xFF) - 127.5f) / 128.0f)
                byteBuffer.putFloat(((value and 0xFF) - 127.5f) / 128.0f)
            }
        }
        return byteBuffer
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelName)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
