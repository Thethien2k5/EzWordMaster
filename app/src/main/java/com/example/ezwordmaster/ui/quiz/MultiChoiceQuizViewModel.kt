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
import kotlin.random.Random

/**
 * ViewModel quản lý logic cho màn hình Quiz nhiều đáp án.
 * Người dùng sẽ chọn 1 trong 4 đáp án được hiển thị.
 * 3 đáp án sai được random từ các câu hỏi khác.
 * 
 * @param application Context của ứng dụng
 * @param showAnswer Có hiển thị đáp án đúng/sai ngay khi chọn không
 */
class MultiChoiceQuizViewModel(application: Application, private val showAnswer: Boolean = true) : AndroidViewModel(application) {

    // Khởi tạo các thành phần cần thiết
    private val repository = QuizRepositoryImpl(application.applicationContext) // Repository đọc dữ liệu từ JSON
    private val getQuestions = GetQuizQuestionsUseCase(repository) // Use case lấy danh sách câu hỏi
    private val checkAnswer = CheckAnswerUseCase() // Use case kiểm tra đáp án

    /**
     * Trạng thái UI của màn hình Quiz nhiều đáp án.
     * Chứa tất cả dữ liệu cần thiết để hiển thị trên giao diện.
     */
    data class UiState(
        val questions: List<QuizQuestion> = emptyList(), // Danh sách tất cả câu hỏi
        val currentIndex: Int = 0, // Chỉ số câu hỏi hiện tại (bắt đầu từ 0)
        val currentQuestion: QuizQuestion? = null, // Câu hỏi đang hiển thị
        val shuffledOptions: List<String> = emptyList(), // 4 đáp án đã được xáo trộn
        val selectedOption: String? = null, // Đáp án người dùng đã chọn
        val showCorrectAnswer: Boolean = false, // Có hiển thị đáp án đúng không
        val score: Int = 0, // Điểm số hiện tại
        val isCompleted: Boolean = false, // Quiz đã hoàn thành chưa
        val totalQuestions: Int = 0, // Tổng số câu hỏi
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
                val shuffledOptions = generateShuffledOptions(questions[0], questions)
                // Cập nhật state với câu hỏi đầu tiên và đáp án random
                _state.value = UiState(
                    questions = questions, // Lưu tất cả câu hỏi
                    currentIndex = 0, // Bắt đầu từ câu đầu tiên
                    currentQuestion = questions[0], // Hiển thị câu đầu tiên
                    shuffledOptions = shuffledOptions, // Tạo 4 đáp án random
                    totalQuestions = questions.size // Lưu tổng số câu hỏi
                )
            }
        }
    }

    /**
     * Tạo danh sách 4 đáp án bao gồm 1 đáp án đúng và 3 đáp án sai random.
     * 3 đáp án sai được lấy từ các câu hỏi khác để tạo độ khó phù hợp.
     * 
     * @param currentQuestion Câu hỏi hiện tại (chứa đáp án đúng)
     * @param allQuestions Tất cả câu hỏi (để lấy đáp án sai)
     * @return Danh sách 4 đáp án đã được xáo trộn
     */
    private fun generateShuffledOptions(currentQuestion: QuizQuestion, allQuestions: List<QuizQuestion>): List<String> {
        val correctAnswer = currentQuestion.answer // Lấy đáp án đúng
        
        // Lấy tất cả đáp án từ các câu khác (loại bỏ đáp án đúng của câu hiện tại)
        val wrongAnswers = allQuestions
            .filter { it != currentQuestion } // Loại bỏ câu hỏi hiện tại
            .map { it.answer } // Lấy đáp án đúng của các câu khác
            .distinct() // Đảm bảo không có đáp án trùng lặp
            .filter { it != correctAnswer } // Loại bỏ đáp án đúng của câu hiện tại
        
        // Chọn ngẫu nhiên 3 đáp án sai
        val selectedWrongAnswers = wrongAnswers.shuffled().take(3)
        
        // Tạo danh sách 4 đáp án và trộn để đáp án đúng không luôn ở vị trí cố định
        val allOptions = (selectedWrongAnswers + correctAnswer).shuffled()
        
        return allOptions
    }

    /**
     * Chọn một đáp án từ danh sách lựa chọn và tự động kiểm tra (nếu được bật).
     * 
     * @param option Đáp án người dùng vừa chọn
     */
    fun selectOption(option: String) {
        val currentState = _state.value
        // Cập nhật đáp án được chọn vào state
        _state.value = currentState.copy(selectedOption = option)
        
        // Luôn kiểm tra để có thể tiến trình (kể cả khi ẩn đáp án)
        submitAnswer()
    }

    /**
     * Kiểm tra đáp án người dùng và cập nhật điểm số.
     * Hàm này được gọi khi người dùng chọn đáp án hoặc bấm nút kiểm tra.
     */
    fun submitAnswer() {
        val currentState = _state.value
        val question = currentState.currentQuestion ?: return // Lấy câu hỏi hiện tại

        // Kiểm tra đáp án có đúng không (không phân biệt hoa thường)
        val isCorrect = checkAnswer(currentState.selectedOption, question.answer)
        // Tính điểm mới: nếu đúng thì +1, sai thì giữ nguyên
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score

        // Tạo chi tiết câu hỏi để lưu vào danh sách kết quả
        val newAnswerDetail = QuizAnswerDetail(
            questionNumber = currentState.currentIndex + 1, // Số thứ tự câu hỏi (bắt đầu từ 1)
            question = question.question, // Từ tiếng Anh
            userAnswer = currentState.selectedOption ?: "", // Đáp án người dùng chọn
            correctAnswer = question.answer, // Đáp án đúng
            isCorrect = isCorrect // Có đúng không
        )

        // Cập nhật state với kết quả kiểm tra
        _state.value = currentState.copy(
            showCorrectAnswer = true, // Cho phép hiển thị đáp án và chuyển câu tiếp theo
            score = newScore, // Cập nhật điểm số
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
            val shuffledOptions = generateShuffledOptions(nextQuestion, currentState.questions)
            
            // Reset trạng thái cho câu hỏi mới
            _state.value = currentState.copy(
                currentIndex = nextIndex, // Cập nhật chỉ số câu hỏi
                currentQuestion = nextQuestion, // Hiển thị câu hỏi mới
                shuffledOptions = shuffledOptions, // Tạo đáp án random mới
                selectedOption = null, // Xóa lựa chọn cũ
                showCorrectAnswer = false // Ẩn đáp án đúng
            )
        }
    }
}
