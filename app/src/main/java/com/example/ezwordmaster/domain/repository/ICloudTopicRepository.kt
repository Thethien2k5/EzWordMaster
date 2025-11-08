package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.model.Topic

/**
 * Hợp đồng (Interface) cho việc thao tác với Topic trên Cloud (Firestore)
 */
interface ICloudTopicRepository {
    suspend fun saveTopics(topics: List<Topic>): Result<Unit>
    suspend fun loadTopics(): Result<List<Topic>>
    suspend fun hasData(): Boolean
}

