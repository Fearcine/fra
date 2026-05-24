package danny.productions.ltd.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val loginUseCase = ServiceLocator.loginUseCase
    private val logoutUseCase = ServiceLocator.logoutUseCase
    private val authRepository = ServiceLocator.authRepository
    private val prefs = ServiceLocator.preferencesManager

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            val teacherId = authRepository.getLoggedInTeacherId()
            val studentId = authRepository.getLoggedInStudentId()
            val role = prefs.getLoggedInRole()

            when {
                teacherId != null && role == UserRole.TEACHER.name -> {
                    _state.update { it.copy(isLoggedIn = true, selectedRole = UserRole.TEACHER, loggedInStudentId = null) }
                }
                studentId != null && role == UserRole.STUDENT.name -> {
                    _state.update { it.copy(isLoggedIn = true, selectedRole = UserRole.STUDENT, loggedInStudentId = studentId) }
                }
            }
        }
    }

    fun onEmailChange(email: String) = _state.update { it.copy(email = email, error = null) }
    fun onPasswordChange(password: String) = _state.update { it.copy(password = password, error = null) }
    fun onRollNumberChange(rollNumber: String) = _state.update { it.copy(rollNumber = rollNumber, error = null) }

    fun onRegisterNameChange(name: String) = _state.update { it.copy(registerName = name, error = null) }
    fun onRegisterEmailChange(email: String) = _state.update { it.copy(registerEmail = email, error = null) }
    fun onRegisterPasswordChange(pw: String) = _state.update { it.copy(registerPassword = pw, error = null) }
    fun onRegisterConfirmPasswordChange(pw: String) = _state.update { it.copy(registerConfirmPassword = pw, error = null) }

    fun loginTeacher() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase.teacherLogin(_state.value.email, _state.value.password)
            result.fold(
                onSuccess = { teacher ->
                    prefs.setLoggedInRole(UserRole.TEACHER.name)
                    _state.update { it.copy(isLoading = false, loggedInTeacher = teacher, isLoggedIn = true, selectedRole = UserRole.TEACHER) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun loginStudent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase.studentLogin(_state.value.rollNumber, _state.value.password)
            result.fold(
                onSuccess = { studentId ->
                    prefs.setLoggedInRole(UserRole.STUDENT.name)
                    _state.update { it.copy(isLoading = false, loggedInStudentId = studentId, isLoggedIn = true, selectedRole = UserRole.STUDENT) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun registerTeacher() {
        val s = _state.value
        if (s.registerName.isBlank() || s.registerEmail.isBlank() || s.registerPassword.isBlank()) {
            _state.update { it.copy(error = "All fields required") }
            return
        }
        if (s.registerPassword != s.registerConfirmPassword) {
            _state.update { it.copy(error = "Passwords do not match") }
            return
        }
        if (s.registerPassword.length < 4) {
            _state.update { it.copy(error = "Password too short") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.registerTeacher(s.registerName.trim(), s.registerEmail.trim(), s.registerPassword)
            result.fold(
                onSuccess = { teacher ->
                    prefs.setLoggedInRole(UserRole.TEACHER.name)
                    _state.update { it.copy(isLoading = false, loggedInTeacher = teacher, isLoggedIn = true, selectedRole = UserRole.TEACHER) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            prefs.setLoggedInRole(null)
            _state.value = LoginState()
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
}
