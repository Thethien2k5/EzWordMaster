package com.example.ezwordmaster.ui.screens.regime

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

    private val _TOPICS = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _TOPICS.asStateFlow()

    private val _SELECTEDTOPIC = MutableStateFlow<Topic?>(null)
    val selectedTopic: StateFlow<Topic?> = _SELECTEDTOPIC.asStateFlow()

    private val _SELECTEDWORDS = MutableStateFlow<Set<String>>(emptySet())
    val selectedWords: StateFlow<Set<String>> = _SELECTEDWORDS.asStateFlow()

    fun loadTopics() {
        viewModelScope.launch {
            _TOPICS.value = topicRepository.loadTopics()
        }
    }

    fun loadTopicById(id: String) {
        viewModelScope.launch {
            _SELECTEDTOPIC.value = topicRepository.getTopicById(id)
        }
    }

    fun toggleWordSelection(word: Word) {
        word.word ?: return
        val currentSelection = _SELECTEDWORDS.value
        _SELECTEDWORDS.value = if (currentSelection.contains(word.word)) {
            currentSelection - word.word
        } else {
            currentSelection + word.word
        }
    }

    fun toggleSelectAll() {
        val allWords = _SELECTEDTOPIC.value?.words?.mapNotNull { it.word }?.toSet() ?: emptySet()
        if (_SELECTEDWORDS.value.size == allWords.size) {
            _SELECTEDWORDS.value = emptySet() // Bỏ chọn tất cả
        } else {
            _SELECTEDWORDS.value = allWords // Chọn tất cả
        }
    }
}