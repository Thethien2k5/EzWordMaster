package com.example.ezwordmaster.model

// Đại diện cho một câu hỏi được tạo ra động
data class DynamicQuizQuestion(
    val questionWord: Word,
    val options: List<String>,
    val displayedMeaning: String,
    val isMeaningCorrect: Boolean
)

// Dữ liệu chi tiết của một câu hỏi đã làm
data class QuizAnswerDetail(
    val questionNumber: Int,
    val question: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)

// Trạng thái (State) duy nhất cho toàn bộ luồng Quiz
data class QuizUiState(
    val topic: Topic? = null,
    val questions: List<DynamicQuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val knownWords: Int = 0,
    val learningWords: Int = 0,
    val selectedOption: String? = null,
    val selectedTrueFalse: Boolean? = null,
    val essayAnswer: String = "",
    val showResult: Boolean = false,
    val lastAnswerCorrect: Boolean? = null,
    val isCompleted: Boolean = false,
    val startTime: Long = 0L,
    val answerDetails: List<QuizAnswerDetail> = emptyList()
)