package com.example.ezwordmaster.ui.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.StudyResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.ezwordmaster.model.FlashCardUiState


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
        _UISTATE.value = _UISTATE.value.copy(ISFLIPPED = !_UISTATE.value.ISFLIPPED)
    }

    fun onSwipe(isKnown: Boolean) {
        viewModelScope.launch {
            val CURRENTSTATE = _UISTATE.value
            val NEWKNOWN = if (isKnown) CURRENTSTATE.KNOWNWORDS + 1 else CURRENTSTATE.KNOWNWORDS
            val NEWLEARNING = if (!isKnown) CURRENTSTATE.LEARNINGWORDS + 1 else CURRENTSTATE.LEARNINGWORDS
            val NEXTINDEX = CURRENTSTATE.CURRENTINDEX + 1

            _UISTATE.value = CURRENTSTATE.copy(
                KNOWNWORDS = NEWKNOWN,
                LEARNINGWORDS = NEWLEARNING,
                ISFLIPPED = false // Reset thẻ lật
            )

            delay(300) // Đợi hiệu ứng vuốt hoàn thành

            if (NEXTINDEX >= CURRENTSTATE.WORDS.size) {
                completeQuiz()
            } else {
                _UISTATE.value = _UISTATE.value.copy(CURRENTINDEX = NEXTINDEX)
            }
        }
    }

    private fun completeQuiz() {
        val STATE = _UISTATE.value
        val TOPIC = STATE.TOPIC ?: return
        val STUDYRESULT = StudyResult.createFlashcardResult(
            id = UUID.randomUUID().toString(),
            topicId = TOPIC.id ?: "Lỗi không id FlashcardViewModel.kt",
            topicName = TOPIC.name ?: "Chủ đề không tên",
            startTime = STATE.STARTTIME,
            endTime = System.currentTimeMillis(),
            totalWords = STATE.WORDS.size,
            knownWords = STATE.KNOWNWORDS,
            learningWords = STATE.LEARNINGWORDS
        )
        STUDYRESULTREPOSITORY.addStudyResult(STUDYRESULT)
        _UISTATE.value = _UISTATE.value.copy(ISCOMPLETED = true)
    }
}