package danny.productions.ltd.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

class PreferencesManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "fra_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    fun getSecretKey(): String {
        return "fra_shared_secret_key_12345"
    }

    suspend fun getLoggedInTeacherId(): String? = prefs.getString(KEY_TEACHER_ID, null)
    suspend fun setLoggedInTeacherId(id: String?) = prefs.edit().putString(KEY_TEACHER_ID, id).apply()
    suspend fun getLoggedInStudentId(): String? = prefs.getString(KEY_STUDENT_ID, null)
    suspend fun setLoggedInStudentId(id: String?) = prefs.edit().putString(KEY_STUDENT_ID, id).apply()

    fun getLoggedInRole(): String? = prefs.getString(KEY_ROLE, null)
    fun setLoggedInRole(role: String?) = prefs.edit().putString(KEY_ROLE, role).apply()

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_SECRET = "secret_key"
        private const val KEY_TEACHER_ID = "logged_in_teacher"
        private const val KEY_STUDENT_ID = "logged_in_student"
        private const val KEY_ROLE = "logged_in_role"
    }
}
