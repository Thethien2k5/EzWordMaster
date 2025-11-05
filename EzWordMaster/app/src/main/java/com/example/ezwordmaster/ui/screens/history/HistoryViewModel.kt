package com.example.ezwordmaster.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.TodayProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý màn hình lịch sử ôn tập
 */
class HistoryViewModel(
    private val studyResultRepository: IStudyResultRepository
) : ViewModel() {

    private val _studyResults = MutableStateFlow<List<StudyResult>>(emptyList())
    val studyResults: StateFlow<List<StudyResult>> = _studyResults.asStateFlow()

    private val _todayProgress = MutableStateFlow<TodayProgress?>(null)
    val todayProgress: StateFlow<TodayProgress?> = _todayProgress.asStateFlow()

    /**
     * Load lịch sử ôn tập (tối đa 44 records, sắp xếp mới nhất trước)
     */
    fun loadStudyHistory() {
        viewModelScope.launch {
            val results = studyResultRepository.getStudyResultsSortedByTime()
            _studyResults.value = results
        }
    }

    /**
     * Load tiến trình ôn tập hôm nay
     * (Cộng tổng số từ đã nhớ nếu có nhiều lần ôn tập trong ngày)
     */
    fun loadTodayProgress() {
        viewModelScope.launch {
            // Gọi hàm mới không cần topicId
            _todayProgress.value = studyResultRepository.getOverallTodayProgress()
        }
    }

    /**
     * Xóa toàn bộ lịch sử ôn tập
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            studyResultRepository.clearAllResults()
            loadStudyHistory()
            loadTodayProgress()
        }
    }
}