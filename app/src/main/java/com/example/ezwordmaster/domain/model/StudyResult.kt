package com.example.ezwordmaster.domain.model

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class StudyResult(
    val id: String,
    val topicId: String,
    val topicName: String,
    val studyMode: String, // "flashcard" hoặc "flipcard"
    val startTime: String,
    val endTime: String,
    val duration: String,
    
    // Flashcard data
    val totalWords: Int? = null,
    val knownWords: Int? = null,
    val learningWords: Int? = null,
    val accuracy: Float? = null,
    
    // FlipCard data
    val totalPairs: Int? = null,
    val matchedPairs: Int? = null,
    val completionRate: Float? = null,
    val playTime: String? = null
) {
    // Helper functions để format thời gian
    companion object {
        private fun formatTime(timestamp: Long): String {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return formatter.format(date)
        }
        
        private fun formatDuration(seconds: Long): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            
            return when {
                hours > 0 -> "${hours}h ${minutes}m ${secs}s"
                minutes > 0 -> "${minutes}m ${secs}s"
                else -> "${secs}s"
            }
        }
        
        fun createFlashcardResult(
            id: String,
            topicId: String,
            topicName: String,
            startTimeRaw: Long,
            endTimeRaw: Long,
            totalWords: Int,
            knownWords: Int,
            learningWords: Int
        ): StudyResult {
            val durationSeconds = (endTimeRaw - startTimeRaw) / 1000 // chuyển từ ms sang giây
            val accuracy = if (totalWords > 0) (knownWords * 100f) / totalWords else 0f
            
            return StudyResult(
                id = id,
                topicId = topicId,
                topicName = topicName,
                studyMode = "flashcard",
                startTime = formatTime(startTimeRaw),
                endTime = formatTime(endTimeRaw),
                duration = formatDuration(durationSeconds),
                totalWords = totalWords,
                knownWords = knownWords,
                learningWords = learningWords,
                accuracy = accuracy
            )
        }
        
        fun createFlipCardResult(
            id: String,
            topicId: String,
            topicName: String,
            startTimeRaw: Long,
            endTimeRaw: Long,
            totalPairs: Int,
            matchedPairs: Int,
            playTimeRaw: Long
        ): StudyResult {
            val durationSeconds = (endTimeRaw - startTimeRaw) / 1000 // chuyển từ ms sang giây
            val completionRate = if (totalPairs > 0) (matchedPairs * 100f) / totalPairs else 0f
            
            return StudyResult(
                id = id,
                topicId = topicId,
                topicName = topicName,
                studyMode = "flipcard",
                startTime = formatTime(startTimeRaw),
                endTime = formatTime(endTimeRaw),
                duration = formatDuration(durationSeconds),
                totalPairs = totalPairs,
                matchedPairs = matchedPairs,
                completionRate = completionRate,
                playTime = formatDuration(playTimeRaw)
            )
        }
    }
}
