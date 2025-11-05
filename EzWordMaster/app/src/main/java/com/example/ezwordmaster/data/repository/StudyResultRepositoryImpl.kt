package com.example.ezwordmaster.data.repository

import android.content.Context
import android.util.Log
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.StudyResultsList
import com.example.ezwordmaster.model.StudyStats
import com.example.ezwordmaster.model.TodayProgress
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class StudyResultRepositoryImpl(private val context: Context) : IStudyResultRepository {

    private val FILE_NAME = "study_results.json"
    private val MAX_RECORDS = 44 // Giới hạn tối đa 44 bản ghi
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // Bỏ qua các field không tồn tại (startTime, endTime cũ)
    }

    // Đường dẫn tới file study_results.json trong thư mục riêng của app
    private fun getStudyResultsFile(): File = File(context.filesDir, FILE_NAME)

    // Kiểm tra file có tồn tại không
    override fun isStudyResultsFileExists(): Boolean {
        val exists = getStudyResultsFile().exists()
        Log.d("StudyResultRepo", "File tồn tại: $exists")
        return exists
    }

    // Tạo file mặc định nếu chưa có
    override fun createStudyResultsFileIfMissing() {
        val file = getStudyResultsFile()
        if (!file.exists()) {
            val emptyResults = StudyResultsList(results = emptyList())
            saveStudyResults(emptyResults)
            Log.d("StudyResultRepo", "Đã tạo file study_results.json mặc định")
        }
    }

    // Đọc dữ liệu từ file
    override fun loadStudyResults(): StudyResultsList {
        createStudyResultsFileIfMissing()
        val file = getStudyResultsFile()

        return try {
            val jsonString = file.readText()
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi đọc file: ${e.message}")
            // Nếu file bị lỗi format, tạo file mới
            StudyResultsList(results = emptyList())
        }
    }

    // Ghi đè toàn bộ danh sách
    private fun saveStudyResults(studyResults: StudyResultsList) {
        try {
            val jsonString = json.encodeToString(studyResults)
            getStudyResultsFile().writeText(jsonString)
            Log.d(
                "StudyResultRepo",
                "Đã lưu ${studyResults.results.size} kết quả học tập vào file."
            )
        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi khi ghi file: ${e.message}")
        }
    }

    // Thêm một kết quả học tập mới (với giới hạn 44 records)
    override fun addStudyResult(newResult: StudyResult) {
        val currentResults = loadStudyResults()
        var updatedList = currentResults.results + newResult

        // Nếu vượt quá 44 bản ghi, xóa bản ghi cũ nhất
        if (updatedList.size > MAX_RECORDS) {
            // Sắp xếp theo ngày (mới nhất trước)
            updatedList = updatedList.sortedByDescending { parseDate(it.day) }
            // Chỉ giữ lại 44 bản ghi mới nhất
            updatedList = updatedList.take(MAX_RECORDS)
            Log.d(
                "StudyResultRepo",
                "Đã xóa ${updatedList.size - MAX_RECORDS} bản ghi cũ nhất để giữ giới hạn $MAX_RECORDS"
            )
        }

        saveStudyResults(StudyResultsList(results = updatedList))
        Log.d(
            "StudyResultRepo",
            "Đã thêm kết quả học tập: ${newResult.studyMode} - ${newResult.topicName}"
        )
    }

    // ================= LẤY =====================
    // Lấy danh sách kết quả theo chế độ học
    override fun getStudyResultsByMode(studyMode: String): List<StudyResult> {
        val allResults = loadStudyResults()
        return allResults.results.filter { it.studyMode == studyMode }
    }

    // Lấy danh sách kết quả theo chủ đề
    override fun getStudyResultsByTopic(topicId: String): List<StudyResult> {
        val allResults = loadStudyResults()
        return allResults.results.filter { it.topicId == topicId }
    }

    // Lấy danh sách kết quả sắp xếp theo thời gian (mới nhất trước)
    override fun getStudyResultsSortedByTime(): List<StudyResult> {
        val allResults = loadStudyResults()
        return allResults.results.sortedByDescending { parseDate(it.day) }
    }

    // Lấy thống kê tổng quan
    override fun getStudyStats(): StudyStats {
        val allResults = loadStudyResults()
        val results = allResults.results

        val totalSessions = results.size
        val totalStudyTime = results.sumOf { it.duration }
        val flashcardResults = results.filter { it.studyMode == "flashcard" }
        val flipcardResults = results.filter { it.studyMode == "flipcard" }

        val totalWordsLearned = flashcardResults.sumOf { it.knownWords ?: 0 }
        val averageAccuracy = if (flashcardResults.isNotEmpty()) {
            flashcardResults.mapNotNull { it.accuracy }.average().toFloat()
        } else 0f

        val averageCompletionRate = if (flipcardResults.isNotEmpty()) {
            flipcardResults.mapNotNull { it.completionRate }.average().toFloat()
        } else 0f

        return StudyStats(
            totalSessions = totalSessions,
            totalStudyTime = totalStudyTime,
            totalWordsLearned = totalWordsLearned,
            averageAccuracy = averageAccuracy,
            averageCompletionRate = averageCompletionRate
        )
    }

    // Xóa tất cả kết quả (dùng cho testing)
    override fun clearAllResults() {
        saveStudyResults(StudyResultsList(results = emptyList()))
        Log.d("StudyResultRepo", "Đã xóa tất cả kết quả học tập")
    }

    /**
     * Parse date string (dd/MM/yyyy) thành timestamp để so sánh
     */
    private fun parseDate(dateString: String): Long {
        return try {
            val parts = dateString.split("/")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt() - 1 // Calendar month is 0-indexed
                val year = parts[2].toInt()

                val calendar = java.util.Calendar.getInstance()
                calendar.set(year, month, day, 0, 0, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            } else {
                0L
            }
        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi parse date: $dateString - ${e.message}")
            0L
        }
    }

    /**
     * Lấy tất cả kết quả ôn tập trong ngày hôm nay
     * Nếu nhiều lần ôn tập trong ngày thì cộng số từ vựng lại
     */
    override fun getTodayStudyProgress(topicId: String): TodayProgress {
        val allResults = loadStudyResults()
        val today = getCurrentDay()

        // Lọc kết quả của ngày hôm nay VÀ theo đúng topicId
        val todayTopicResults =
            allResults.results.filter { it.day == today && it.topicId == topicId }

        // Tính tổng số từ đã ôn tập trong ngày
        val totalKnownWords = todayTopicResults
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.knownWords ?: 0 }

        val totalWords = todayTopicResults
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.totalWords ?: 0 }

        val totalSessions = todayTopicResults.size

        return TodayProgress(
            day = today,
            totalSessions = totalSessions,
            totalKnownWords = totalKnownWords,
            totalWords = totalWords,
            results = todayTopicResults
        )
    }

    /**
    //     * Lấy ngày hiện tại (dd/MM/yyyy)
    //     */
    private fun getCurrentDay(): String {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }

    /**
     *  Lấy tổng tiến trình của TẤT CẢ các chủ đề trong ngày
     */
    override fun getOverallTodayProgress(): TodayProgress {
        val allResults = loadStudyResults()
        val today = getCurrentDay()

        // Chỉ lọc theo ngày, không lọc theo topicId
        val todayAllResults = allResults.results.filter { it.day == today }

        val totalKnownWords = todayAllResults
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.knownWords ?: 0 }

        val totalWords = todayAllResults
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.totalWords ?: 0 }

        val totalSessions = todayAllResults.size

        return TodayProgress(
            day = today,
            totalSessions = totalSessions,
            totalKnownWords = totalKnownWords,
            totalWords = totalWords,
            results = todayAllResults
        )
    }
}

