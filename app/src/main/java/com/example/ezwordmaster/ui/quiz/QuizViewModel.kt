package com.example.ezwordmaster.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.quiz.QuizRepositoryImpl
import com.example.ezwordmaster.domain.model.QuizQuestion
import com.example.ezwordmaster.domain.usecase.CheckAnswerUseCase
import com.example.ezwordmaster.domain.usecase.GetQuizQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý trạng thái quiz: danh sách câu hỏi, index hiện tại, điểm và tiến trình.
 */
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuizRepositoryImpl(application.applicationContext)
    private val getQuestions = GetQuizQuestionsUseCase(repository)
    private val checkAnswer = CheckAnswerUseCase()

    data class UiState(
        val questions: List<QuizQuestion> = emptyList(),
        val currentIndex: Int = 0,
        val selectedOption: String? = null,
        val score: Int = 0,
        val isCompleted: Boolean = false,
        val showAnswerResult: Boolean = false,
        val lastAnswerCorrect: Boolean? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        load()
    }

    /** Tải câu hỏi từ JSON. */
    fun load() {
        viewModelScope.launch {
            val qs = getQuestions()
            _state.value = _state.value.copy(questions = qs, currentIndex = 0, score = 0, isCompleted = false, selectedOption = null, showAnswerResult = false, lastAnswerCorrect = null)
        }
    }

    /** Chọn 1 đáp án. */
    fun selectOption(option: String) {
        _state.value = _state.value.copy(selectedOption = option)
    }

    /** Xác nhận đáp án, cập nhật điểm và hiển thị kết quả đúng/sai. */
    fun submitAnswer() {
        val s = _state.value
        val question = s.questions.getOrNull(s.currentIndex) ?: return
        val correct = checkAnswer(s.selectedOption, question.answer)
        val newScore = if (correct) s.score + 1 else s.score
        _state.value = s.copy(score = newScore, showAnswerResult = true, lastAnswerCorrect = correct)
    }

    /** Sang câu tiếp theo, reset lựa chọn; nếu hết thì đánh dấu hoàn thành. */
    fun nextQuestion() {
        val s = _state.value
        val nextIndex = s.currentIndex + 1
        val finished = nextIndex >= s.questions.size
        _state.value = s.copy(
            currentIndex = if (finished) s.currentIndex else nextIndex,
            selectedOption = null,
            isCompleted = finished,
            showAnswerResult = false,
            lastAnswerCorrect = null
        )
    }
}


