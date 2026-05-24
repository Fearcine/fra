package danny.productions.ltd.presentation.teacher.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.model.Student
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StudentListState(
    val students: List<Student> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

data class AddStudentState(
    val rollNumber: String = "",
    val fullName: String = "",
    val department: String = "",
    val year: String = "",
    val password: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class StudentViewModel : ViewModel() {
    private val searchUseCase = ServiceLocator.searchStudentsUseCase
    private val addStudentUseCase = ServiceLocator.addStudentUseCase
    private val removeStudentUseCase = ServiceLocator.removeStudentUseCase

    private val _searchQuery = MutableStateFlow("")
    
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val listState: StateFlow<StudentListState> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            searchUseCase(query).map { list ->
                StudentListState(students = list, searchQuery = query, isLoading = false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentListState())

    private val _addState = MutableStateFlow(AddStudentState())
    val addState: StateFlow<AddStudentState> = _addState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun updateAddForm(
        rollNumber: String = _addState.value.rollNumber,
        fullName: String = _addState.value.fullName,
        department: String = _addState.value.department,
        year: String = _addState.value.year,
        password: String = _addState.value.password
    ) {
        _addState.update { 
            it.copy(
                rollNumber = rollNumber, 
                fullName = fullName, 
                department = department, 
                year = year, 
                password = password,
                error = null
            ) 
        }
    }

    fun saveStudent() {
        val s = _addState.value
        val yearInt = s.year.toIntOrNull()
        if (yearInt == null || yearInt < 1 || yearInt > 6) {
            _addState.update { it.copy(error = "Invalid year") }
            return
        }

        viewModelScope.launch {
            _addState.update { it.copy(isSaving = true, error = null) }
            val result = addStudentUseCase(s.rollNumber, s.fullName, s.department, yearInt, s.password)
            result.fold(
                onSuccess = {
                    _addState.update { AddStudentState(success = true) }
                },
                onFailure = { e ->
                    _addState.update { it.copy(isSaving = false, error = e.message) }
                }
            )
        }
    }

    fun deleteStudent(id: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            removeStudentUseCase(id)
            onDeleted()
        }
    }

    fun clearAddError() {
        _addState.update { it.copy(error = null) }
    }
    
    fun resetAddState() {
        _addState.value = AddStudentState()
    }
}
