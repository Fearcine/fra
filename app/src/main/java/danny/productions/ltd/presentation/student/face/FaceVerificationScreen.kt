package danny.productions.ltd.presentation.student.face

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.filled.Check
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danny.productions.ltd.di.ServiceLocator
import danny.productions.ltd.presentation.components.FRAButton
import danny.productions.ltd.presentation.components.FRATopBar
import danny.productions.ltd.presentation.theme.DarkBackground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

data class FaceVerificationState(
    val isVerifying: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class FaceVerificationViewModel : ViewModel() {
    private val authRepo = ServiceLocator.authRepository
    private val faceRepository = ServiceLocator.faceRepository
    private val markAttendanceUseCase = ServiceLocator.markAttendanceUseCase
    private var faceEngine: danny.productions.ltd.domain.face.FaceEngine? = null

    private val _state = MutableStateFlow(FaceVerificationState())
    val state: StateFlow<FaceVerificationState> = _state.asStateFlow()

    fun init(engine: danny.productions.ltd.domain.face.FaceEngine) {
        faceEngine = engine
    }

    fun verifyFace(imageBitmap: android.graphics.Bitmap, sessionId: String) {
        if (_state.value.isVerifying) return
        val engine = faceEngine ?: return

        _state.update { it.copy(isVerifying = true, error = null) }
        
        viewModelScope.launch {
            val loggedInStudentId = authRepo.getLoggedInStudentId()
            // If loggedInStudentId is null, we assume this is a "Simulated Student Scan" from Teacher's device.

            // 1. Extract live embedding
            val liveEmbedding = engine.extractFaceEmbedding(imageBitmap)
            if (liveEmbedding == null) {
                _state.update { it.copy(isVerifying = false) } // Silently wait for next frame
                return@launch
            }

            // 2. Fetch registered embeddings from DB
            val registeredFaces = if (loggedInStudentId != null) {
                faceRepository.getByStudentId(loggedInStudentId)
            } else {
                faceRepository.getAll() // Simulated mode: check against all registered faces
            }

            if (registeredFaces.isEmpty()) {
                _state.update { it.copy(isVerifying = false, error = "No registered faces found in database.") }
                return@launch
            }

            // 3. Compare using Cosine Similarity
            var bestScore = -1f
            var matchedStudentId: String? = null
            val faceMatcher = danny.productions.ltd.domain.face.FaceMatcher()
            
            for (face in registeredFaces) {
                val score = faceMatcher.computeCosineSimilarity(liveEmbedding, face.embedding)
                if (score > bestScore) {
                    bestScore = score
                    matchedStudentId = face.studentId
                }
            }

            // Threshold for MobileFaceNet is typically around 0.5 - 0.6
            val threshold = danny.productions.ltd.domain.face.FaceMatcher.MATCH_THRESHOLD
            if (bestScore >= threshold && matchedStudentId != null) {
                // 4. Mark Attendance
                val result = markAttendanceUseCase(
                    studentId = matchedStudentId,
                    sessionId = sessionId,
                    verificationMethod = danny.productions.ltd.domain.model.VerificationMethod.FACE_VERIFIED,
                    confidenceScore = bestScore
                )
                
                result.fold(
                    onSuccess = {
                        _state.update { it.copy(isVerifying = false, success = true) }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isVerifying = false, error = e.message ?: "Failed to mark attendance") }
                    }
                )
            } else {
                _state.update { it.copy(isVerifying = false) } // Silently wait for next frame
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

@Composable
fun FaceVerificationScreen(
    sessionId: String,
    nonce: String,
    viewModel: FaceVerificationViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val engine = remember { danny.productions.ltd.domain.face.FaceEngine(context).apply { init() } }
    
    LaunchedEffect(Unit) {
        viewModel.init(engine)
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

    LaunchedEffect(state.success) {
        if (state.success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = { FRATopBar(title = "Verify Identity", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.success) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Check, contentDescription = "Success", tint = androidx.compose.ui.graphics.Color.Green, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Identity Verified", color = androidx.compose.ui.graphics.Color.White)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    VerificationCameraPreview { bitmap ->
                        viewModel.verifyFace(bitmap, sessionId)
                    }
                    if (state.isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationCameraPreview(onFaceCaptured: (android.graphics.Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var isProcessing by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            if (!isProcessing) {
                                isProcessing = true
                                val bitmap = danny.productions.ltd.utils.CameraXBitmapUtils.toBitmap(imageProxy)
                                onFaceCaptured(bitmap)
                                // We rely on the ViewModel updating state to know when we can process next,
                                // but a simple timeout or just letting the UI recompose handles the throttling better.
                                // For simplicity, we just unlock after processing.
                                isProcessing = false
                            }
                        } catch (e: Exception) {
                            isProcessing = false
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
}
