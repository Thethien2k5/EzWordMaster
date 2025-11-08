package com.example.ezwordmaster.data.local.dao

import androidx.room.*
import com.example.ezwordmaster.data.local.entity.TranslationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationHistoryDao {

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAllTranslationHistory(): Flow<List<TranslationHistoryEntity>>

    @Query("SELECT * FROM translation_history WHERE originalText LIKE :query OR translatedText LIKE :query ORDER BY timestamp DESC")
    fun searchTranslationHistory(query: String): Flow<List<TranslationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationHistoryEntity)

    @Delete
    suspend fun deleteTranslation(translation: TranslationHistoryEntity)

    @Query("DELETE FROM translation_history WHERE id = :id")
    suspend fun deleteTranslationById(id: String)

    @Query("DELETE FROM translation_history")
    suspend fun deleteAllTranslationHistory()

    @Query("SELECT * FROM translation_history WHERE originalText = :text LIMIT 1")
    suspend fun findTranslationByText(text: String): TranslationHistoryEntity?
}