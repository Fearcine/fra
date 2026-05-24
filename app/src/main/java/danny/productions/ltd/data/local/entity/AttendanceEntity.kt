package danny.productions.ltd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId", "sessionId"], unique = true),
        Index(value = ["sessionId"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val sessionId: String,
    val timestamp: Long,
    val status: String,
    val verificationMethod: String,
    val confidenceScore: Float,
    val manuallyModified: Boolean
)
