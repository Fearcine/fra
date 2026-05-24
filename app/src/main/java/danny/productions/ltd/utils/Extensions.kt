package danny.productions.ltd.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun <T> Flow<T>.catchAndLog(): Flow<T> = catch { e ->
    android.util.Log.e("FRA", "Flow error", e)
}
