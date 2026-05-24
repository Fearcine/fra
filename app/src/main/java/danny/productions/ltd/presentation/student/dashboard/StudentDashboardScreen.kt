package danny.productions.ltd.presentation.student.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun StudentDashboardScreen(
    onNavigateToQRScanner: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            FRATopBar(
                title = "Student Dashboard",
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = NeonCyan)
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to FRA",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Connect to your teacher's Wi-Fi Direct network, then scan their QR code to mark your attendance.",
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            FRAButton(
                text = "Scan Attendance QR",
                onClick = onNavigateToQRScanner
            )
        }
    }
}
