package com.example.ezwordmaster.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_results",
    indices = [
        Index(value = ["topicId"]),
        Index(value = ["studyMode"]),
        Index(value = ["day"])
    ]
)
data class StudyResultEntity(
    @PrimaryKey
    val id: String,
    val topicId: String,
    val topicName: String,
    val studyMode: String, // "flashcard", "flipcard", "quiz_multi", etc.
    val day: String, // Format: "dd/MM/yyyy"
    val duration: Long, // Total time in seconds

    // Data for Flashcard & Quiz
    val totalWords: Int? = null,
    val knownWords: Int? = null,
    val learningWords: Int? = null,
    val accuracy: Float? = null,

    // Data for FlipCard
    val totalPairs: Int? = null,
    val matchedPairs: Int? = null,
    val completionRate: Float? = null,
    val playTime: Long? = null
)


