package com.example.ezwordmaster.ui.screens.regime.practice.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.DynamicQuizQuestion
import com.example.ezwordmaster.model.QuizAnswerDetail
import com.example.ezwordmaster.model.QuizUiState
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class QuizViewModel(
    private val topicRepository: ITopicRepository,
    private val studyResultRepository: IStudyResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var allOtherWords: List<Word> = emptyList()

    /**
     * Tải chủ đề theo ID
     */
    fun loadTopic(topicId: String) {
        viewModelScope.launch {
            _uiState.value = QuizUiState() // Reset trạng thái

            val topic = topicRepository.getTopicById(topicId)
            val questionWords = topic?.words?.shuffled() ?: emptyList() // xào trộn câu hỏi

            allOtherWords = topicRepository.loadTopics().flatMap { it.words }

            if (questionWords.isEmpty()) {
                _uiState.value = QuizUiState()
                return@launch
            }

            val dynamicQuestions = questionWords.map { word -> createDynamicQuestion(word) }
            _uiState.value = QuizUiState(
                topic = topic,
                questions = dynamicQuestions,
                startTime = System.currentTimeMillis()
            )
        }
    }

    private fun createDynamicQuestion(word: Word): DynamicQuizQuestion {
        val correctAnswer = word.meaning ?: ""

        val wrongOptions = allOtherWords
            .filter { it.meaning != correctAnswer && !it.meaning.isNullOrEmpty() }
            .mapNotNull { it.meaning }
            .shuffled()
            .take(3)
        val allOptions = (wrongOptions + correctAnswer).shuffled()

        val isMeaningCorrect = Math.random() < 0.5
        val displayedMeaning = if (isMeaningCorrect || allOtherWords.size < 2) {
            correctAnswer
        } else {
            allOtherWords.mapNotNull { it.meaning }.randomOrNull() ?: "Nghĩa sai"
        }

        return DynamicQuizQuestion(
            questionWord = word,
            options = allOptions,
            displayedMeaning = displayedMeaning,
            isMeaningCorrect = isMeaningCorrect
        )
    }

    private fun getCurrentQuestion(): DynamicQuizQuestion? {
        return _uiState.value.questions.getOrNull(_uiState.value.currentIndex)
    }

    // --- Logic cho MultiChoice ---
    fun onMultiChoiceSelect(option: String) {
        _uiState.value = _uiState.value.copy(selectedOption = option)
    }

    fun submitMultiChoice() {
        val state = _uiState.value
        val question = getCurrentQuestion() ?: return
        val isCorrect =
            question.questionWord.meaning.equals(state.selectedOption, ignoreCase = true)
        updateStateAfterSubmit(
            isCorrect,
            state.selectedOption ?: "",
            question.questionWord.meaning ?: ""
        )
    }

    // --- Logic cho True/False ---
    fun onTrueFalseSelect(isTrue: Boolean) {
        _uiState.value = _uiState.value.copy(selectedTrueFalse = isTrue)
    }

    fun submitTrueFalse() {
        val state = _uiState.value
        val question = getCurrentQuestion() ?: return
        val selection = state.selectedTrueFalse ?: return
        val isCorrect =
            (selection && question.isMeaningCorrect) || (!selection && !question.isMeaningCorrect)
        updateStateAfterSubmit(
            isCorrect,
            if (selection) "Đúng" else "Sai",
            if (question.isMeaningCorrect) "Đúng" else "Sai"
        )
    }

    // --- Logic cho Essay ---
    fun onEssayAnswerChanged(answer: String) {
        _uiState.value = _uiState.value.copy(essayAnswer = answer)
    }

    fun submitEssay() {
        val state = _uiState.value
        val question = getCurrentQuestion() ?: return
        // Luôn cho phép submit nếu có nhập câu trả lời (không cần kiểm tra đúng/sai)
        if (state.essayAnswer.isNotBlank()) {
            // Chuẩn hóa câu trả lời: loại bỏ khoảng trắng thừa và chuyển thành chữ thường
            val normalizedUserAnswer = state.essayAnswer
                .trim() // Loại bỏ khoảng trắng ở đầu và cuối
                .replace("\\s+".toRegex(), " ") // Thay thế nhiều khoảng trắng bằng một khoảng trắng
                .lowercase() // Chuyển thành chữ thường

            val normalizedCorrectAnswer = (question.questionWord.meaning ?: "")
                .trim()
                .replace("\\s+".toRegex(), " ")
                .lowercase()

            val isCorrect = normalizedUserAnswer == normalizedCorrectAnswer

            updateStateAfterSubmit(
                isCorrect,
                state.essayAnswer, // Vẫn giữ nguyên câu trả lời gốc để hiển thị
                question.questionWord.meaning ?: ""
            )
        }
    }

    private fun updateStateAfterSubmit(
        isCorrect: Boolean,
        userAnswer: String,
        correctAnswer: String
    ) {
        val state = _uiState.value
        if (state.showResult) return

        val question = getCurrentQuestion() ?: return

        val newKnown = if (isCorrect) state.knownWords + 1 else state.knownWords
        val newLearning = if (!isCorrect) state.learningWords + 1 else state.learningWords

        val newDetail = QuizAnswerDetail(
            questionNumber = state.currentIndex + 1,
            question = question.questionWord.word ?: "",
            userAnswer = userAnswer,
            correctAnswer = correctAnswer,
            isCorrect = isCorrect
        )

        _uiState.value = state.copy(
            knownWords = newKnown,
            learningWords = newLearning,
            showResult = true,
            lastAnswerCorrect = isCorrect,
            answerDetails = state.answerDetails + newDetail
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.questions.size) {
            completeQuiz()
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                selectedOption = null,
                selectedTrueFalse = null,
                essayAnswer = "",
                showResult = false,
                lastAnswerCorrect = null
            )
        }
    }

    /**
     * Hoàn thành bài kiểm tra và lưu kết quả (giống FlashcardViewModel)
     */
    private fun completeQuiz() {
        val state = _uiState.value
        if (state.isCompleted) return // Tránh gọi nhiều lần

        val topic = state.topic ?: return
        val durationMs = System.currentTimeMillis() - state.startTime

        val studyResult = StudyResult.createQuizResult(
            id = UUID.randomUUID().toString(),
            topicId = topic.id ?: "Lỗi không id QuizViewModel.kt",
            topicName = topic.name ?: "Chủ đề không tên",
            duration = durationMs,
            totalWords = state.questions.size,
            knownWords = state.knownWords,
            learningWords = state.learningWords
        )

        studyResultRepository.addStudyResult(studyResult)
        _uiState.value = _uiState.value.copy(isCompleted = true)
    }
}