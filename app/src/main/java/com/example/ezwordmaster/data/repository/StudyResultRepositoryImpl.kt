package com.example.ezwordmaster.data.local.repository

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository triển khai cho việc quản lý kết quả học tập (StudyResult).
 * Sử dụng Room Database làm nguồn dữ liệu chính.
 * Hỗ trợ tự động migrate một lần từ file JSON cũ nếu tồn tại.
 *
 * @param context Context của ứng dụng.
 * @property studyResultDao DAO để tương tác với CSDL Room.
 * @property ioDispatcher Sử dụng Dispatchers.IO cho mọi thao tác I/O (Database, File).
 */
@OptIn(ExperimentalSerializationApi::class)
class StudyResultRepositoryImpl(private val context: Context) : IStudyResultRepository {

    // Tên file JSON cũ để kiểm tra và migrate
    private val oldJsonFileName = "study_results.json"
    private val maxRecords = 44 // Giới hạn tối đa 44 bản ghi

    private val database = EzWordMasterDatabase.getDatabase(context)
    private val studyResultDao: StudyResultDao = database.studyResultDao()

    // Luôn sử dụng Dispatchers.IO cho các tác vụ I/O của repository
    private val ioDispatcher = Dispatchers.IO

    override fun isStudyResultsFileExists(): Boolean {
        val file = File(context.filesDir, oldJsonFileName)
        return file.exists()
    }

    /**
     * Khởi tạo CSDL.
     * Kiểm tra xem CSDL có rỗng không. Nếu rỗng, thử tìm file JSON cũ để migrate dữ liệu.
     * Hàm này nên được gọi một lần khi ứng dụng khởi động (ví dụ: trong ViewModel).
     */
    override suspend fun createStudyResultsFileIfMissing() {
        val count = studyResultDao.getResultCount()

        if (count == 0) {
            // Kiểm tra xem có file JSON cũ không, nếu có thì migrate
            val jsonFile = File(context.filesDir, oldJsonFileName)
            if (jsonFile.exists()) {
                migrateFromJson(jsonFile)
            }
            // Nếu không có file cũ, database sẽ rỗng (đúng)
        }
    }

    /**
     * Helper: Di chuyển dữ liệu từ file JSON cũ sang Room DB.
     * Chỉ chạy một lần khi CSDL rỗng và file cũ tồn tại.
     */
    private suspend fun migrateFromJson(jsonFile: File) {
        try {
            val jsonString = jsonFile.readText()
            val json = Json {
                prettyPrint = true
                ignoreUnknownKeys = true // Rất quan trọng để bỏ qua field cũ không dùng nữa
            }

            val studyResultsList: StudyResultsList = json.decodeFromString(jsonString)
            val entities = studyResultsList.results.map { StudyResultMapper.toEntity(it) }

            studyResultDao.insertResults(entities)

            Log.d("StudyResultRepo", "Đã migrate ${entities.size} kết quả từ JSON sang Room")

        } catch (e: Exception) {
            Log.e("StudyResultRepo", "Lỗi nghiêm trọng khi migration từ JSON: ${e.message}", e)
        }
    }

    /**
     * Tải tất cả kết quả học tập từ CSDL.
     */
    override suspend fun loadStudyResults(): StudyResultsList = withContext(ioDispatcher) {
        val entities = studyResultDao.getAllResults().first()
        val results = entities.map { StudyResultMapper.toDomain(it) }
        StudyResultsList(results = results)
    }

    /**
     * Thêm một kết quả học tập mới vào CSDL.
     * Đồng thời kiểm tra và xóa bản ghi cũ nhất nếu vượt quá maxRecords.
     */
    override suspend fun addStudyResult(newResult: StudyResult) = withContext(ioDispatcher) {
        val currentCount = studyResultDao.getResultCount()

        if (currentCount >= maxRecords) {
            val allResults = studyResultDao.getAllResults().first()
            val sortedResults = allResults.sortedByDescending { parseDate(it.day) }

            // Chỉ giữ lại 43 bản ghi mới nhất (để sau khi thêm mới sẽ là 44)
            sortedResults.take(maxRecords - 1)
            val toDelete = sortedResults.drop(maxRecords - 1)

            // Xóa các bản ghi cũ
            for (result in toDelete) {
                studyResultDao.deleteResultById(result.id)
            }

            Log.d(
                "StudyResultRepo",
                "Đã xóa ${toDelete.size} bản ghi cũ nhất để giữ giới hạn $maxRecords"
            )
        }
    }

