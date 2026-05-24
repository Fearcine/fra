package danny.productions.ltd.presentation.teacher.export

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.core.content.FileProvider
import android.widget.Toast
import android.content.ActivityNotFoundException
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.DarkCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportScreen(
    sessionId: String,
    viewModel: ExportViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadExportedFiles(context)
    }

    Scaffold(
        topBar = { FRATopBar(title = "Export Data", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Export session attendance data to an Excel (.xlsx) file.")
            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.isExporting) {
                CircularProgressIndicator(color = NeonCyan)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Generating Excel file...")
            } else {
                FRAButton(
                    text = "Export Current Session",
                    onClick = { viewModel.exportSession(context, sessionId) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = NeonCyan.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Previously Exported Files", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
            Spacer(modifier = Modifier.height(16.dp))

            if (state.exportedFilesList.isEmpty()) {
                Text("No exported files found.", color = androidx.compose.ui.graphics.Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.exportedFilesList) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Open Excel File"))
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(context, "No app found to open Excel files.", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.InsertDriveFile, contentDescription = "Excel File", tint = NeonCyan)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(file.name, color = androidx.compose.ui.graphics.Color.White)
                                    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                    Text(sdf.format(Date(file.lastModified())), color = androidx.compose.ui.graphics.Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
