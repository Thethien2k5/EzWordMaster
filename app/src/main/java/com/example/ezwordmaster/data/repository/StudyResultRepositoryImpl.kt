package com.example.ezwordmaster.data.repository

import java.io.File
import android.util.Log
import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.ExperimentalSerializationApi
import com.example.ezwordmaster.model.StudyStats
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.StudyResultsList
import com.example.ezwordmaster.domain.repository.IStudyResultRepository

@OptIn(ExperimentalSerializationApi::class)
class StudyResultRepositoryImpl(private val context: Context): IStudyResultRepository {

    private val FILE_NAME = "study_results.json"
    private val json = Json { prettyPrint = true }

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
  override  fun loadStudyResults(): StudyResultsList {
        createStudyResultsFileIfMissing()
        val file = getStudyResultsFile()

        return try {
            val jsonString = file.readText()
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi đọc file: ${e.message}")
            StudyResultsList(results = emptyList())
        }
    }

    // Ghi đè toàn bộ danh sách
    private fun saveStudyResults(studyResults: StudyResultsList) {
        try {
            val jsonString = json.encodeToString(studyResults)
            getStudyResultsFile().writeText(jsonString)
            Log.d("StudyResultRepo", "Đã lưu ${studyResults.results.size} kết quả học tập vào file.")
        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi khi ghi file: ${e.message}")
        }
    }

    // Thêm một kết quả học tập mới
   override fun addStudyResult(newResult: StudyResult) {
        val currentResults = loadStudyResults()
        val updatedResults = currentResults.results + newResult
        saveStudyResults(StudyResultsList(results = updatedResults))
        Log.d("StudyResultRepo", "Đã thêm kết quả học tập: ${newResult.studyMode} - ${newResult.topicName}")
    }

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
        return allResults.results.sortedByDescending { it.endTime }
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
}




