package danny.productions.ltd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = TeacherEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["teacherId"]),
        Index(value = ["qrNonce"], unique = true)
    ]
)
data class SessionEntity(
    @PrimaryKey val id: String,
    val lectureName: String,
    val subject: String,
    val teacherId: String,
    val startTime: Long,
    val expiryTime: Long,
    val qrNonce: String,
    val isActive: Boolean
)
