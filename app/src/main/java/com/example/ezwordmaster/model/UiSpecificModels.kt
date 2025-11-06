package com.example.ezwordmaster.model

enum class FilterSortType {
    ALL,
    Z_TO_A,
    WORD_COUNT,
}

enum class SwipeDirection {
    LEFT, RIGHT
}

enum class MainTab {
    MANAGEMENT, PRACTICE, SETTINGS,TRANSLATION
}

data class HelpItem(
    val id: Int,
    val question: String,
    val answer: String
)

data class HelpUiState(
    val helpItems: List<HelpItem> = emptyList(),
    val selectedItem: HelpItem? = null
)