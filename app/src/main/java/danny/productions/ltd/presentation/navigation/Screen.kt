package danny.productions.ltd.presentation.navigation

sealed class Screen(val route: String) {
    data object RoleSelection : Screen("role_selection")
    data object TeacherLogin : Screen("teacher_login")
    data object TeacherRegister : Screen("teacher_register")
    data object StudentLogin : Screen("student_login")

    data object TeacherDashboard : Screen("teacher_dashboard")
    data object StudentList : Screen("student_list")
    data object AddStudent : Screen("add_student")
    data object StudentDetail : Screen("student_detail/{studentId}") {
        fun createRoute(studentId: String) = "student_detail/$studentId"
    }

    data object CreateSession : Screen("create_session")
    data object SessionHistory : Screen("session_history")
    data object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: String) = "session_detail/$sessionId"
    }
    data object QRDisplay : Screen("qr_display/{sessionId}") {
        fun createRoute(sessionId: String) = "qr_display/$sessionId"
    }

    data object FaceRegistration : Screen("face_registration/{studentId}") {
        fun createRoute(studentId: String) = "face_registration/$studentId"
    }

    data object AttendanceReview : Screen("attendance_review/{sessionId}") {
        fun createRoute(sessionId: String) = "attendance_review/$sessionId"
    }
    data object AttendanceAnalytics : Screen("attendance_analytics")
    data object Export : Screen("export/{sessionId}") {
        fun createRoute(sessionId: String) = "export/$sessionId"
    }

    data object StudentDashboard : Screen("student_dashboard")
    data object QRScanner : Screen("qr_scanner")
    data object FaceVerification : Screen("face_verification/{sessionId}/{nonce}") {
        fun createRoute(sessionId: String, nonce: String) = "face_verification/$sessionId/$nonce"
    }
    data object AttendanceHistory : Screen("attendance_history")
    data object ConnectionStatus : Screen("connection_status")
}
