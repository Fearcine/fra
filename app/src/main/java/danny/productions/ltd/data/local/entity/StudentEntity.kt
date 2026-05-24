package danny.productions.ltd.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    indices = [Index(value = ["rollNumber"], unique = true)]
)
data class StudentEntity(
    @PrimaryKey val id: String,
    val rollNumber: String,
    val fullName: String,
    val department: String,
    val year: Int,
    val passwordHash: String,
    val registeredAt: Long
)
