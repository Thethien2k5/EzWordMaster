package com.example.ezwordmaster.model

import kotlinx.serialization.Serializable

@Serializable // cho phép chuyển đổi giữa kotlin và json
data class Topic(
    val id: String? = null,
    val name: String? = null,
    val words: List<Word>
)

@Serializable
data class Word(
    val word: String? = null, // từ vựng
    val meaning: String? = null, // nghĩa
    val example: String? = null // Ví dụ
)

