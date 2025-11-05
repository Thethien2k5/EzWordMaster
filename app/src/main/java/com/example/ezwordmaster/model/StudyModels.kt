package com.example.ezwordmaster.model

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- MÔ HÌNH DỮ LIỆU CỐT LÕI ---
@Serializable
data class StudyResult(
    val id: String,
    val topicId: String,
    val topicName: String,
    val studyMode: String, // "flashcard", "flipcard", "quiz_multi", v.v.
    val day: String,
    val duration: Long, // Tổng thời gian (giây)

    // Dữ liệu chung cho Flashcard & Quiz
    val totalWords: Int? = null,
    val knownWords: Int? = null,
    val learningWords: Int? = null,
    val accuracy: Float? = null,

    // Dữ liệu cho FlipCard
    val totalPairs: Int? = null,
    val matchedPairs: Int? = null,
    val completionRate: Float? = null,
    val playTime: Long? = null
) {
    companion object {
        private fun getCurrentDay(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun createFlashcardResult(
            id: String, topicId: String, topicName: String,
            duration: Long, // milliseconds
            totalWords: Int, knownWords: Int, learningWords: Int
        ): StudyResult {
            val durationInSeconds = duration / 1000
            val accuracy = if (totalWords > 0) (knownWords * 100f) / totalWords else 0f
            return StudyResult(
                id = id, topicId = topicId, topicName = topicName,
                studyMode = "flashcard", day = getCurrentDay(), duration = durationInSeconds,
                totalWords = totalWords, knownWords = knownWords,
                learningWords = learningWords, accuracy = accuracy
            )
        }

        fun createFlipCardResult(
            id: String, topicId: String, topicName: String,
            duration: Long, // milliseconds
            totalPairs: Int, matchedPairs: Int, playTime: Long
        ): StudyResult {
            val durationInSeconds = duration / 1000
            val completionRate = if (totalPairs > 0) (matchedPairs * 100f) / totalPairs else 0f
            return StudyResult(
                id = id, topicId = topicId, topicName = topicName,
                studyMode = "flipcard", day = getCurrentDay(), duration = durationInSeconds,
                totalPairs = totalPairs, matchedPairs = matchedPairs,
                completionRate = completionRate, playTime = playTime
            )
        }

        // CÓ THỂ THÊM: Helper cho Quiz Result
        fun createQuizResult(
            id: String, topicId: String, topicName: String,
            duration: Long, // milliseconds
            totalWords: Int, knownWords: Int, learningWords: Int
        ): StudyResult {
            val durationInSeconds = duration / 1000
            val accuracy = if (totalWords > 0) (knownWords * 100f) / totalWords else 0f
            return StudyResult(
                id = id, topicId = topicId, topicName = topicName,
                studyMode = "quiz", day = getCurrentDay(), duration = durationInSeconds,
                totalWords = totalWords, knownWords = knownWords,
                learningWords = learningWords, accuracy = accuracy
            )
        }
    }
}

@Serializable
data class StudyResultsList(
    val results: List<StudyResult>
)

// --- CÁC MÔ HÌNH THỐNG KÊ & HIỂN THỊ ---

data class StudyStats(
    val totalSessions: Int,
    val totalStudyTime: Long, // giây
    val totalWordsLearned: Int,
    val averageAccuracy: Float,
    val averageCompletionRate: Float
)

data class TodayProgress(
    val day: String,
    val totalSessions: Int,
    val totalKnownWords: Int,
    val totalWords: Int,
    val results: List<StudyResult>
)

data class LatestStudyInfo(
    val knownWords: Int,
    val totalWords: Int,
    val day: String
)