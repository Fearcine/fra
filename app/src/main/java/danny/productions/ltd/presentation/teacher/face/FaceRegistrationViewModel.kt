package danny.productions.ltd.presentation.teacher.face

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.face.FaceEngine
import danny.productions.ltd.domain.model.FaceAngle
import danny.productions.ltd.domain.model.FaceData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FaceRegistrationState(
    val studentId: String = "",
    val registeredAngles: List<FaceAngle> = emptyList(),
    val currentAngle: FaceAngle = FaceAngle.FRONT,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false,
    val isCooldown: Boolean = false
)

class FaceRegistrationViewModel : ViewModel() {
    private val registerFaceUseCase = ServiceLocator.registerFaceUseCase
    private val faceRepository = ServiceLocator.faceRepository
    private var faceEngine: FaceEngine? = null

    private val _state = MutableStateFlow(FaceRegistrationState())
    val state: StateFlow<FaceRegistrationState> = _state.asStateFlow()

    fun init(studentId: String, engine: FaceEngine) {
        faceEngine = engine
        _state.update { it.copy(studentId = studentId) }
        loadExisting()
    }

    private fun loadExisting() {
        viewModelScope.launch {
            val existing = faceRepository.getByStudentId(_state.value.studentId)
            val angles = existing.map { it.angle }
            _state.update { 
                it.copy(
                    registeredAngles = angles,
                    currentAngle = determineNextAngle(angles),
                    isComplete = angles.size >= FaceAngle.entries.size
                ) 
            }
        }
    }

    fun processFrame(bitmap: Bitmap) {
        if (_state.value.isProcessing || _state.value.isComplete || _state.value.isCooldown) return
        val engine = faceEngine ?: return

        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, error = null) }
            
            val embedding = engine.extractFaceEmbedding(bitmap)
            if (embedding == null) {
                _state.update { it.copy(isProcessing = false) } // Silently wait for next frame
                return@launch
            }

            val result = registerFaceUseCase(
                studentId = _state.value.studentId,
                embedding = embedding,
                angle = _state.value.currentAngle
            )

            result.fold(
                onSuccess = {
                    val newAngles = _state.value.registeredAngles + _state.value.currentAngle
                    _state.update { 
                        it.copy(
                            isProcessing = false,
                            registeredAngles = newAngles,
                            currentAngle = determineNextAngle(newAngles),
                            isComplete = newAngles.size >= FaceAngle.entries.size,
                            isCooldown = true // Activate cooldown
                        ) 
                    }
                    if (!_state.value.isComplete) {
                        kotlinx.coroutines.delay(2000) // 2 second delay for user to reposition head
                        _state.update { it.copy(isCooldown = false) }
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isProcessing = false, error = e.message) }
                }
            )
        }
    }

    private fun determineNextAngle(registered: List<FaceAngle>): FaceAngle {
        return FaceAngle.entries.firstOrNull { it !in registered } ?: FaceAngle.FRONT
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
