package com.example.ezwordmaster.model

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
    val day: String, // Ngày thực hiện ôn tập (dd/MM/yyyy)
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
    companion object {
        /**
         * Lấy ngày hiện tại theo định dạng dd/MM/yyyy
         */
        private fun getCurrentDay(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(Date())
        }

        /**
         * Helper function để tạo StudyResult cho Flashcard
         */
        fun createFlashcardResult(
            id: String,
            topicId: String,
            topicName: String,
            duration: Long, // Truyền duration (milliseconds) thay vì startTime/endTime
            totalWords: Int,
            knownWords: Int,
            learningWords: Int
        ): StudyResult {
            val durationInSeconds = duration / 1000 // chuyển từ ms sang giây
            val accuracy = if (totalWords > 0) (knownWords * 100f) / totalWords else 0f

            return StudyResult(
                id = id,
                topicId = topicId,
                topicName = topicName,
                studyMode = "flashcard",
                day = getCurrentDay(),
                duration = durationInSeconds,
                totalWords = totalWords,
                knownWords = knownWords,
                learningWords = learningWords,
                accuracy = accuracy
            )
        }

        /**
         * Helper function để tạo StudyResult cho FlipCard
         */
        fun createFlipCardResult(
            id: String,
            topicId: String,
            topicName: String,
            duration: Long, // Truyền duration (milliseconds) thay vì startTime/endTime
            totalPairs: Int,
            matchedPairs: Int,
            playTime: Long
        ): StudyResult {
            val durationInSeconds = duration / 1000 // chuyển từ ms sang giây
            val completionRate = if (totalPairs > 0) (matchedPairs * 100f) / totalPairs else 0f

            return StudyResult(
                id = id,
                topicId = topicId,
                topicName = topicName,
                studyMode = "flipcard",
                day = getCurrentDay(),
                duration = durationInSeconds,
                totalPairs = totalPairs,
                matchedPairs = matchedPairs,
                completionRate = completionRate,
                playTime = playTime
            )
        }
    }
}

@Serializable
data class StudyResultsList(
    val results: List<StudyResult>
)