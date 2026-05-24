package danny.productions.ltd.presentation.auth

import danny.productions.ltd.domain.model.Teacher
import danny.productions.ltd.domain.model.UserRole

data class LoginState(
    val email: String = "",
    val password: String = "",
    val rollNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInTeacher: Teacher? = null,
    val loggedInStudentId: String? = null,
    val selectedRole: UserRole? = null,
    val isLoggedIn: Boolean = false,
    // Registration fields
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val isRegistering: Boolean = false
)