    /**
     * Xóa tất cả kết quả học tập khỏi CSDL.
     */
    override suspend fun clearAllResults() {
        studyResultDao.deleteAllResults()
        Log.d("StudyResultRepo", "Đã xóa tất cả kết quả học tập")
    }

    /**
     * Lấy danh sách kết quả theo chế độ học (studyMode).
     */
    override suspend fun getStudyResultsByMode(studyMode: String): List<StudyResult> =
        withContext(ioDispatcher) {
            val entities = studyResultDao.getResultsByMode(studyMode)
            return@withContext entities.map { StudyResultMapper.toDomain(it) }
        }

    /**
     * Lấy danh sách kết quả theo chủ đề (topicId).
     */
    override suspend fun getStudyResultsByTopic(topicId: String): List<StudyResult> =
        withContext(ioDispatcher) {
            val entities = studyResultDao.getResultsByTopic(topicId)
            return@withContext entities.map { StudyResultMapper.toDomain(it) }
        }

    /**
     * Lấy danh sách kết quả sắp xếp theo thời gian (mới nhất trước).
     */
    override suspend fun getStudyResultsSortedByTime(): List<StudyResult> =
        withContext(ioDispatcher) {
            val entities = studyResultDao.getAllResults().first()
            val sorted = entities.sortedByDescending { parseDate(it.day) }
            return@withContext sorted.map { StudyResultMapper.toDomain(it) }
        }

    /**
     * Lấy thống kê tổng quan (tổng số phiên, thời gian, từ...).
     */
    override suspend fun getStudyStats(): StudyStats = withContext(ioDispatcher) {
        val allEntities = studyResultDao.getAllResults().first()
        val results = allEntities.map { StudyResultMapper.toDomain(it) }

        val totalSessions = results.size
        val totalStudyTime = results.sumOf { it.duration }
        val (totalKnownWords, totalWords) = calculateFlashcardStats(results)

        return@withContext StudyStats(
            totalSessions = totalSessions,
            totalStudyTime = totalStudyTime,
            totalKnownWords = totalKnownWords,
            totalWords = totalWords
        )
    }

    /**
     * Lấy tiến trình học tập của một chủ đề (topicId) cụ thể TRONG HÔM NAY.
     */
    override suspend fun getTodayStudyProgress(topicId: String): TodayProgress =
        withContext(ioDispatcher) {
            val today = getCurrentDay()
            val entities = studyResultDao.getResultsByDayAndTopic(today, topicId)
            val results = entities.map { StudyResultMapper.toDomain(it) }

            val (totalKnownWords, totalWords) = calculateFlashcardStats(results)
            val totalSessions = results.size

            return@withContext TodayProgress(
                day = today,
                totalSessions = totalSessions,
                totalKnownWords = totalKnownWords,
                totalWords = totalWords,
                results = results
            )
        }

    /**
     * Lấy tổng tiến trình học tập của TẤT CẢ chủ đề TRONG HÔM NAY.
     */
    override suspend fun getOverallTodayProgress(): TodayProgress = withContext(ioDispatcher) {
        val today = getCurrentDay()
        val entities = studyResultDao.getResultsByDay(today)
        val results = entities.map { StudyResultMapper.toDomain(it) }

        val (totalKnownWords, totalWords) = calculateFlashcardStats(results)
        val totalSessions = results.size

        return@withContext TodayProgress(
            day = today,
            totalSessions = totalSessions,
            totalKnownWords = totalKnownWords,
            totalWords = totalWords,
            results = results
        )
    }

    // ================= HELPER FUNCTIONS =====================

    /**
     * Helper: Tính tổng số từ (known/total) từ danh sách kết quả (chỉ tính mode 'flashcard').
     */
    private fun calculateFlashcardStats(results: List<StudyResult>): Pair<Int, Int> {
        val flashcardResults = results.filter { it.studyMode == "flashcard" }
        val totalKnownWords = flashcardResults.sumOf { it.knownWords ?: 0 }
        val totalWords = flashcardResults.sumOf { it.totalWords ?: 0 }
        return Pair(totalKnownWords, totalWords)
    }

    /**
     * Helper: Lấy ngày hiện tại (dd/MM/yyyy).
     */
    private fun getCurrentDay(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Helper: Chuyển đổi chuỗi ngày (dd/MM/yyyy) sang đối tượng Date để so sánh/sắp xếp.
     */
    private fun parseDate(day: String): Date {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.parse(day) ?: Date(0)
        } catch (e: Exception) {
            Log.w("StudyResultRepo", "Lỗi parse ngày: $day", e)
            Date(0)
        }
    }
}