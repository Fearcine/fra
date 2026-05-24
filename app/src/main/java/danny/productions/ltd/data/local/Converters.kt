package danny.productions.ltd.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromFloatArray(value: FloatArray): ByteArray {
        val buffer = java.nio.ByteBuffer.allocate(value.size * 4)
        value.forEach { buffer.putFloat(it) }
        return buffer.array()
    }

    @TypeConverter
    fun toFloatArray(bytes: ByteArray): FloatArray {
        val buffer = java.nio.ByteBuffer.wrap(bytes)
        return FloatArray(bytes.size / 4) { buffer.getFloat() }
    }
}
