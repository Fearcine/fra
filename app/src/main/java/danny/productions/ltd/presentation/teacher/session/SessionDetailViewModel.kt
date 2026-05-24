package danny.productions.ltd.presentation.teacher.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.model.AttendanceStatus
import danny.productions.ltd.domain.model.Session
import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.domain.model.VerificationMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StudentAttendanceItem(
    val student: Student,
    val attendance: Attendance?
)

data class SessionDetailState(
    val session: Session? = null,
    val items: List<StudentAttendanceItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class SessionDetailViewModel : ViewModel() {
    private val sessionRepo = ServiceLocator.sessionRepository
    private val attendanceRepo = ServiceLocator.attendanceRepository
    private val studentRepo = ServiceLocator.studentRepository

    private val _state = MutableStateFlow(SessionDetailState())
    val state: StateFlow<SessionDetailState> = _state.asStateFlow()

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val session = sessionRepo.getById(sessionId)
            val attendanceFlow = attendanceRepo.getBySession(sessionId)
            val studentsFlow = studentRepo.getAll()
            
            _state.update { it.copy(session = session) }
            
            launch {
                combine(studentsFlow, attendanceFlow) { students, records ->
                    students.map { student ->
                        val record = records.find { it.studentId == student.id }
                        StudentAttendanceItem(student, record)
                    }.sortedBy { it.student.fullName }
                }.collect { items ->
                    _state.update { it.copy(items = items, isLoading = false) }
                }
            }
        }
    }

    fun toggleAttendance(studentId: String, sessionId: String, isPresent: Boolean) {
        viewModelScope.launch {
            val existingRecord = _state.value.items.find { it.student.id == studentId }?.attendance
            
            val record = existingRecord?.copy(
                status = if (isPresent) AttendanceStatus.PRESENT else AttendanceStatus.ABSENT,
                verificationMethod = VerificationMethod.MANUAL_OVERRIDE,
                manuallyModified = true,
                timestamp = System.currentTimeMillis()
            ) ?: Attendance(
                id = "${sessionId}_${studentId}",
                sessionId = sessionId,
                studentId = studentId,
                timestamp = System.currentTimeMillis(),
                status = if (isPresent) AttendanceStatus.PRESENT else AttendanceStatus.ABSENT,
                verificationMethod = VerificationMethod.MANUAL_OVERRIDE,
                confidenceScore = 1.0f,
                manuallyModified = true
            )
            
            if (existingRecord != null) {
                attendanceRepo.update(record)
            } else {
                attendanceRepo.mark(record)
            }
        }
    }
}
