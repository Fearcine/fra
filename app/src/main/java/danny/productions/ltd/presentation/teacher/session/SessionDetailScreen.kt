package danny.productions.ltd.presentation.teacher.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import danny.productions.ltd.domain.model.AttendanceStatus
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.components.LoadingOverlay
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.DarkSurface
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun SessionDetailScreen(
    sessionId: String,
    viewModel: SessionDetailViewModel,
    onNavigateToExport: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    Scaffold(
        topBar = { FRATopBar(title = "Session Details", onBack = onBack) },
        containerColor = DarkBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                LoadingOverlay()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    state.session?.let { session ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Lecture: ${session.lectureName}", style = MaterialTheme.typography.titleLarge, color = NeonCyan)
                            Text("Subject: ${session.subject}", color = TextSecondary)
                            Text("Present: ${state.items.count { it.attendance?.status == AttendanceStatus.PRESENT }} / ${state.items.size}", color = TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            FRAButton(
                                text = "Export to Excel",
                                onClick = { onNavigateToExport(session.id) }
                            )
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items) { item ->
                            AttendanceItem(
                                item = item,
                                onToggle = { isPresent ->
                                    state.session?.let { 
                                        viewModel.toggleAttendance(item.student.id, it.id, isPresent) 
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceItem(
    item: StudentAttendanceItem,
    onToggle: (Boolean) -> Unit
) {
    val isPresent = item.attendance?.status == AttendanceStatus.PRESENT

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.student.fullName, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                Text(text = "Roll No: ${item.student.rollNumber}", color = TextSecondary)
                if (item.attendance != null) {
                    Text(text = "Method: ${item.attendance.verificationMethod.name}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    if (item.attendance.manuallyModified) {
                        Text(text = "MANUALLY MODIFIED", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    Text(text = "No Record (Absent)", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (isPresent) "Present" else "Absent", color = if (isPresent) androidx.compose.ui.graphics.Color.Green else MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                Switch(
                    checked = isPresent,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = androidx.compose.ui.graphics.Color.Green,
                        checkedTrackColor = androidx.compose.ui.graphics.Color.Green.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.error,
                        uncheckedTrackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
