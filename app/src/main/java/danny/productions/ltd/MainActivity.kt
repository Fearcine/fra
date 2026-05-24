package danny.productions.ltd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import danny.productions.ltd.presentation.navigation.FRANavGraph
import danny.productions.ltd.presentation.theme.FRATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FRATheme {
                FRANavGraph()
            }
        }
    }
}
