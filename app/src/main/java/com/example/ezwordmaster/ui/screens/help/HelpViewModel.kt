package com.example.ezwordmaster.ui.screens.help

import androidx.lifecycle.ViewModel
import com.example.ezwordmaster.model.HelpItem
import com.example.ezwordmaster.model.HelpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class HelpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelpUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHelpItems()
    }

    private fun loadHelpItems() {
        // Đây là nơi bạn sẽ lấy dữ liệu thật, ví dụ từ database hoặc network
        // Hiện tại chúng ta sẽ dùng dữ liệu giả (mock data)
        val items = listOf(
            HelpItem(
                1,
                "Why should I use this app?",
                "This app helps you learn vocabulary effectively through interactive quizzes and spaced repetition, making learning fun and efficient."
            ),
            HelpItem(
                2,
                "How do I add a new word?",
                "Go to the 'Topics' section, select a topic, and use the '+' button to add a new word with its meaning and an example."
            ),
            HelpItem(
                3,
                "Can I track my progress?",
                "Yes! The 'Learning History' screen shows your recent quiz scores, words learned, and time spent studying."
            ),
            HelpItem(
                4,
                "What are topics?",
                "Topics are categories for your words, like 'Animals', 'Business', or 'Travel'. This helps you organize your vocabulary."
            ),
            HelpItem(
                5,
                "How do notifications work?",
                "The app can send you random words or reminders to study. You can configure this in the Settings menu."
            )
        )
        _uiState.update { it.copy(helpItems = items) }
    }

    fun onQuestionClicked(item: HelpItem) {
        _uiState.update { currentState ->
            // Nếu câu hỏi đã được chọn, bỏ chọn nó. Nếu chưa, chọn nó.
            val newSelectedItem = if (currentState.selectedItem == item) null else item
            currentState.copy(selectedItem = newSelectedItem)
        }
    }
}

