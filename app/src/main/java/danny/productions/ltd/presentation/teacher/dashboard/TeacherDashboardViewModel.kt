package danny.productions.ltd.presentation.teacher.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeacherDashboardState(
    val activeSessions: List<Session> = emptyList(),
    val totalStudents: Int = 0,
    val isLoading: Boolean = true
)

class TeacherDashboardViewModel : ViewModel() {
    private val sessionRepo = ServiceLocator.sessionRepository
    private val studentRepo = ServiceLocator.studentRepository
    private val authRepo = ServiceLocator.authRepository

    private val _state = MutableStateFlow(TeacherDashboardState())
    val state: StateFlow<TeacherDashboardState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val teacherId = authRepo.getLoggedInTeacherId() ?: return@launch
            
            launch {
                sessionRepo.getActiveSessions(teacherId).collect { sessions ->
                    _state.update { it.copy(activeSessions = sessions, isLoading = false) }
                }
            }
            
            launch {
                studentRepo.countFlow().collect { count ->
                    _state.update { it.copy(totalStudents = count) }
                }
            }
        }
    }
}
