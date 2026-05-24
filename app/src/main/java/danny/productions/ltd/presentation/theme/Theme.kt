package danny.productions.ltd.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DarkBackground,
    primaryContainer = NeonCyanDim,
    onPrimaryContainer = TextPrimary,
    secondary = NeonBlue,
    onSecondary = DarkBackground,
    secondaryContainer = NeonBlueDim,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonPurple,
    onTertiary = DarkBackground,
    error = NeonRed,
    onError = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = TextTertiary
)

@Composable
fun FRATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = FRATypography,
        shapes = FRAShapes,
        content = content
    )
}
