package com.example.ezwordmaster.ui.screens.regime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.R
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.LatestStudyInfo
import com.example.ezwordmaster.model.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * ViewModel quản lý màn hình kết quả (ResultScreen)
 */
class ResultViewModel(
    private val TOPICREPOSITORY: ITopicRepository,
    private val STUDYRESULTREPOSITORY: IStudyResultRepository
) : ViewModel() {

    private val _SELECTEDTOPIC = MutableStateFlow<Topic?>(null)
    val selectedTopic: StateFlow<Topic?> = _SELECTEDTOPIC.asStateFlow()

    private val _LATESTSTUDYINFO = MutableStateFlow<LatestStudyInfo?>(null)
    val latestStudyInfo: StateFlow<LatestStudyInfo?> = _LATESTSTUDYINFO.asStateFlow()

    /**
     * Tải chủ đề theo ID
     */
    fun loadTopicById(topicId: String) {
        viewModelScope.launch {
            _SELECTEDTOPIC.value = TOPICREPOSITORY.getTopicById(topicId)
        }
    }

    /**
     * Lấy thông tin bài ôn tập trong ngày hiện tại của một chủ đề
     * Nếu có nhiều lần ôn tập trong ngày thì cộng số từ vựng lại
     */
    fun getLatestStudyInfo(topicId: String) {
        viewModelScope.launch {
            val todayProgress = STUDYRESULTREPOSITORY.getTodayStudyProgress(topicId)

            _LATESTSTUDYINFO.value = LatestStudyInfo(
                knownWords = todayProgress.totalKnownWords,
                totalWords = todayProgress.totalWords,
                day = todayProgress.day
            )
        }
    }

    /**
     * Tính phần trăm hoàn thành
     */
    fun calculateProgressPercentage(knownWords: Int, totalWords: Int): Int {
        return if (totalWords > 0) (knownWords * 100) / totalWords else 0
    }

    /**
     * Lấy thông điệp động dựa trên % hoàn thành
     */
    fun getMotivationalMessage(progressPercentage: Int): String {
        return when {
            progressPercentage > 70 -> "Chúc mừng bạn"
            progressPercentage in 50..70 -> "Một chút nữa thôi, cố lên"
            else -> "Cùng nhau cố gắng, tôi sẽ luôn ở bên bạn"
        }
    }

    /**
     * Lấy tên file GIF dựa trên % hoàn thành
     */
    fun getAnimationResId(progressPercentage: Int): Int {
        return when {
            progressPercentage > 70 -> R.drawable.chucmung
            progressPercentage in 50..70 -> R.drawable.colen
            else -> R.drawable.khongsao
        }
    }

    /**
     * Lấy ngày hiện tại ở định dạng dd/MM/yyyy
     */
    fun getCurrentDay(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Format thời gian từ milliseconds sang phút:giây
     */
    fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}