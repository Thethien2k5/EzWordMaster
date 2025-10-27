package com.example.ezwordmaster.ui.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.FlashCardUiState
import com.example.ezwordmaster.model.StudyResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class FlashcardViewModel(
    private val TOPICREPOSITORY: ITopicRepository,
    private val STUDYRESULTREPOSITORY: IStudyResultRepository
) : ViewModel() {

    private val _UISTATE = MutableStateFlow(FlashCardUiState())
    val UISTATE: StateFlow<FlashCardUiState> = _UISTATE.asStateFlow()


    fun loadTopic(topicId: String) {
        viewModelScope.launch {
            val TOPIC = TOPICREPOSITORY.getTopicById(topicId)
            _UISTATE.value = FlashCardUiState(
                TOPIC = TOPIC,
                WORDS = TOPIC?.words?.shuffled() ?: emptyList(),
                STARTTIME = System.currentTimeMillis()
            )
        }
    }

    fun flipCard() {
        if (_UISTATE.value.IS_PROCESSING) return
        _UISTATE.value = _UISTATE.value.copy(ISFLIPPED = !_UISTATE.value.ISFLIPPED)
    }

    fun onSwipe(isKnown: Boolean) {
        viewModelScope.launch {
            if (_UISTATE.value.IS_PROCESSING || _UISTATE.value.ISCOMPLETED) return@launch
            _UISTATE.value = _UISTATE.value.copy(IS_PROCESSING = true)

            try {
                val CURRENTSTATE = _UISTATE.value
                val NEWKNOWN = if (isKnown) CURRENTSTATE.KNOWNWORDS + 1 else CURRENTSTATE.KNOWNWORDS
                val NEWLEARNING =
                    if (!isKnown) CURRENTSTATE.LEARNINGWORDS + 1 else CURRENTSTATE.LEARNINGWORDS
                _UISTATE.value =
                    _UISTATE.value.copy(KNOWNWORDS = NEWKNOWN, LEARNINGWORDS = NEWLEARNING)

                delay(350)

                val NEXTINDEX = CURRENTSTATE.CURRENTINDEX + 1
                if (NEXTINDEX >= CURRENTSTATE.WORDS.size) {
                    completeQuiz()
                } else {
                    _UISTATE.value = _UISTATE.value.copy(
                        CURRENTINDEX = NEXTINDEX,
                        ISFLIPPED = false
                    )
                }
            } finally {
                _UISTATE.value = _UISTATE.value.copy(IS_PROCESSING = false)
            }
        }
    }

    fun nextQuestion(isSwipe: Boolean = false) {
        viewModelScope.launch {
            if (_UISTATE.value.IS_PROCESSING && !isSwipe) return@launch // Khóa nút bấm nếu đang vuốt
            _UISTATE.value = _UISTATE.value.copy(IS_PROCESSING = true)

            try {
                if (isSwipe) {
                    delay(300)
                }

                val CURRENTSTATE = _UISTATE.value
                val NEXTINDEX = CURRENTSTATE.CURRENTINDEX + 1

                if (NEXTINDEX >= CURRENTSTATE.WORDS.size) {
                    completeQuiz()
                } else {
                    _UISTATE.value = CURRENTSTATE.copy(
                        CURRENTINDEX = NEXTINDEX,
                        ISFLIPPED = false
                    )
                }
            } finally {
                delay(400)
                _UISTATE.value = _UISTATE.value.copy(IS_PROCESSING = false)
            }
        }
    }

    fun previousQuestion() {
        if (_UISTATE.value.IS_PROCESSING) return
        val CURRENTSTATE = _UISTATE.value
        if (CURRENTSTATE.CURRENTINDEX > 0) {
            _UISTATE.value = CURRENTSTATE.copy(
                CURRENTINDEX = CURRENTSTATE.CURRENTINDEX - 1,
                ISFLIPPED = false
            )
        }
    }

    fun goToNextWord() {
        val CURRENTSTATE = _UISTATE.value
        val NEXTINDEX = CURRENTSTATE.CURRENTINDEX + 1
        if (NEXTINDEX < CURRENTSTATE.WORDS.size) {
            _UISTATE.value = CURRENTSTATE.copy(
                CURRENTINDEX = NEXTINDEX,
                ISFLIPPED = false
            )
        }
    }

    fun goToPreviousWord() {
        val CURRENTSTATE = _UISTATE.value
        val PREVINDEX = CURRENTSTATE.CURRENTINDEX - 1
        if (PREVINDEX >= 0) {
            _UISTATE.value = CURRENTSTATE.copy(
                CURRENTINDEX = PREVINDEX,
                ISFLIPPED = false
            )
        }
    }

    private fun completeQuiz() {
        val STATE = _UISTATE.value
        if (STATE.ISCOMPLETED) return // Tránh gọi nhiều lần
        
        val TOPIC = STATE.TOPIC ?: return
        val DURATION_MS = System.currentTimeMillis() - STATE.STARTTIME
        val STUDYRESULT = StudyResult.createFlashcardResult(
            id = UUID.randomUUID().toString(),
            topicId = TOPIC.id ?: "Lỗi không id FlashcardViewModel.kt",
            topicName = TOPIC.name ?: "Chủ đề không tên",
            duration = DURATION_MS,
            totalWords = STATE.WORDS.size,
            knownWords = STATE.KNOWNWORDS,
            learningWords = STATE.LEARNINGWORDS
        )
        STUDYRESULTREPOSITORY.addStudyResult(STUDYRESULT)
        _UISTATE.value = _UISTATE.value.copy(ISCOMPLETED = true)
    }
}