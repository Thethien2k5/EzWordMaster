package com.example.ezwordmaster.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Mô tả 1 câu hỏi quiz đơn giản.
 * - options: danh sách lựa chọn (4 mục cho dạng trắc nghiệm)
 * - answer: đáp án đúng (so sánh theo chữ, không phân biệt hoa thường)
 */
@Serializable
data class QuizQuestion(
    @SerialName("question") val question: String,
    @SerialName("options") val options: List<String>,
    @SerialName("answer") val answer: String
)


