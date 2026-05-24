package danny.productions.ltd.data.mapper

import danny.productions.ltd.data.local.entity.AttendanceEntity
import danny.productions.ltd.data.local.entity.FaceDataEntity
import danny.productions.ltd.data.local.entity.SessionEntity
import danny.productions.ltd.data.local.entity.StudentEntity
import danny.productions.ltd.data.local.entity.TeacherEntity
import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.model.AttendanceStatus
import danny.productions.ltd.domain.model.FaceAngle
import danny.productions.ltd.domain.model.FaceData
import danny.productions.ltd.domain.model.Session
import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.domain.model.Teacher
import danny.productions.ltd.domain.model.VerificationMethod

fun TeacherEntity.toDomain() = Teacher(id, name, email, passwordHash, deviceId, createdAt)
fun Teacher.toEntity() = TeacherEntity(id, name, email, passwordHash, deviceId, createdAt)

fun StudentEntity.toDomain() = Student(id, rollNumber, fullName, department, year, passwordHash, registeredAt)
fun Student.toEntity() = StudentEntity(id, rollNumber, fullName, department, year, passwordHash, registeredAt)

fun FaceDataEntity.toDomain() = FaceData(id, studentId, embedding, FaceAngle.valueOf(angle), createdAt)
fun FaceData.toEntity() = FaceDataEntity(id, studentId, embedding, angle.name, createdAt)

fun SessionEntity.toDomain() = Session(id, lectureName, subject, teacherId, startTime, expiryTime, qrNonce, isActive)
fun Session.toEntity() = SessionEntity(id, lectureName, subject, teacherId, startTime, expiryTime, qrNonce, isActive)

fun AttendanceEntity.toDomain() = Attendance(
    id, studentId, sessionId, timestamp,
    AttendanceStatus.valueOf(status),
    VerificationMethod.valueOf(verificationMethod),
    confidenceScore, manuallyModified
)

fun Attendance.toEntity() = AttendanceEntity(
    id, studentId, sessionId, timestamp,
    status.name, verificationMethod.name,
    confidenceScore, manuallyModified
)
