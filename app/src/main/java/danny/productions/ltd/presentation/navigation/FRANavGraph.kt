package danny.productions.ltd.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import danny.productions.ltd.domain.model.UserRole
import danny.productions.ltd.presentation.auth.LoginScreen
import danny.productions.ltd.presentation.auth.LoginViewModel
import danny.productions.ltd.presentation.auth.RoleSelectionScreen
import danny.productions.ltd.presentation.auth.TeacherRegisterScreen
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun FRANavGraph(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val loginState by loginViewModel.state.collectAsState()

    val startDestination = when {
        loginState.isLoggedIn && loginState.selectedRole == UserRole.TEACHER -> Screen.TeacherDashboard.route
        loginState.isLoggedIn && loginState.selectedRole == UserRole.STUDENT -> Screen.StudentDashboard.route
        else -> Screen.RoleSelection.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // Auth
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onTeacherSelected = { navController.navigate(Screen.TeacherLogin.route) },
                onStudentSelected = { navController.navigate(Screen.StudentLogin.route) }
            )
        }

        composable(Screen.TeacherLogin.route) {
            LoginScreen(
                state = loginState,
                isTeacher = true,
                onEmailChange = loginViewModel::onEmailChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                onRollNumberChange = {},
                onLogin = loginViewModel::loginTeacher,
                onNavigateToRegister = { navController.navigate(Screen.TeacherRegister.route) },
                onBack = { navController.popBackStack() },
                onClearError = loginViewModel::clearError
            )
        }

        composable(Screen.TeacherRegister.route) {
            TeacherRegisterScreen(
                state = loginState,
                onNameChange = loginViewModel::onRegisterNameChange,
                onEmailChange = loginViewModel::onRegisterEmailChange,
                onPasswordChange = loginViewModel::onRegisterPasswordChange,
                onConfirmPasswordChange = loginViewModel::onRegisterConfirmPasswordChange,
                onRegister = loginViewModel::registerTeacher,
                onBack = { navController.popBackStack() },
                onClearError = loginViewModel::clearError
            )
        }

        composable(Screen.StudentLogin.route) {
            LoginScreen(
                state = loginState,
                isTeacher = false,
                onEmailChange = {},
                onPasswordChange = loginViewModel::onPasswordChange,
                onRollNumberChange = loginViewModel::onRollNumberChange,
                onLogin = loginViewModel::loginStudent,
                onNavigateToRegister = {},
                onBack = { navController.popBackStack() },
                onClearError = loginViewModel::clearError
            )
        }

        // Phase 7: Teacher Dashboard & Student CRUD
        composable(Screen.TeacherDashboard.route) {
            danny.productions.ltd.presentation.teacher.dashboard.TeacherDashboardScreen(
                onNavigateToCreateSession = { navController.navigate(Screen.CreateSession.route) },
                onNavigateToStudentList = { navController.navigate(Screen.StudentList.route) },
                onNavigateToSessionDetail = { id -> navController.navigate(Screen.SessionDetail.createRoute(id)) },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.StudentList.route) {
            val vm: danny.productions.ltd.presentation.teacher.student.StudentViewModel = viewModel()
            danny.productions.ltd.presentation.teacher.student.StudentListScreen(
                viewModel = vm,
                onNavigateToAddStudent = { navController.navigate(Screen.AddStudent.route) },
                onNavigateToStudentDetail = { id -> navController.navigate(Screen.StudentDetail.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddStudent.route) {
            val backStackEntry = remember { navController.getBackStackEntry(Screen.StudentList.route) }
            val vm: danny.productions.ltd.presentation.teacher.student.StudentViewModel = viewModel(backStackEntry)
            danny.productions.ltd.presentation.teacher.student.AddStudentScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StudentDetail.route) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: return@composable
            val parentEntry = remember { navController.getBackStackEntry(Screen.StudentList.route) }
            val vm: danny.productions.ltd.presentation.teacher.student.StudentViewModel = viewModel(parentEntry)
            danny.productions.ltd.presentation.teacher.student.StudentDetailScreen(
                studentId = studentId,
                viewModel = vm,
                onNavigateToFaceRegistration = { id -> navController.navigate(Screen.FaceRegistration.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        // Phase 11: Student Dashboard & QR Scanner
        composable(Screen.StudentDashboard.route) {
            danny.productions.ltd.presentation.student.dashboard.StudentDashboardScreen(
                onNavigateToQRScanner = { navController.navigate(Screen.QRScanner.route) },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.QRScanner.route) {
            val vm: danny.productions.ltd.presentation.student.qr.QRScannerViewModel = viewModel()
            danny.productions.ltd.presentation.student.qr.QRScannerScreen(
                viewModel = vm,
                onNavigateToFaceVerification = { sessionId, nonce -> 
                    navController.navigate(Screen.FaceVerification.createRoute(sessionId, nonce)) 
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FaceVerification.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val nonce = backStackEntry.arguments?.getString("nonce") ?: return@composable
            val vm: danny.productions.ltd.presentation.student.face.FaceVerificationViewModel = viewModel()
            
            danny.productions.ltd.presentation.student.face.FaceVerificationScreen(
                sessionId = sessionId,
                nonce = nonce,
                viewModel = vm,
                onSuccess = {
                    navController.navigate(Screen.StudentDashboard.route) {
                        popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateSession.route) {
            val vm: danny.productions.ltd.presentation.teacher.qr.CreateSessionViewModel = viewModel()
            danny.productions.ltd.presentation.teacher.qr.CreateSessionScreen(
                viewModel = vm,
                onNavigateToQRDisplay = { id -> navController.navigate(Screen.QRDisplay.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.QRDisplay.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val vm: danny.productions.ltd.presentation.teacher.qr.QRDisplayViewModel = viewModel()
            danny.productions.ltd.presentation.teacher.qr.QRDisplayScreen(
                sessionId = sessionId,
                viewModel = vm,
                onNavigateToDashboard = {
                    navController.navigate(Screen.TeacherDashboard.route) {
                        popUpTo(Screen.TeacherDashboard.route) { inclusive = true }
                    }
                },
                onSimulateStudent = { sid, nonce ->
                    navController.navigate(Screen.FaceVerification.createRoute(sid, nonce))
                }
            )
        }

        composable(Screen.SessionDetail.route) { backStackEntry -> 
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val vm: danny.productions.ltd.presentation.teacher.session.SessionDetailViewModel = viewModel()
            danny.productions.ltd.presentation.teacher.session.SessionDetailScreen(
                sessionId = sessionId,
                viewModel = vm,
                onNavigateToExport = { id -> navController.navigate(Screen.Export.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FaceRegistration.route) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: return@composable
            val vm: danny.productions.ltd.presentation.teacher.face.FaceRegistrationViewModel = viewModel()
            danny.productions.ltd.presentation.teacher.face.FaceRegistrationScreen(
                studentId = studentId,
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Export.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            val vm: danny.productions.ltd.presentation.teacher.export.ExportViewModel = viewModel()
            danny.productions.ltd.presentation.teacher.export.ExportScreen(
                sessionId = sessionId,
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = TextSecondary)
    }
}
