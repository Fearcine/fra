package danny.productions.ltd.presentation.teacher.student

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.presentation.components.FRACard
import danny.productions.ltd.presentation.components.FRADialog
import danny.productions.ltd.presentation.components.FRAOutlinedButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.NeonRed
import danny.productions.ltd.presentation.theme.TextSecondary
import danny.productions.ltd.utils.DateTimeUtils

@Composable
fun StudentDetailScreen(
    studentId: String,
    viewModel: StudentViewModel,
    onNavigateToFaceRegistration: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.listState.collectAsState()
    val student = state.students.find { it.id == studentId }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (student == null) {
        onBack()
        return
    }

    Scaffold(
        topBar = {
            FRATopBar(
                title = "Student Detail",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete", tint = NeonRed)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FRACard {
                Text(student.fullName, style = MaterialTheme.typography.headlineMedium, color = NeonCyan, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("Roll Number", student.rollNumber)
                DetailRow("Department", student.department)
                DetailRow("Year", student.year.toString())
                DetailRow("Registered", DateTimeUtils.formatDate(student.registeredAt))
            }

            Spacer(modifier = Modifier.height(16.dp))

            FRAOutlinedButton(
                text = "Manage Face Data (Phase 8)",
                onClick = { onNavigateToFaceRegistration(student.id) }
            )
        }
    }

    if (showDeleteDialog) {
        FRADialog(
            title = "Delete Student",
            message = "Are you sure you want to delete ${student.fullName}? This will also delete their face data and attendance history.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteStudent(studentId, onBack)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary)
        Text(value, color = NeonCyan, fontWeight = FontWeight.Medium)
    }
}
