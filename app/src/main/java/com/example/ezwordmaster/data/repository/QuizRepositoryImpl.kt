package com.example.ezwordmaster.data.repository

import android.content.Context
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.QuizQuestion
import com.example.ezwordmaster.domain.repository.IQuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Triển khai [com.example.ezwordmaster.domain.repository.IQuizRepository] đọc dữ liệu từ res/raw/quiz_data.json.
 */
class QuizRepositoryImpl(
    private val appContext: Context
) : IQuizRepository {

    private val json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    override suspend fun loadQuestions(): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val input = appContext.resources.openRawResource(R.raw.quiz_data)
        input.use { stream ->
            val text = stream.reader().readText()
            json.decodeFromString(ListSerializer(QuizQuestion.serializer()), text)
        }
    }
}