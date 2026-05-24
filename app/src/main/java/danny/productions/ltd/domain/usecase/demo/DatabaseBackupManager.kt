package danny.productions.ltd.domain.usecase.demo

import android.content.Context
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DatabaseBackupManager(private val context: Context) {

    private val dbName = "fra_database"

    suspend fun exportDatabase(): Uri? = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath(dbName)
            if (!dbFile.exists()) return@withContext null

            val backupDir = File(context.getExternalFilesDir(null), "DemoBackups")
            if (!backupDir.exists()) backupDir.mkdirs()

            val backupFile = File(backupDir, "fra_demo_db.sqlite")
            // Force WAL checkpoint so all data is written to the main .sqlite file
            try {
                danny.productions.ltd.di.ServiceLocator.database.query(
                    androidx.sqlite.db.SimpleSQLiteQuery("pragma wal_checkpoint(full)")
                ).moveToFirst()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val filesToCopy = listOf(
                dbFile
            )

            for (file in filesToCopy) {
                if (file.exists()) {
                    val dest = File(backupDir, file.name.replace(dbName, "fra_demo_db.sqlite"))
                    FileInputStream(file).use { input ->
                        FileOutputStream(dest).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
            
            // Use FileProvider to share
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                backupFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun importDatabase(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath(dbName)
            
            // Close DB first! 
            try {
                danny.productions.ltd.di.ServiceLocator.database.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            // Delete WAL and SHM to prevent corruption
            File(dbFile.absolutePath + "-wal").delete()
            File(dbFile.absolutePath + "-shm").delete()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
