package danny.productions.ltd.presentation.teacher.qr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATextField
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.components.LoadingOverlay
import danny.productions.ltd.presentation.theme.DarkBackground

@Composable
fun CreateSessionScreen(
    viewModel: CreateSessionViewModel,
    onNavigateToQRDisplay: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.createdSessionId) {
        state.createdSessionId?.let { id ->
            onNavigateToQRDisplay(id)
        }
    }

    Scaffold(
        topBar = { FRATopBar(title = "Create Session", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FRATextField(
                    value = state.lectureName,
                    onValueChange = { viewModel.updateForm(lectureName = it) },
                    label = "Lecture Name (e.g. CS101 Lecture 5)"
                )
                FRATextField(
                    value = state.subject,
                    onValueChange = { viewModel.updateForm(subject = it) },
                    label = "Subject"
                )
                FRATextField(
                    value = state.durationMinutes,
                    onValueChange = { viewModel.updateForm(durationMinutes = it) },
                    label = "Duration (Minutes)",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    onImeAction = viewModel::createSession
                )

                Spacer(modifier = Modifier.height(16.dp))

                FRAButton(
                    text = "Start Session",
                    onClick = viewModel::createSession,
                    enabled = !state.isLoading
                )
            }

            if (state.isLoading) {
                LoadingOverlay()
            }
        }
    }
}
