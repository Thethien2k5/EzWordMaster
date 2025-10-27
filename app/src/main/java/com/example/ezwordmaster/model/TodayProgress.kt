package com.example.ezwordmaster.model

/**
 * Data class chứa thông tin tiến trình ôn tập trong ngày
 */
data class TodayProgress(
    val day: String,
    val totalSessions: Int,
    val totalKnownWords: Int,
    val totalWords: Int,
    val results: List<StudyResult>
)