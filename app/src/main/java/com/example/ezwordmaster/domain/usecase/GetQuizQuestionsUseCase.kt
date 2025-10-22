package com.example.ezwordmaster.domain.usecase

import com.example.ezwordmaster.domain.model.QuizQuestion
import com.example.ezwordmaster.domain.repository.QuizRepository

/** Lấy danh sách câu hỏi từ repository. */
class GetQuizQuestionsUseCase(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(): List<QuizQuestion> = repository.loadQuestions()
}


