package com.example.ezwordmaster.domain.usecase

/** Kiểm tra đáp án người dùng có đúng không (không phân biệt hoa thường). */
class CheckAnswerUseCase {
    operator fun invoke(userAnswer: String?, correctAnswer: String): Boolean {
        if (userAnswer == null) return false
        return userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
    }
}


