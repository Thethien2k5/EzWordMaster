package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.data.local.entity.TranslationHistoryEntity
import com.example.ezwordmaster.model.DetailedTranslationResult
import kotlinx.coroutines.flow.Flow

interface ITranslationRepository {
    // Remote operations
    suspend fun translateText(
        text: String,
        sourceLang: String,
        targetLang: String
    ): DetailedTranslationResult

    // Local operations
    fun getAllTranslationHistory(): Flow<List<TranslationHistoryEntity>>
    fun searchTranslationHistory(query: String): Flow<List<TranslationHistoryEntity>>
    suspend fun insertTranslation(translation: TranslationHistoryEntity)
    suspend fun deleteTranslation(translation: TranslationHistoryEntity)
    suspend fun deleteTranslationById(id: String)
    suspend fun deleteAllTranslationHistory()
    suspend fun findTranslationByText(text: String): TranslationHistoryEntity?
    fun cleanup()
}