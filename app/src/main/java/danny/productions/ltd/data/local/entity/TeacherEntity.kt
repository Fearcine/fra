package danny.productions.ltd.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teachers",
    indices = [Index(value = ["email"], unique = true)]
)
data class TeacherEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val deviceId: String,
    val createdAt: Long
)
