package com.example.ezwordmaster.domain.model
import kotlinx.serialization.Serializable

@Serializable // cho phép chuyển đổi giữa kotlin và json
data class Topic(
    val id: String? = null,
    val name: String? = null,
    val words: List<Word>
)