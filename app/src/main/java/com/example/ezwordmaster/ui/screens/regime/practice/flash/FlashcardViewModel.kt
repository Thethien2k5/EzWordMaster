package com.example.ezwordmaster.ui.screens.regime.practice.flash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.FlashcardUiState
import com.example.ezwordmaster.model.StudyResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class FlashcardViewModel(
    private val topicRepository: ITopicRepository,
    private val studyResultRepository: IStudyResultRepository
) : ViewModel() {

    // StateFlow để lưu trữ trạng thái giao diện người dùng
    private val _UISTATE = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _UISTATE.asStateFlow()

    // Hàm tải chủ đề dựa trên topicId
    fun loadTopic(topicId: String) {
        viewModelScope.launch {
            val TOPIC = topicRepository.getTopicById(topicId)
            _UISTATE.value = FlashcardUiState(
                topic = TOPIC,
                words = TOPIC?.words?.shuffled() ?: emptyList(),
                startTime = System.currentTimeMillis()
            )
        }
    }

    // Hàm lật thẻ
    fun flipCard() {
        if (_UISTATE.value.isProcessing) return
        _UISTATE.value = _UISTATE.value.copy(isFlipped = !_UISTATE.value.isFlipped)
    }

    // Hàm xử lý khi vuốt (đánh dấu từ đã biết hoặc đang học)
    fun onSwipe(isKnown: Boolean) {
        viewModelScope.launch {
            if (_UISTATE.value.isProcessing || _UISTATE.value.isCompleted) return@launch
            _UISTATE.value = _UISTATE.value.copy(isProcessing = true)

            try {
                val CURRENTSTATE = _UISTATE.value
                val NEWKNOWN = if (isKnown) CURRENTSTATE.knownWords + 1 else CURRENTSTATE.knownWords
                val NEWLEARNING =
                    if (!isKnown) CURRENTSTATE.learningWords + 1 else CURRENTSTATE.learningWords
                _UISTATE.value =
                    _UISTATE.value.copy(knownWords = NEWKNOWN, learningWords = NEWLEARNING)

                delay(350)

                val NEXTINDEX = CURRENTSTATE.currentIndex + 1
                if (NEXTINDEX >= CURRENTSTATE.words.size) {
                    completeQuiz()
                } else {
                    _UISTATE.value = _UISTATE.value.copy(
                        currentIndex = NEXTINDEX,
                        isFlipped = false
                    )
                }
            } finally {
                _UISTATE.value = _UISTATE.value.copy(isProcessing = false)
            }
        }
    }

    // Hàm chuyển sang câu hỏi tiếp theo
    fun nextQuestion(isSwipe: Boolean = false) {
        viewModelScope.launch {
            if (_UISTATE.value.isProcessing && !isSwipe) return@launch // Khóa nút bấm nếu đang vuốt
            _UISTATE.value = _UISTATE.value.copy(isProcessing = true)

            try {
                if (isSwipe) {
                    delay(300)
                }

                val CURRENTSTATE = _UISTATE.value
                val NEXTINDEX = CURRENTSTATE.currentIndex + 1

                if (NEXTINDEX >= CURRENTSTATE.words.size) {
                    completeQuiz()
                } else {
                    _UISTATE.value = CURRENTSTATE.copy(
                        currentIndex = NEXTINDEX,
                        isFlipped = false
                    )
                }
            } finally {
                delay(400)
                _UISTATE.value = _UISTATE.value.copy(isProcessing = false)
            }
        }
    }

    // Hàm quay lại câu hỏi trước đó
    fun previousQuestion() {
        if (_UISTATE.value.isProcessing) return
        val CURRENTSTATE = _UISTATE.value
        if (CURRENTSTATE.currentIndex > 0) {
            _UISTATE.value = CURRENTSTATE.copy(
                currentIndex = CURRENTSTATE.currentIndex - 1,
                isFlipped = false
            )
        }
    }

    // Hàm chuyển sang từ tiếp theo
    fun goToNextWord() {
        val CURRENTSTATE = _UISTATE.value
        val NEXTINDEX = CURRENTSTATE.currentIndex + 1
        if (NEXTINDEX < CURRENTSTATE.words.size) {
            _UISTATE.value = CURRENTSTATE.copy(
                currentIndex = NEXTINDEX,
                isFlipped = false
            )
        }
    }

    // Hàm quay lại từ trước đó
    fun goToPreviousWord() {
        val CURRENTSTATE = _UISTATE.value
        val PREVINDEX = CURRENTSTATE.currentIndex - 1
        if (PREVINDEX >= 0) {
            _UISTATE.value = CURRENTSTATE.copy(
                currentIndex = PREVINDEX,
                isFlipped = false
            )
        }
    }

    // Hàm hoàn thành bài kiểm tra
    private fun completeQuiz() {
        val STATE = _UISTATE.value
        if (STATE.isCompleted) return // Tránh gọi nhiều lần

        val TOPIC = STATE.topic ?: return
        val DURATION_MS = System.currentTimeMillis() - STATE.startTime
        val STUDYRESULT = StudyResult.createFlashcardResult(
            id = UUID.randomUUID().toString(),
            topicId = TOPIC.id ?: "Lỗi không id FlashcardViewModel.kt",
            topicName = TOPIC.name ?: "Chủ đề không tên",
            duration = DURATION_MS,
            totalWords = STATE.words.size,
            knownWords = STATE.knownWords,
            learningWords = STATE.learningWords
        )
        studyResultRepository.addStudyResult(STUDYRESULT)
        _UISTATE.value = _UISTATE.value.copy(isCompleted = true)
    }
}