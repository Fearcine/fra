package danny.productions.ltd.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import danny.productions.ltd.data.local.dao.AttendanceDao
import danny.productions.ltd.data.local.dao.FaceDataDao
import danny.productions.ltd.data.local.dao.SessionDao
import danny.productions.ltd.data.local.dao.StudentDao
import danny.productions.ltd.data.local.dao.TeacherDao
import danny.productions.ltd.data.local.entity.AttendanceEntity
import danny.productions.ltd.data.local.entity.FaceDataEntity
import danny.productions.ltd.data.local.entity.SessionEntity
import danny.productions.ltd.data.local.entity.StudentEntity
import danny.productions.ltd.data.local.entity.TeacherEntity

@Database(
    entities = [
        TeacherEntity::class,
        StudentEntity::class,
        FaceDataEntity::class,
        SessionEntity::class,
        AttendanceEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FRADatabase : RoomDatabase() {

    abstract fun teacherDao(): TeacherDao
    abstract fun studentDao(): StudentDao
    abstract fun faceDataDao(): FaceDataDao
    abstract fun sessionDao(): SessionDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        fun create(context: Context): FRADatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FRADatabase::class.java,
                "fra_database"
            ).build()
        }
    }
}
