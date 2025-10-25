package com.example.ezwordmaster.model

data class FlipCardUiState(
    val TOPIC: Topic? = null,
    val CARDS: List<CardItem> = emptyList(),
    val FLIPPED_CARDS: List<CardItem> = emptyList(), // Tối đa 2 thẻ đang được lật
    val MATCHED_PAIRS: Int = 0,
    val IS_COMPLETED: Boolean = false,
    val WRONG_PAIR_IDS: Set<String> = emptySet(), // Lưu pairId của các cặp lật sai
    val CORRECT_PAIR_IDS: Set<String> = emptySet(), // Lưu pairId của các cặp lật đúng
    val START_TIME: Long = 0L
)