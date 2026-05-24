package danny.productions.ltd.presentation.teacher.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import danny.productions.ltd.presentation.components.FRACard
import danny.productions.ltd.presentation.components.FRATextField
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.components.LoadingOverlay
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    onNavigateToAddStudent: () -> Unit,
    onNavigateToStudentDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        topBar = { FRATopBar(title = "Students", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddStudent,
                containerColor = NeonCyan,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "Add Student")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            FRATextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = "Search Roll/Name/Dept",
                trailingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    LoadingOverlay()
                }
            } else if (state.students.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No students found.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.students) { student ->
                        FRACard(onClick = { onNavigateToStudentDetail(student.id) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(student.fullName, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    Text("${student.department} - Year ${student.year}", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(student.rollNumber, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
