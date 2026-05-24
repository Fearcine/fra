package danny.productions.ltd.utils

import danny.productions.ltd.domain.model.Attendance
import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.domain.model.Session
import java.io.File
import java.io.FileWriter

object CsvExporter {

    fun export(
        file: File,
        attendanceList: List<Attendance>,
        students: Map<String, Student>,
        sessions: Map<String, Session>
    ): Result<File> = try {
        file.parentFile?.mkdirs()
        FileWriter(file).use { writer ->
            writer.appendLine("Roll Number,Student Name,Department,Lecture,Subject,Date,Time,Status,Verification Method,Confidence,Manual Override")
            for (record in attendanceList) {
                val student = students[record.studentId]
                val session = sessions[record.sessionId]
                writer.appendLine(
                    listOf(
                        student?.rollNumber.orEmpty().csvEscape(),
                        student?.fullName.orEmpty().csvEscape(),
                        student?.department.orEmpty().csvEscape(),
                        session?.lectureName.orEmpty().csvEscape(),
                        session?.subject.orEmpty().csvEscape(),
                        DateTimeUtils.formatDate(record.timestamp),
                        DateTimeUtils.formatTime(record.timestamp),
                        record.status.name,
                        record.verificationMethod.name,
                        "%.2f".format(record.confidenceScore),
                        if (record.manuallyModified) "YES" else "NO"
                    ).joinToString(",")
                )
            }
        }
        Result.success(file)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun String.csvEscape(): String {
        return if (contains(",") || contains("\"") || contains("\n")) {
            "\"${replace("\"", "\"\"")}\""
        } else this
    }
}
