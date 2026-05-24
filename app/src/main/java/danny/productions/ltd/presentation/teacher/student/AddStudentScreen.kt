package danny.productions.ltd.presentation.teacher.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun AddStudentScreen(
    viewModel: StudentViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.addState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAddError()
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            viewModel.resetAddState()
            onBack()
        }
    }

    Scaffold(
        topBar = { FRATopBar(title = "Add Student", onBack = onBack) },
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
                    value = state.rollNumber,
                    onValueChange = { viewModel.updateAddForm(rollNumber = it) },
                    label = "Roll Number"
                )
                FRATextField(
                    value = state.fullName,
                    onValueChange = { viewModel.updateAddForm(fullName = it) },
                    label = "Full Name"
                )
                FRATextField(
                    value = state.department,
                    onValueChange = { viewModel.updateAddForm(department = it) },
                    label = "Department"
                )
                FRATextField(
                    value = state.year,
                    onValueChange = { viewModel.updateAddForm(year = it) },
                    label = "Year (1-6)",
                    keyboardType = KeyboardType.Number
                )
                FRATextField(
                    value = state.password,
                    onValueChange = { viewModel.updateAddForm(password = it) },
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = viewModel::saveStudent
                )

                Spacer(modifier = Modifier.height(16.dp))

                FRAButton(
                    text = "Save Student",
                    onClick = viewModel::saveStudent,
                    enabled = !state.isSaving
                )
            }

            if (state.isSaving) {
                LoadingOverlay()
            }
        }
    }
}
