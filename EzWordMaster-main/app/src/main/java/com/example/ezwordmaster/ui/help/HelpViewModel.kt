package com.example.ezwordmaster.ui.help

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Dùng data class để lưu cặp Hỏi-Đáp
data class HelpItem(val question: String, val answer: String)

class HelpViewModel : ViewModel() {
    private val _helpItems = MutableStateFlow<List<HelpItem>>(
        listOf(
            HelpItem("Why to use it?", "This app helps you learn new words easily..."),
            HelpItem("Why is it good?", "Because it uses spaced repetition and interactive quizzes..."),
            // Thêm các câu hỏi khác ở đây
        )
    )
    val helpItems: StateFlow<List<HelpItem>> = _helpItems.asStateFlow()
}