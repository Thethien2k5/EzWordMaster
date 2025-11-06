package com.example.ezwordmaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ezwordmaster.data.local.entity.StudyResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyResultDao {

    @Query("SELECT * FROM study_results ORDER BY day DESC, id DESC")
    fun getAllResults(): Flow<List<StudyResultEntity>>

    @Query("SELECT * FROM study_results WHERE id = :id")
    suspend fun getResultById(id: String): StudyResultEntity?

    @Query("SELECT * FROM study_results WHERE studyMode = :studyMode ORDER BY day DESC")
    suspend fun getResultsByMode(studyMode: String): List<StudyResultEntity>

    @Query("SELECT * FROM study_results WHERE topicId = :topicId ORDER BY day DESC")
    suspend fun getResultsByTopic(topicId: String): List<StudyResultEntity>

    @Query("SELECT * FROM study_results WHERE day = :day ORDER BY id DESC")
    suspend fun getResultsByDay(day: String): List<StudyResultEntity>

    @Query("SELECT * FROM study_results WHERE day = :day AND topicId = :topicId ORDER BY id DESC")
    suspend fun getResultsByDayAndTopic(day: String, topicId: String): List<StudyResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: StudyResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<StudyResultEntity>)

    @Query("DELETE FROM study_results WHERE id = :id")
    suspend fun deleteResultById(id: String)

    @Query("DELETE FROM study_results")
    suspend fun deleteAllResults()

    @Query("DELETE FROM study_results WHERE id IN (SELECT id FROM study_results ORDER BY id ASC LIMIT 1)")
    suspend fun deleteOldestResult()

    @Query("SELECT COUNT(*) FROM study_results")
    suspend fun getResultCount(): Int

    @Query("SELECT * FROM study_results ORDER BY day DESC LIMIT :limit")
    suspend fun getLatestResults(limit: Int): List<StudyResultEntity>
}

