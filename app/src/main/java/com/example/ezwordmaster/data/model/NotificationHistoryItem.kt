package com.example.ezwordmaster.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationHistoryItem(
    val id: String, // Dùng UUID để đảm bảo mỗi item là duy nhất
    val title: String,
    val content: String,
    val timestamp: Long
)

@Serializable
data class NotificationPhrase(
    val title: String,
    val content: String
)