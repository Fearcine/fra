package danny.productions.ltd.domain.usecase.export

import android.content.Context
import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.model.Session
import danny.productions.ltd.domain.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportToExcelUseCase(private val context: Context) {

    suspend operator fun invoke(session: Session, records: List<Attendance>, students: List<Student>): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Attendance")

                // Header
                val headerRow = sheet.createRow(0)
                headerRow.createCell(0).setCellValue("Student Name")
                headerRow.createCell(1).setCellValue("Roll Number")
                headerRow.createCell(2).setCellValue("Status")
                headerRow.createCell(3).setCellValue("Method")
                headerRow.createCell(4).setCellValue("Timestamp")
                headerRow.createCell(5).setCellValue("Manually Modified")

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                // Data
                records.forEachIndexed { index, record ->
                    val student = students.find { it.id == record.studentId }
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(student?.fullName ?: "Unknown")
                    row.createCell(1).setCellValue(student?.rollNumber ?: "Unknown")
                    row.createCell(2).setCellValue(record.status.name)
                    row.createCell(3).setCellValue(record.verificationMethod.name)
                    row.createCell(4).setCellValue(sdf.format(Date(record.timestamp)))
                    row.createCell(5).setCellValue(if (record.manuallyModified) "YES" else "NO")
                }

                // File
                val fileName = "Attendance_${session.lectureName.replace(" ", "_")}_${session.id.take(6)}.xlsx"
                val file = File(context.getExternalFilesDir(null), fileName)
                FileOutputStream(file).use { out ->
                    workbook.write(out)
                }
                workbook.close()
                Result.success(file)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
