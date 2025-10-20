package com.example.ezwordmaster.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StudyResult(
    val id: String,
    val topicId: String,
    val topicName: String,
    val studyMode: String, // "flashcard" hoặc "flipcard"
    val startTime: Long,
    val endTime: Long,
    val duration: Long, // thời gian học (giây)
    
    // Flashcard data
    val totalWords: Int? = null,
    val knownWords: Int? = null,
    val learningWords: Int? = null,
    val accuracy: Float? = null,
    
    // FlipCard data
    val totalPairs: Int? = null,
    val matchedPairs: Int? = null,
    val completionRate: Float? = null,
    val playTime: Long? = null
) {
    // Helper function để tạo StudyResult cho Flashcard
    companion object {
        fun createFlashcardResult(
            id: String,
            topicId: String,
            topicName: String,
            startTime: Long,
            endTime: Long,
            totalWords: Int,
            knownWords: Int,
            learningWords: Int
        ): StudyResult {
            val duration = (endTime - startTime) / 1000 // chuyển từ ms sang giây
            val accuracy = if (totalWords > 0) (knownWords * 100f) / totalWords else 0f
            
            return StudyResult(
                id = id,
                topicId = topicId,
                topicName = topicName,
                studyMode = "flashcard",
                startTime = startTime,
                endTime = endTime,
                duration = duration,
                totalWords = totalWords,
                knownWords = knownWords,
                learningWords = learningWords,
                accuracy = accuracy
            )
        }
        
        // Helper function để tạo StudyResult cho FlipCard
        fun createFlipCardResult(
            id: String,
            topicId: String,
            topicName: String,
            startTime: Long,
            endTime: Long,
            totalPairs: Int,
            matchedPairs: Int,
            playTime: Long
        ): StudyResult {
            val duration = (endTime - startTime) / 1000 // chuyển từ ms sang giây
            val completionRate = if (totalPairs > 0) (matchedPairs * 100f) / totalPairs else 0f
            
            return StudyResult(
                id = id,
                topicId = topicId,
                topicName = topicName,
                studyMode = "flipcard",
                startTime = startTime,
                endTime = endTime,
                duration = duration,
                totalPairs = totalPairs,
                matchedPairs = matchedPairs,
                completionRate = completionRate,
                playTime = playTime
            )
        }
    }
}
