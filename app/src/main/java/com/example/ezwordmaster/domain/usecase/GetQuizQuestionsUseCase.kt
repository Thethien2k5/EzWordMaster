package com.example.ezwordmaster.domain.usecase

import com.example.ezwordmaster.model.QuizQuestion
import com.example.ezwordmaster.domain.repository.IQuizRepository

/** Lấy danh sách câu hỏi từ repository. */
class GetQuizQuestionsUseCase(
    private val repository: IQuizRepository
) {
    suspend operator fun invoke(): List<QuizQuestion> = repository.loadQuestions()
}


