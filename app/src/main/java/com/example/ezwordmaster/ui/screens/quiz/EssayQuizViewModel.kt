package com.example.ezwordmaster.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.repository.QuizRepositoryImpl
import com.example.ezwordmaster.model.QuizQuestion
import com.example.ezwordmaster.domain.usecase.CheckAnswerUseCase
import com.example.ezwordmaster.domain.usecase.GetQuizQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý logic cho màn hình Quiz tự luận.
 * Người dùng sẽ nhập đáp án bằng cách gõ text vào ô input.
 * 
 * @param application Context của ứng dụng
 * @param showAnswer Có hiển thị đáp án đúng/sai ngay khi nhập không
 */
class EssayQuizViewModel(application: Application, private val showAnswer: Boolean = true) : AndroidViewModel(application) {

    // Khởi tạo các thành phần cần thiết
    private val repository = QuizRepositoryImpl(application.applicationContext) // Repository đọc dữ liệu từ JSON
    private val getQuestions = GetQuizQuestionsUseCase(repository) // Use case lấy danh sách câu hỏi
    private val checkAnswer = CheckAnswerUseCase() // Use case kiểm tra đáp án

    /**
     * Trạng thái UI của màn hình Quiz tự luận.
     * Chứa tất cả dữ liệu cần thiết để hiển thị trên giao diện.
     */
    data class UiState(
        val questions: List<QuizQuestion> = emptyList(), // Danh sách tất cả câu hỏi
        val currentIndex: Int = 0, // Chỉ số câu hỏi hiện tại (bắt đầu từ 0)
        val currentQuestion: QuizQuestion? = null, // Câu hỏi đang hiển thị
        val userAnswer: String = "", // Đáp án người dùng đã nhập
        val showCorrectAnswer: Boolean = false, // Có hiển thị đáp án đúng không
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
                // Cập nhật state với câu hỏi đầu tiên
                _state.value = UiState(
                    questions = questions, // Lưu tất cả câu hỏi
                    currentIndex = 0, // Bắt đầu từ câu đầu tiên
                    currentQuestion = questions[0], // Hiển thị câu đầu tiên
                    totalQuestions = questions.size // Lưu tổng số câu hỏi
                )
            }
        }
    }

    /**
     * Cập nhật đáp án người dùng nhập vào và tự động kiểm tra (nếu được bật).
     * 
     * @param answer Đáp án người dùng vừa nhập
     */
    fun updateUserAnswer(answer: String) {
        val currentState = _state.value
        // Cập nhật đáp án người dùng vào state
        _state.value = currentState.copy(userAnswer = answer)
        
        // Nếu bật hiển thị đáp án và đã nhập đáp án, tự động kiểm tra ngay
        if (showAnswer && answer.isNotBlank()) {
            submitAnswer()
        }
    }

    /**
     * Kiểm tra đáp án người dùng và cập nhật điểm số.
     * Hàm này được gọi khi người dùng nhập đáp án hoặc bấm nút kiểm tra.
     */
    fun submitAnswer() {
        val currentState = _state.value
        val question = currentState.currentQuestion ?: return // Lấy câu hỏi hiện tại
        
        // Kiểm tra đáp án có đúng không (không phân biệt hoa thường)
        val isCorrect = checkAnswer(currentState.userAnswer, question.answer)
        // Tính điểm mới: nếu đúng thì +1, sai thì giữ nguyên
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        // Tạo chi tiết câu hỏi để lưu vào danh sách kết quả
        val newAnswerDetail = QuizAnswerDetail(
            questionNumber = currentState.currentIndex + 1, // Số thứ tự câu hỏi (bắt đầu từ 1)
            question = question.question, // Từ tiếng Anh
            userAnswer = currentState.userAnswer, // Đáp án người dùng nhập
            correctAnswer = question.answer, // Đáp án đúng
            isCorrect = isCorrect // Có đúng không
        )
        
        // Cập nhật state với kết quả kiểm tra
        _state.value = currentState.copy(
            showCorrectAnswer = true, // Cho phép hiển thị đáp án và chuyển câu tiếp theo
            score = newScore, // Cập nhật điểm số
            lastAnswerCorrect = isCorrect, // Lưu kết quả câu này
            answerDetails = currentState.answerDetails + newAnswerDetail // Thêm vào danh sách chi tiết
        )
    }

    /**
     * Chuyển sang câu hỏi tiếp theo hoặc hoàn thành quiz.
     * Hàm này được gọi khi người dùng bấm nút "Câu tiếp theo".
     */
    fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentIndex + 1 // Tính chỉ số câu tiếp theo
        
        if (nextIndex >= currentState.questions.size) {
            // Đã làm hết tất cả câu hỏi - hoàn thành quiz
            _state.value = currentState.copy(isCompleted = true)
        } else {
            // Chuyển sang câu hỏi tiếp theo
            val nextQuestion = currentState.questions[nextIndex]
            
            // Reset trạng thái cho câu hỏi mới
            _state.value = currentState.copy(
                currentIndex = nextIndex, // Cập nhật chỉ số câu hỏi
                currentQuestion = nextQuestion, // Hiển thị câu hỏi mới
                userAnswer = "", // Xóa đáp án cũ
                showCorrectAnswer = false, // Ẩn đáp án đúng
                lastAnswerCorrect = null // Reset kết quả câu trước
            )
        }
    }
}
