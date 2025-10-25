package com.example.ezwordmaster.ui.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Quản lý PracticeScreen, WordPracticeScreen, WordSelectionScreen
class PracticeViewModel(private val topicRepository: ITopicRepository) : ViewModel() {

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _selectedTopic = MutableStateFlow<Topic?>(null)
    val selectedTopic: StateFlow<Topic?> = _selectedTopic.asStateFlow()

    private val _selectedWords = MutableStateFlow<Set<String>>(emptySet())
    val selectedWords: StateFlow<Set<String>> = _selectedWords.asStateFlow()

    fun loadTopics() {
        viewModelScope.launch {
            _topics.value = topicRepository.loadTopics()
        }
    }

    fun loadTopicById(id: String) {
        viewModelScope.launch {
            _selectedTopic.value = topicRepository.getTopicById(id)
        }
    }

    fun toggleWordSelection(word: Word) {
        val wordText = word.word ?: return
        val currentSelection = _selectedWords.value
        _selectedWords.value = if (currentSelection.contains(word.word)) {
            currentSelection - word.word
        } else {
            currentSelection + word.word
        }
    }

    fun toggleSelectAll() {
        val allWords = _selectedTopic.value?.words?.mapNotNull { it.word }?.toSet() ?: emptySet()
        if (_selectedWords.value.size == allWords.size) {
            _selectedWords.value = emptySet() // Bỏ chọn tất cả
        } else {
            _selectedWords.value = allWords // Chọn tất cả
        }
    }
}