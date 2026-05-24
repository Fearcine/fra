package danny.productions.ltd.presentation.teacher.qr

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.model.QRPayload
import danny.productions.ltd.utils.QRSigner
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QRDisplayState(
    val sessionId: String = "",
    val qrBitmap: Bitmap? = null,
    val countdown: Int = 10,
    val isSessionActive: Boolean = true,
    val currentNonce: String = ""
)

class QRDisplayViewModel : ViewModel() {
    private val sessionRepo = ServiceLocator.sessionRepository
    private val prefs = ServiceLocator.preferencesManager

    private val _state = MutableStateFlow(QRDisplayState())
    val state: StateFlow<QRDisplayState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun init(sessionId: String) {
        _state.update { it.copy(sessionId = sessionId) }
        startQRRotation()
    }

    private fun startQRRotation() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.isSessionActive) {
                generateNewQR()
                for (i in 10 downTo 1) {
                    _state.update { it.copy(countdown = i) }
                    delay(1000)
                }
            }
        }
    }

    private suspend fun generateNewQR() {
        val teacherId = prefs.getLoggedInTeacherId() ?: return
        val deviceId = prefs.getDeviceId()
        val hostIp = "192.168.49.1" // Mock Wi-Fi Direct GO IP
        val port = 8888
        val nonce = System.currentTimeMillis().toString()
        val timestamp = System.currentTimeMillis()
        
        val secret = prefs.getSecretKey()

        val payload = QRPayload(
            sessionId = _state.value.sessionId,
            teacherDeviceId = deviceId,
            hostIp = hostIp,
            port = port,
            issuedAt = timestamp,
            nonce = nonce
        )

        val jsonStr = payload.toJson()
        val signature = QRSigner.sign(jsonStr, secret)
        val fullContent = "$jsonStr|$signature"
        
        val bitmap = QRCodeGenerator.generate(fullContent)
        _state.update { it.copy(qrBitmap = bitmap, currentNonce = nonce) }
    }

    fun stopSession() {
        _state.update { it.copy(isSessionActive = false) }
        timerJob?.cancel()
        viewModelScope.launch {
            // In real app, close session in DB here
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
