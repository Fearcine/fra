package danny.productions.ltd.presentation.teacher.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import danny.productions.ltd.presentation.components.FRACard
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.components.LoadingOverlay
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.NeonGreen
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun TeacherDashboardScreen(
    onNavigateToCreateSession: () -> Unit,
    onNavigateToStudentList: () -> Unit,
    onNavigateToSessionDetail: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: TeacherDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            FRATopBar(
                title = "Dashboard",
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = NeonCyan)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateSession,
                containerColor = NeonCyan,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "New Session")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        if (state.isLoading) {
            LoadingOverlay()
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Students",
                        value = state.totalStudents.toString(),
                        icon = { Icon(Icons.Default.People, null, tint = NeonCyan) },
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToStudentList
                    )
                    StatCard(
                        title = "Active Sessions",
                        value = state.activeSessions.size.toString(),
                        icon = { Icon(Icons.Default.QrCode, null, tint = NeonGreen) },
                        modifier = Modifier.weight(1f),
                        onClick = null
                    )
                }
            }

            item {
                Text(
                    text = "Active Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            if (state.activeSessions.isEmpty()) {
                item {
                    Text("No active sessions.", color = TextSecondary)
                }
            } else {
                items(state.activeSessions) { session ->
                    FRACard(
                        onClick = { onNavigateToSessionDetail(session.id) }
                    ) {
                        Text(session.lectureName, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Text(session.subject, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ID: ${session.id.take(8)}...", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    FRACard(modifier = modifier, onClick = onClick) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(title, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium, color = NeonCyan)
            }
            icon()
        }
    }
}
