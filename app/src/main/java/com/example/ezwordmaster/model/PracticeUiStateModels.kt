package com.example.ezwordmaster.model

//trạng thái giao diện của các màn hình ôn tập.
data class FlashcardUiState(
    val topic: Topic? = null,
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val knownWords: Int = 0,
    val learningWords: Int = 0,
    val isFlipped: Boolean = false,
    val isCompleted: Boolean = false,
    val startTime: Long = 0L,
    val isProcessing: Boolean = false
)

data class FlipCardUiState(
    val topic: Topic? = null,
    val cards: List<CardItem> = emptyList(),
    val flippedCards: List<CardItem> = emptyList(),
    val matchedPairs: Int = 0,
    val isCompleted: Boolean = false,
    val wrongCardIds: Set<String> = emptySet(),
    val correctCardIds: Set<String> = emptySet(),
    val isProcessing: Boolean = false,
    val startTime: Long = 0L
)

data class CardItem(
    val id: String,
    val text: String,
    val isWord: Boolean,
    val pairId: String,
    val isMatched: Boolean = false
)