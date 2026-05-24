package danny.productions.ltd.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import danny.productions.ltd.presentation.theme.DarkCard
import danny.productions.ltd.presentation.theme.NeonCyan
import danny.productions.ltd.presentation.theme.NeonRed
import danny.productions.ltd.presentation.theme.TextPrimary

@Composable
fun FRADialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, color = TextPrimary) },
        text = { Text(text = message, color = TextPrimary.copy(alpha = 0.8f)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = if (isDestructive) NeonRed else NeonCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = TextPrimary.copy(alpha = 0.6f))
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(16.dp)
    )
}
