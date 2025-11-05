package com.example.ezwordmaster.data.repository

import android.content.Context
import android.util.Log
import com.example.ezwordmaster.data.local.dao.StudyResultDao
import com.example.ezwordmaster.data.local.database.EzWordMasterDatabase
import com.example.ezwordmaster.data.local.mapper.StudyResultMapper
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.StudyResultsList
import com.example.ezwordmaster.model.StudyStats
import com.example.ezwordmaster.model.TodayProgress
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudyResultRepositoryImpl(private val context: Context) : IStudyResultRepository {

    private val database = EzWordMasterDatabase.getDatabase(context)
    private val studyResultDao: StudyResultDao = database.studyResultDao()

    private val FILE_NAME = "study_results.json" // Để check file cũ nếu có
    private val MAX_RECORDS = 44 // Giới hạn tối đa 44 bản ghi

    // Kiểm tra file JSON cũ có tồn tại không
    override fun isStudyResultsFileExists(): Boolean {
        val file = File(context.filesDir, FILE_NAME)
        return file.exists()
    }

    // Tạo file mặc định nếu chưa có
    override suspend fun createStudyResultsFileIfMissing() {
        val count = studyResultDao.getResultCount()
        
        if (count == 0) {
            // Kiểm tra xem có file JSON cũ không, nếu có thì migrate
            val jsonFile = File(context.filesDir, FILE_NAME)
            if (jsonFile.exists()) {
                migrateFromJson(jsonFile)
            }
            // Nếu không có file cũ, database sẽ rỗng (đúng)
        }
    }

    // Migrate từ JSON cũ sang Room
    private suspend fun migrateFromJson(jsonFile: File) {
        try {
            Log.d("StudyResultRepo", "Bắt đầu migration từ JSON sang Room")
            val jsonString = jsonFile.readText()
            val json = kotlinx.serialization.json.Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }
            val studyResultsList: StudyResultsList = json.decodeFromString(jsonString)
            
            val entities = studyResultsList.results.map { result ->
                StudyResultMapper.toEntity(result)
            }
            studyResultDao.insertResults(entities)
            
            Log.d("StudyResultRepo", "Đã migrate ${entities.size} kết quả từ JSON sang Room")
            
            // Optionally: Backup file cũ hoặc xóa
            // jsonFile.delete()
        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi migration từ JSON: ${e.message}")
        }
    }

    // Đọc dữ liệu từ Room Database
    override suspend fun loadStudyResults(): StudyResultsList {
        createStudyResultsFileIfMissing()
        
        val entities = studyResultDao.getAllResults().first()
        val results = entities.map { entity ->
            StudyResultMapper.toDomain(entity)
        }
        
        return StudyResultsList(results = results)
    }

    // Thêm một kết quả học tập mới (với giới hạn 44 records)
    override suspend fun addStudyResult(newResult: StudyResult) {
        val currentCount = studyResultDao.getResultCount()
        
        // Nếu vượt quá 44 bản ghi, xóa bản ghi cũ nhất
        if (currentCount >= MAX_RECORDS) {
            val allResults = studyResultDao.getAllResults().first()
            val sortedResults = allResults.sortedByDescending { parseDate(it.day) }
            
            // Chỉ giữ lại 43 bản ghi mới nhất (để sau khi thêm mới sẽ là 44)
            val toKeep = sortedResults.take(MAX_RECORDS - 1)
            val toDelete = sortedResults.drop(MAX_RECORDS - 1)
            
            // Xóa các bản ghi cũ
            for (result in toDelete) {
                studyResultDao.deleteResultById(result.id)
            }
            
            Log.d("StudyResultRepo", "Đã xóa ${toDelete.size} bản ghi cũ nhất để giữ giới hạn $MAX_RECORDS")
        }
        
        val entity = StudyResultMapper.toEntity(newResult)
        studyResultDao.insertResult(entity)
        
        Log.d("StudyResultRepo", "Đã thêm kết quả học tập: ${newResult.studyMode} - ${newResult.topicName}")
    }

    // Lấy danh sách kết quả theo chế độ học
    override suspend fun getStudyResultsByMode(studyMode: String): List<StudyResult> {
        val entities = studyResultDao.getResultsByMode(studyMode)
        return entities.map { entity ->
            StudyResultMapper.toDomain(entity)
        }
    }

    // Lấy danh sách kết quả theo chủ đề
    override suspend fun getStudyResultsByTopic(topicId: String): List<StudyResult> {
        val entities = studyResultDao.getResultsByTopic(topicId)
        return entities.map { entity ->
            StudyResultMapper.toDomain(entity)
        }
    }

    // Lấy danh sách kết quả sắp xếp theo thời gian (mới nhất trước)
    override suspend fun getStudyResultsSortedByTime(): List<StudyResult> {
        val entities = studyResultDao.getAllResults().first()
        val sorted = entities.sortedByDescending { parseDate(it.day) }
        return sorted.map { entity ->
            StudyResultMapper.toDomain(entity)
        }
    }

    // Lấy thống kê tổng quan
    override suspend fun getStudyStats(): StudyStats {
        val allEntities = studyResultDao.getAllResults().first()
        val results = allEntities.map { StudyResultMapper.toDomain(it) }

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
    override suspend fun clearAllResults() {
        studyResultDao.deleteAllResults()
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
    override suspend fun getTodayStudyProgress(topicId: String): TodayProgress {
        val today = getCurrentDay()
        val entities = studyResultDao.getResultsByDayAndTopic(today, topicId)
        val results = entities.map { StudyResultMapper.toDomain(it) }

        // Tính tổng số từ đã ôn tập trong ngày
        val totalKnownWords = results
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.knownWords ?: 0 }

        val totalWords = results
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.totalWords ?: 0 }

        val totalSessions = results.size

        return TodayProgress(
            day = today,
            totalSessions = totalSessions,
            totalKnownWords = totalKnownWords,
            totalWords = totalWords,
            results = results
        )
    }

    /**
     * Lấy ngày hiện tại (dd/MM/yyyy)
     */
    private fun getCurrentDay(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Lấy tổng tiến trình của TẤT CẢ các chủ đề trong ngày
     */
    override suspend fun getOverallTodayProgress(): TodayProgress {
        val today = getCurrentDay()
        val entities = studyResultDao.getResultsByDay(today)
        val results = entities.map { StudyResultMapper.toDomain(it) }

        val totalKnownWords = results
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.knownWords ?: 0 }

        val totalWords = results
            .filter { it.studyMode == "flashcard" }
            .sumOf { it.totalWords ?: 0 }

        val totalSessions = results.size

        return TodayProgress(
            day = today,
            totalSessions = totalSessions,
            totalKnownWords = totalKnownWords,
            totalWords = totalWords,
            results = results
        )
    }
}
