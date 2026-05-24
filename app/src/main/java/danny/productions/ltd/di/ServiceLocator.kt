package danny.productions.ltd.di

import android.app.Application
import danny.productions.ltd.data.local.FRADatabase
import danny.productions.ltd.data.preferences.PreferencesManager
import danny.productions.ltd.data.repository.AttendanceRepositoryImpl
import danny.productions.ltd.data.repository.AuthRepositoryImpl
import danny.productions.ltd.data.repository.FaceRepositoryImpl
import danny.productions.ltd.data.repository.SessionRepositoryImpl
import danny.productions.ltd.data.repository.StudentRepositoryImpl
import danny.productions.ltd.domain.repository.AttendanceRepository
import danny.productions.ltd.domain.repository.AuthRepository
import danny.productions.ltd.domain.repository.FaceRepository
import danny.productions.ltd.domain.repository.SessionRepository
import danny.productions.ltd.domain.repository.StudentRepository
import danny.productions.ltd.domain.usecase.attendance.ExportAttendanceUseCase
import danny.productions.ltd.domain.usecase.attendance.GetAttendanceUseCase
import danny.productions.ltd.domain.usecase.attendance.MarkAttendanceUseCase
import danny.productions.ltd.domain.usecase.attendance.OverrideAttendanceUseCase
import danny.productions.ltd.domain.usecase.auth.LoginUseCase
import danny.productions.ltd.domain.usecase.auth.LogoutUseCase
import danny.productions.ltd.domain.usecase.face.RegisterFaceUseCase
import danny.productions.ltd.domain.usecase.face.VerifyFaceUseCase
import danny.productions.ltd.domain.usecase.qr.GenerateQRUseCase
import danny.productions.ltd.domain.usecase.qr.ValidateQRUseCase
import danny.productions.ltd.domain.usecase.session.CreateSessionUseCase
import danny.productions.ltd.domain.usecase.session.EndSessionUseCase
import danny.productions.ltd.domain.usecase.session.GetActiveSessionsUseCase
import danny.productions.ltd.domain.usecase.student.AddStudentUseCase
import danny.productions.ltd.domain.usecase.student.RemoveStudentUseCase
import danny.productions.ltd.domain.usecase.student.SearchStudentsUseCase

object ServiceLocator {

    private lateinit var application: Application

    fun init(app: Application) {
        application = app
    }

    fun getApplication(): Application = application

    // Database
    val database: FRADatabase by lazy { FRADatabase.create(application) }

    // Preferences
    val preferencesManager: PreferencesManager by lazy { PreferencesManager(application) }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database.teacherDao(), database.studentDao(), preferencesManager)
    }
    val studentRepository: StudentRepository by lazy {
        StudentRepositoryImpl(database.studentDao())
    }
    val sessionRepository: SessionRepository by lazy {
        SessionRepositoryImpl(database.sessionDao())
    }
    val attendanceRepository: AttendanceRepository by lazy {
        AttendanceRepositoryImpl(database.attendanceDao())
    }
    val faceRepository: FaceRepository by lazy {
        FaceRepositoryImpl(database.faceDataDao())
    }

    // Use Cases — Auth
    val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepository) }
    val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }

    // Use Cases — Session
    val createSessionUseCase: CreateSessionUseCase by lazy { CreateSessionUseCase(sessionRepository) }
    val getActiveSessionsUseCase: GetActiveSessionsUseCase by lazy { GetActiveSessionsUseCase(sessionRepository) }
    val endSessionUseCase: EndSessionUseCase by lazy { EndSessionUseCase(sessionRepository) }

    // Use Cases — Attendance
    val markAttendanceUseCase: MarkAttendanceUseCase by lazy { MarkAttendanceUseCase(attendanceRepository) }
    val getAttendanceUseCase: GetAttendanceUseCase by lazy { GetAttendanceUseCase(attendanceRepository) }
    val overrideAttendanceUseCase: OverrideAttendanceUseCase by lazy { OverrideAttendanceUseCase(attendanceRepository) }
    val exportAttendanceUseCase: ExportAttendanceUseCase by lazy { ExportAttendanceUseCase(attendanceRepository) }

    // Use Cases — Student
    val addStudentUseCase: AddStudentUseCase by lazy { AddStudentUseCase(studentRepository) }
    val removeStudentUseCase: RemoveStudentUseCase by lazy { RemoveStudentUseCase(studentRepository, faceRepository) }
    val searchStudentsUseCase: SearchStudentsUseCase by lazy { SearchStudentsUseCase(studentRepository) }

    // Use Cases — Face
    val registerFaceUseCase: RegisterFaceUseCase by lazy { RegisterFaceUseCase(faceRepository) }
    val verifyFaceUseCase: VerifyFaceUseCase by lazy { VerifyFaceUseCase(faceRepository) }

    // Use Cases — QR
    val generateQRUseCase: GenerateQRUseCase by lazy { GenerateQRUseCase() }
    val validateQRUseCase: ValidateQRUseCase by lazy { ValidateQRUseCase() }
}
