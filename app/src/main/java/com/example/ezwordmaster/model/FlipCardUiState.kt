package com.example.ezwordmaster.model

data class FlipCardUiState(
    val TOPIC: Topic? = null,
    val CARDS: List<CardItem> = emptyList(),
    val FLIPPED_CARDS: List<CardItem> = emptyList(), // Tối đa 2 thẻ đang được lật
    val MATCHED_PAIRS: Int = 0,
    val IS_COMPLETED: Boolean = false,
    val WRONG_CARD_IDS: Set<String> = emptySet(), // Lưu ID của các thẻ lật sai để hiển thị hiệu ứng
    val CORRECT_CARD_IDS: Set<String> = emptySet(), // Lưu ID của các thẻ lật đúng để hiển thị hiệu ứng
    val IS_PROCESSING: Boolean = false, // Cờ chống nhấn nhanh khi đang xử lý animation
    val START_TIME: Long = 0L
)