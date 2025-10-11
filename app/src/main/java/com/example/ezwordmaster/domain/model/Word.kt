package com.example.ezwordmaster.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val word: String? = null, // từ vựng
    val meaning: String? = null, // nghĩa
    val example: String? = null // Ví dụ
)