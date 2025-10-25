package com.example.ezwordmaster.model

// Class thống kê
data class StudyStats(
    val totalSessions: Int,
    val totalStudyTime: Long, // giây
    val totalWordsLearned: Int,
    val averageAccuracy: Float,
    val averageCompletionRate: Float
)