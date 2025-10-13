package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.domain.model.QuizQuestion

/**
 * Repository giao tiếp nguồn dữ liệu quiz (JSON trong res/raw).
 */
interface QuizRepository {
    suspend fun loadQuestions(): List<QuizQuestion>
}


