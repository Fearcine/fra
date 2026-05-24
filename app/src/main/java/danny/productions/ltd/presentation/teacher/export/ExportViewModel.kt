package danny.productions.ltd.presentation.teacher.export

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.usecase.export.ExportToExcelUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

data class ExportState(
    val isExporting: Boolean = false,
    val exportedFile: File? = null,
    val exportedFilesList: List<File> = emptyList(),
    val error: String? = null
)

class ExportViewModel : ViewModel() {
    private val sessionRepo = ServiceLocator.sessionRepository
    private val attendanceRepo = ServiceLocator.attendanceRepository
    private val studentRepo = ServiceLocator.studentRepository
    
    private val _state = MutableStateFlow(ExportState())
    val state: StateFlow<ExportState> = _state.asStateFlow()

    fun loadExportedFiles(context: Context) {
        viewModelScope.launch {
            val dir = context.getExternalFilesDir(null)
            val files = dir?.listFiles { file -> file.name.endsWith(".xlsx") }?.toList() ?: emptyList()
            _state.update { it.copy(exportedFilesList = files.sortedByDescending { f -> f.lastModified() }) }
        }
    }

    fun exportSession(context: Context, sessionId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isExporting = true, error = null, exportedFile = null) }
            val session = sessionRepo.getById(sessionId)
            if (session == null) {
                _state.update { it.copy(isExporting = false, error = "Session not found") }
                return@launch
            }
            
            val records = attendanceRepo.getBySession(sessionId).firstOrNull() ?: emptyList()
            val students = studentRepo.getAll().firstOrNull() ?: emptyList()
            val exportUseCase = ExportToExcelUseCase(context)
            
            val result = exportUseCase(session, records, students)
            result.fold(
                onSuccess = { file ->
                    _state.update { it.copy(isExporting = false, exportedFile = file) }
                    loadExportedFiles(context)
                },
                onFailure = { e ->
                    _state.update { it.copy(isExporting = false, error = e.message) }
                }
            )
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
