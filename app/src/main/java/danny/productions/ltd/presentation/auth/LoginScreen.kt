package danny.productions.ltd.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATextField
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.components.LoadingOverlay
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.TextSecondary

@Composable
fun LoginScreen(
    state: LoginState,
    isTeacher: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRollNumberChange: (String) -> Unit,
    onLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = { FRATopBar(title = if (isTeacher) "Teacher Login" else "Student Login", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                if (isTeacher) {
                    FRATextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )
                } else {
                    FRATextField(
                        value = state.rollNumber,
                        onValueChange = onRollNumberChange,
                        label = "Roll Number"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FRATextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = onLogin,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password",
                                tint = TextSecondary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                FRAButton(text = "Login", onClick = onLogin, enabled = !state.isLoading)

                if (isTeacher) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            text = "Don't have an account? Register",
                            color = NeonCyan,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }

            if (state.isLoading) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
fun TeacherRegisterScreen(
    state: LoginState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = { FRATopBar(title = "Register Teacher", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                FRATextField(value = state.registerName, onValueChange = onNameChange, label = "Full Name")
                Spacer(modifier = Modifier.height(12.dp))
                FRATextField(value = state.registerEmail, onValueChange = onEmailChange, label = "Email", keyboardType = KeyboardType.Email)
                Spacer(modifier = Modifier.height(12.dp))
                FRATextField(
                    value = state.registerPassword,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle",
                                tint = TextSecondary
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                FRATextField(
                    value = state.registerConfirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirm Password",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = onRegister,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(32.dp))
                FRAButton(text = "Register", onClick = onRegister, enabled = !state.isLoading)
            }

            if (state.isLoading) {
                LoadingOverlay()
            }
        }
    }
}
