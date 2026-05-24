package danny.productions.ltd.presentation.student.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.usecase.qr.ValidateQRUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QRScannerState(
    val isScanning: Boolean = true,
    val error: String? = null,
    val validatedSessionId: String? = null,
    val validatedNonce: String? = null
)

class QRScannerViewModel : ViewModel() {
    private val validateQRUseCase = ServiceLocator.validateQRUseCase
    private val prefs = ServiceLocator.preferencesManager

    private val _state = MutableStateFlow(QRScannerState())
    val state: StateFlow<QRScannerState> = _state.asStateFlow()

    fun onQrScanned(rawContent: String) {
        if (!_state.value.isScanning) return
        _state.update { it.copy(isScanning = false) }

        viewModelScope.launch {
            val secret = prefs.getSecretKey()
            when (val result = validateQRUseCase(rawContent, secret)) {
                is ValidateQRUseCase.ValidationResult.Valid -> {
                    _state.update { 
                        it.copy(
                            validatedSessionId = result.payload.sessionId,
                            validatedNonce = result.payload.nonce
                        ) 
                    }
                }
                is ValidateQRUseCase.ValidationResult.Expired -> {
                    _state.update { it.copy(error = "QR Code Expired. Try again.", isScanning = true) }
                }
                is ValidateQRUseCase.ValidationResult.InvalidSignature -> {
                    _state.update { it.copy(error = "Invalid QR Signature.", isScanning = true) }
                }
                is ValidateQRUseCase.ValidationResult.InvalidFormat -> {
                    _state.update { it.copy(error = "Invalid QR Format.", isScanning = true) }
                }
            }
        }
    }

    fun resumeScanning() {
        _state.update { it.copy(isScanning = true, error = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
