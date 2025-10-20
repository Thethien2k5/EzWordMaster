package com.example.ezwordmaster.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.quiz.QuizRepositoryImpl
import com.example.ezwordmaster.domain.model.QuizQuestion
import com.example.ezwordmaster.domain.usecase.GetQuizQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel quản lý logic cho màn hình Quiz Đúng/Sai.
 * Người dùng sẽ xem một từ tiếng Anh và nghĩa tiếng Việt, sau đó chọn Đúng hoặc Sai.
 * Nghĩa hiển thị có thể đúng hoặc sai (50% cơ hội mỗi loại).
 * 
 * @param application Context của ứng dụng
 * @param showAnswer Có hiển thị đáp án đúng/sai ngay khi chọn không
 */
class TrueFalseQuizViewModel(application: Application, private val showAnswer: Boolean = true) : AndroidViewModel(application) {

    // Khởi tạo các thành phần cần thiết
    private val repository = QuizRepositoryImpl(application.applicationContext) // Repository đọc dữ liệu từ JSON
    private val getQuestions = GetQuizQuestionsUseCase(repository) // Use case lấy danh sách câu hỏi

    /**
     * Trạng thái UI của màn hình Quiz Đúng/Sai.
     * Chứa tất cả dữ liệu cần thiết để hiển thị trên giao diện.
     */
    data class UiState(
        val questions: List<QuizQuestion> = emptyList(), // Danh sách tất cả câu hỏi
        val currentIndex: Int = 0, // Chỉ số câu hỏi hiện tại (bắt đầu từ 0)
        val currentQuestion: QuizQuestion? = null, // Câu hỏi đang hiển thị
        val displayedAnswer: String = "", // Nghĩa hiển thị (có thể đúng hoặc sai)
        val isCorrectAnswer: Boolean = true, // Nghĩa hiển thị có đúng không
        val selectedOption: Boolean? = null, // true = Đúng, false = Sai, null = chưa chọn
        val showResult: Boolean = false, // Có hiển thị kết quả không
        val score: Int = 0, // Điểm số hiện tại
        val isCompleted: Boolean = false, // Quiz đã hoàn thành chưa
        val totalQuestions: Int = 0, // Tổng số câu hỏi
        val lastAnswerCorrect: Boolean? = null, // Câu trả lời cuối cùng có đúng không
        val answerDetails: List<QuizAnswerDetail> = emptyList() // Chi tiết tất cả câu hỏi đã làm
    )

    // StateFlow để UI có thể observe và cập nhật tự động
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    /**
     * Khởi tạo ViewModel - tự động load câu hỏi khi tạo
     */
    init {
        loadQuestions()
    }

    /**
     * Tải danh sách câu hỏi từ file JSON và khởi tạo trạng thái ban đầu.
     * Hàm này được gọi tự động khi ViewModel được tạo.
     */
    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = getQuestions() // Gọi use case để lấy câu hỏi
            if (questions.isNotEmpty()) {
                val firstQuestion = questions[0]
                val (displayedAnswer, isCorrectAnswer) = generateRandomAnswer(firstQuestion, questions)
                
                // Cập nhật state với câu hỏi đầu tiên và nghĩa random
                _state.value = UiState(
                    questions = questions, // Lưu tất cả câu hỏi
                    currentIndex = 0, // Bắt đầu từ câu đầu tiên
                    currentQuestion = firstQuestion, // Hiển thị câu đầu tiên
                    displayedAnswer = displayedAnswer, // Nghĩa hiển thị (đúng hoặc sai)
                    isCorrectAnswer = isCorrectAnswer, // Nghĩa có đúng không
                    totalQuestions = questions.size // Lưu tổng số câu hỏi
                )
            }
        }
    }

    /**
     * Tạo nghĩa random: 50% cơ hội hiển thị nghĩa đúng, 50% cơ hội hiển thị nghĩa sai.
     * Nghĩa sai được lấy từ các câu hỏi khác để tạo độ khó phù hợp.
     * 
     * @param currentQuestion Câu hỏi hiện tại (chứa nghĩa đúng)
     * @param allQuestions Tất cả câu hỏi (để lấy nghĩa sai)
     * @return Pair<String, Boolean> - (nghĩa hiển thị, có đúng không)
     */
    private fun generateRandomAnswer(currentQuestion: QuizQuestion, allQuestions: List<QuizQuestion>): Pair<String, Boolean> {
        val isCorrect = Random.nextBoolean() // 50% đúng, 50% sai
        
        return if (isCorrect) {
            // Hiển thị nghĩa đúng
            Pair(currentQuestion.answer, true)
        } else {
            // Hiển thị nghĩa sai từ câu khác
            val wrongAnswers = allQuestions
                .filter { it != currentQuestion }
                .map { it.answer }
                .distinct()
            
            val randomWrongAnswer = wrongAnswers.randomOrNull() ?: "nghĩa sai"
            Pair(randomWrongAnswer, false)
        }
    }

    /**
     * Chọn Đúng hoặc Sai và tự động kiểm tra (nếu showAnswer = true).
     */
    fun selectOption(isTrue: Boolean) {
        val currentState = _state.value
        _state.value = currentState.copy(selectedOption = isTrue)
        
        // Luôn kiểm tra để có thể tiến trình (kể cả khi ẩn đáp án)
        submitAnswer()
    }

    /**
     * Kiểm tra đáp án và hiển thị kết quả.
     */
    fun submitAnswer() {
        val currentState = _state.value
        val selectedOption = currentState.selectedOption ?: return
        
        // Đáp án đúng khi:
        // - Chọn "Đúng" và nghĩa hiển thị là đúng, HOẶC
        // - Chọn "Sai" và nghĩa hiển thị là sai
        val isCorrect = (selectedOption && currentState.isCorrectAnswer) || 
                       (!selectedOption && !currentState.isCorrectAnswer)
        
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        // Thêm chi tiết câu hỏi vào danh sách
        val newAnswerDetail = QuizAnswerDetail(
            questionNumber = currentState.currentIndex + 1,
            question = currentState.currentQuestion?.question ?: "",
            userAnswer = if (selectedOption) "Đúng" else "Sai",
            correctAnswer = if (currentState.isCorrectAnswer) "Đúng" else "Sai",
            isCorrect = isCorrect
        )
        
        _state.value = currentState.copy(
            showResult = true, // Luôn cho phép chuyển câu tiếp theo
            score = newScore,
            lastAnswerCorrect = isCorrect,
            answerDetails = currentState.answerDetails + newAnswerDetail
        )
    }

    /**
     * Chuyển sang câu hỏi tiếp theo hoặc hoàn thành quiz.
     */
    fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentIndex + 1
        
        if (nextIndex >= currentState.questions.size) {
            // Hoàn thành quiz
            _state.value = currentState.copy(isCompleted = true)
        } else {
            // Chuyển sang câu tiếp theo
            val nextQuestion = currentState.questions[nextIndex]
            val (displayedAnswer, isCorrectAnswer) = generateRandomAnswer(nextQuestion, currentState.questions)
            
            _state.value = currentState.copy(
                currentIndex = nextIndex,
                currentQuestion = nextQuestion,
                displayedAnswer = displayedAnswer,
                isCorrectAnswer = isCorrectAnswer,
                selectedOption = null,
                showResult = false,
                lastAnswerCorrect = null
            )
        }
    }
}
