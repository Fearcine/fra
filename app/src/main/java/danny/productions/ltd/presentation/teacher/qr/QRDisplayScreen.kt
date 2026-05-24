package danny.productions.ltd.presentation.teacher.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.NeonRed
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun QRDisplayScreen(
    sessionId: String,
    viewModel: QRDisplayViewModel,
    onNavigateToDashboard: () -> Unit,
    onSimulateStudent: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.init(sessionId)
    }

    Scaffold(
        topBar = { FRATopBar(title = "Scan to Connect", onBack = null) },
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
            Text("Session ID: ${sessionId.take(8)}", color = TextSecondary)
            Spacer(modifier = Modifier.height(32.dp))

            if (state.qrBitmap != null) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Image(
                        bitmap = state.qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "QR updates in ${state.countdown}s",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Students must be on the same Wi-Fi network to connect.", color = TextSecondary)

            Spacer(modifier = Modifier.height(32.dp))

            FRAButton(
                text = "Simulate Student Scan",
                onClick = {
                    onSimulateStudent(state.sessionId, state.currentNonce)
                },
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            FRAButton(
                text = "End Session",
                onClick = {
                    viewModel.stopSession()
                    onNavigateToDashboard()
                },
                color = NeonRed
            )
        }
    }
}
