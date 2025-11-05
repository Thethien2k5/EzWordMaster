package com.example.ezwordmaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ezwordmaster.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    
    @Query("SELECT * FROM topics ORDER BY id")
    fun getAllTopics(): Flow<List<TopicEntity>>
    
    @Query("SELECT * FROM topics ORDER BY id")
    suspend fun getAllTopicsSync(): List<TopicEntity>
    
    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: String): TopicEntity?
    
    @Query("SELECT * FROM topics WHERE LOWER(name) = LOWER(:name)")
    suspend fun getTopicByName(name: String): TopicEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)
    
    @Update
    suspend fun updateTopic(topic: TopicEntity)
    
    @Query("UPDATE topics SET name = :newName WHERE id = :id")
    suspend fun updateTopicName(id: String, newName: String)
    
    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteTopicById(id: String)
    
    @Query("DELETE FROM topics")
    suspend fun deleteAllTopics()
    
    @Query("SELECT COUNT(*) FROM topics")
    suspend fun getTopicCount(): Int
    
    @Query("SELECT MAX(CAST(id AS INTEGER)) FROM topics")
    suspend fun getMaxTopicId(): Int?
}

