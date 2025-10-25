package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.model.QuizQuestion

/**
 * Repository giao tiếp nguồn dữ liệu quiz (JSON trong res/raw).
 */
interface IQuizRepository {
    suspend fun loadQuestions(): List<QuizQuestion>
}


