package danny.productions.ltd.presentation.teacher.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateSessionState(
    val lectureName: String = "",
    val subject: String = "",
    val durationMinutes: String = "60",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdSessionId: String? = null
)

class CreateSessionViewModel : ViewModel() {
    private val createSessionUseCase = ServiceLocator.createSessionUseCase
    private val authRepo = ServiceLocator.authRepository

    private val _state = MutableStateFlow(CreateSessionState())
    val state: StateFlow<CreateSessionState> = _state.asStateFlow()

    fun updateForm(
        lectureName: String = _state.value.lectureName,
        subject: String = _state.value.subject,
        durationMinutes: String = _state.value.durationMinutes
    ) {
        _state.update { 
            it.copy(
                lectureName = lectureName,
                subject = subject,
                durationMinutes = durationMinutes,
                error = null
            ) 
        }
    }

    fun createSession() {
        val s = _state.value
        val duration = s.durationMinutes.toIntOrNull()
        if (duration == null || duration <= 0) {
            _state.update { it.copy(error = "Invalid duration") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val teacherId = authRepo.getLoggedInTeacherId()
            if (teacherId == null) {
                _state.update { it.copy(isLoading = false, error = "Not logged in") }
                return@launch
            }

            val result = createSessionUseCase(
                lectureName = s.lectureName,
                subject = s.subject,
                teacherId = teacherId,
                durationMillis = duration.toLong() * 60 * 1000 // duration is in minutes!
            )
            result.fold(
                onSuccess = { session ->
                    _state.update { it.copy(isLoading = false, createdSessionId = session.id) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
