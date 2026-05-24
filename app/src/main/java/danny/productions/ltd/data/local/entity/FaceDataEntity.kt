package danny.productions.ltd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "face_data",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]
)
data class FaceDataEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val embedding: FloatArray,
    val angle: String,
    val createdAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FaceDataEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
