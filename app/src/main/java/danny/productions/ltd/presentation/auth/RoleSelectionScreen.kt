package danny.productions.ltd.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRAOutlinedButton
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonBlue
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.TextSecondary
import danny.productions.ltd.presentation.theme.TextTertiary

@Composable
fun RoleSelectionScreen(
    onTeacherSelected: () -> Unit,
    onStudentSelected: () -> Unit
) {
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) {}

    androidx.compose.runtime.LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = NeonCyan
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FRA",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Text(
                text = "Face Recognition Attendance",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Select your role",
                color = TextTertiary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            FRAButton(
                text = "Teacher",
                onClick = onTeacherSelected,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(16.dp))
            FRAOutlinedButton(
                text = "Student",
                onClick = onStudentSelected,
                color = NeonBlue
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // DEMO SYNC TOOLS
            val context = androidx.compose.ui.platform.LocalContext.current
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
            val backupManager = androidx.compose.runtime.remember { danny.productions.ltd.domain.usecase.demo.DatabaseBackupManager(context) }
            
            val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    coroutineScope.launch {
                        val success = backupManager.importDatabase(uri)
                        android.widget.Toast.makeText(context, if (success) "DB Imported! Restart app." else "Import failed", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }

            Text("Physical Demo Sync", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                androidx.compose.material3.TextButton(onClick = {
                    coroutineScope.launch {
                        val uri = backupManager.exportDatabase()
                        if (uri != null) {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "application/octet-stream"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share Demo DB"))
                        } else {
                            android.widget.Toast.makeText(context, "Export failed", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Export DB", color = NeonCyan)
                }
                androidx.compose.material3.TextButton(onClick = {
                    importLauncher.launch("*/*")
                }) {
                    Text("Import DB", color = NeonCyan)
                }
            }
        }
    }
}
