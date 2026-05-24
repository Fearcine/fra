package danny.productions.ltd.presentation.teacher.face

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import danny.productions.ltd.domain.face.FaceEngine
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.theme.DarkBackground
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.NeonGreen
import java.util.concurrent.Executors

@Composable
fun FaceRegistrationScreen(
    studentId: String,
    viewModel: FaceRegistrationViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val engine = remember { FaceEngine(context).apply { init() } }
    
    LaunchedEffect(studentId) {
        viewModel.init(studentId, engine)
    }

    DisposableEffect(Unit) {
        onDispose { engine.close() }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = { FRATopBar(title = "Register Face", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Please capture: ${state.currentAngle.name}",
                style = MaterialTheme.typography.titleLarge,
                color = NeonCyan
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            var latestBitmap by remember { mutableStateOf<Bitmap?>(null) }

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            ) {
                if (!state.isComplete) {
                    CameraPreview(
                        onFrame = { bitmap ->
                            viewModel.processFrame(bitmap)
                        }
                    )
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    if (state.isCooldown) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Get Ready...", color = Color.White)
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Registration Complete!", color = NeonGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            if (!state.isComplete) {
                // No manual capture button needed, it auto-captures.
            } else {
                FRAButton(
                    text = "Done",
                    onClick = onBack
                )
            }
        }
    }
}

@Composable
fun CameraPreview(onFrame: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val mainExecutor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                
                imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                    try {
                        val bitmap = danny.productions.ltd.utils.CameraXBitmapUtils.toBitmap(imageProxy)
                        onFrame(bitmap)
                    } catch (e: Exception) {
                        // Ignore
                    } finally {
                        imageProxy.close()
                    }
                }
                
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    android.util.Log.e("FRA", "Camera bind failed", e)
                }
            }, mainExecutor)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
