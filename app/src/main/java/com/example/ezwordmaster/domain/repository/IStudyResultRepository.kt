package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.StudyResultsList
import com.example.ezwordmaster.model.StudyStats
import com.example.ezwordmaster.model.TodayProgress

/**
 * Interface cho StudyResultRepository.
 */
interface IStudyResultRepository {

    /**
     * Kiểm tra xem file lưu kết quả có tồn tại hay không.
     */
    fun isStudyResultsFileExists(): Boolean

    /**
     * Tạo file lưu kết quả mặc định nếu nó chưa tồn tại.
     */
    fun createStudyResultsFileIfMissing()

    /**
     * Tải tất cả kết quả học tập từ nguồn dữ liệu.
     */
    fun loadStudyResults(): StudyResultsList

    /**
     * Thêm một kết quả học tập mới vào nguồn dữ liệu.
     */
    fun addStudyResult(newResult: StudyResult)

    /**
     * Lấy danh sách kết quả học tập theo một chế độ học cụ thể.
     */
    fun getStudyResultsByMode(studyMode: String): List<StudyResult>

    /**
     * Lấy danh sách kết quả học tập theo một chủ đề cụ thể.
     */
    fun getStudyResultsByTopic(topicId: String): List<StudyResult>

    /**
     * Lấy danh sách kết quả học tập được sắp xếp theo thời gian (mới nhất trước).
     */
    fun getStudyResultsSortedByTime(): List<StudyResult>

    /**
     * Tính toán và lấy các số liệu thống kê tổng quan về quá trình học.
     */
    fun getStudyStats(): StudyStats

    /**
     * Xóa tất cả các kết quả học tập khỏi nguồn dữ liệu.
     */
    fun clearAllResults()

    /**
     * Lấy tất cả kết quả ôn tập trong ngày hôm nay
     * Nếu nhiều lần ôn tập trong ngày thì cộng số từ vựng lại
     */
    fun getTodayStudyProgress(topicId: String): TodayProgress

    /**
     * THÊM VÀO: Lấy tổng tiến trình học của TẤT CẢ chủ đề trong ngày
     */
    fun getOverallTodayProgress(): TodayProgress
}