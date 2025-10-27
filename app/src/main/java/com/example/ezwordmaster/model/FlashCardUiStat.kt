package com.example.ezwordmaster.model

data class FlashCardUiState(
    val TOPIC: Topic? = null,
    val WORDS: List<Word> = emptyList(),
    val CURRENTINDEX: Int = 0,
    val KNOWNWORDS: Int = 0,
    val LEARNINGWORDS: Int = 0,
    val ISFLIPPED: Boolean = false,
    val ISCOMPLETED: Boolean = false,
    val STARTTIME: Long = 0L,
    val IS_PROCESSING: Boolean = false
)