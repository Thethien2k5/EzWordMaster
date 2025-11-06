package com.example.ezwordmaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ezwordmaster.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE topicId = :topicId ORDER BY wordId")
    fun getWordsByTopicId(topicId: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE topicId = :topicId ORDER BY wordId")
    suspend fun getWordsByTopicIdSync(topicId: String): List<WordEntity>

    @Query("SELECT * FROM words WHERE topicId = :topicId AND LOWER(word) = LOWER(:word) AND LOWER(meaning) = LOWER(:meaning)")
    suspend fun getWordByTopicAndContent(
        topicId: String,
        word: String?,
        meaning: String?
    ): WordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("DELETE FROM words WHERE topicId = :topicId AND word = :word AND meaning = :meaning")
    suspend fun deleteWordFromTopic(topicId: String, word: String?, meaning: String?)

    @Query("DELETE FROM words WHERE topicId = :topicId")
    suspend fun deleteWordsByTopicId(topicId: String)

    @Query("DELETE FROM words WHERE wordId = :wordId")
    suspend fun deleteWordById(wordId: Long)
}

