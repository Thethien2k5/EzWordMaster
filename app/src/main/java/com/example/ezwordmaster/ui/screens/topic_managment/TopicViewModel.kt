package com.example.ezwordmaster.ui.screens.topic_managment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel chịu trách nhiệm xử lý logic nghiệp vụ cho các màn hình quản lý chủ đề.
 * Nó giao tiếp với ITopicRepository để lấy và cập nhật dữ liệu,
 * sau đó cung cấp dữ liệu này cho UI thông qua StateFlow.
 */
class TopicViewModel(
    private val TOPICREPOSITORY: ITopicRepository // Nhận 'bản hợp đồng', không phải 'người thực hiện'
) : ViewModel() {

    // StateFlow để chứa danh sách tất cả các chủ đề cho màn hình chính
    private val _TOPICS = MutableStateFlow<List<Topic>>(emptyList())
    private val _TOASTMESSAGE = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _TOASTMESSAGE.asStateFlow()
    val topics: StateFlow<List<Topic>> = _TOPICS.asStateFlow()

    // StateFlow để chứa thông tin của một chủ đề cụ thể cho màn hình chỉnh sửa
    private val _SELECTEDTOPIC = MutableStateFlow<Topic?>(null)
    val selectedTopic: StateFlow<Topic?> = _SELECTEDTOPIC.asStateFlow()

    fun clearToastMessage() {
        _TOASTMESSAGE.value = null
    }

    //Tải toàn bộ danh sách chủ đề từ repository và cập nhật StateFlow.
    fun loadAllTopics() {
        viewModelScope.launch {
            _TOPICS.value = TOPICREPOSITORY.loadTopics()
        }
    }

    //Tải thông tin của một chủ đề cụ thể bằng ID.
    fun loadTopicById(id: String) {
        viewModelScope.launch {
            _SELECTEDTOPIC.value = TOPICREPOSITORY.getTopicById(id)
        }
    }

    //Thêm một chủ đề mới chỉ với tên.
    fun addTopic(name: String) {
        viewModelScope.launch {
            if (TOPICREPOSITORY.topicNameExists(name)) {
                _TOASTMESSAGE.value = "Chủ đề '$name' đã tồn tại."
                return@launch
            }
            TOPICREPOSITORY.addNameTopic(name)
            loadAllTopics()
            _TOASTMESSAGE.value = "Đã thêm chủ đề '$name'."
        }
    }


    //Cập nhật tên của một chủ đề đã có.
    fun updateTopicName(id: String, newName: String) {
        viewModelScope.launch {
            if (TOPICREPOSITORY.topicNameExists(newName)) {
                _TOASTMESSAGE.value = "Chủ đề '$newName' đã tồn tại."
                return@launch
            }
            TOPICREPOSITORY.updateTopicName(id, newName)
            loadTopicById(id) // Tải lại topic hiện tại để cập nhật tên
        }
    }

    //Xóa một chủ đề bằng ID.
    fun deleteTopicById(id: String) {
        viewModelScope.launch {
            TOPICREPOSITORY.deleteTopicById(id)
        }
    }

    //Thêm một từ mới vào một chủ đề
    fun addWordToTopic(topicId: String, word: Word) {
        viewModelScope.launch {
            if (TOPICREPOSITORY.wordExistsInTopic(topicId, word)) {
                _TOASTMESSAGE.value = "Từ '${word.word}' đã tồn tại trong chủ đề này."
                return@launch
            }
            TOPICREPOSITORY.addWordToTopic(topicId, word)
            loadTopicById(topicId)
            _TOASTMESSAGE.value = "Đã thêm từ '${word.word}'."
        }
    }

    //Cập nhật một từ đã có trong chủ đề.
    fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word) {
        viewModelScope.launch {
            if (TOPICREPOSITORY.wordExistsInTopic(topicId, newWord)) {
                _TOASTMESSAGE.value = "Từ '${newWord.word}' đã tồn tại trong chủ đề này."
                return@launch
            }
            TOPICREPOSITORY.updateWordInTopic(topicId, oldWord, newWord)
            loadTopicById(topicId)
        }
    }

    //Xóa một từ khỏi chủ đề.
    fun deleteWordFromTopic(topicId: String, word: Word) {
        viewModelScope.launch {
            TOPICREPOSITORY.deleteWordFromTopic(topicId, word)
            loadTopicById(topicId)
        }
    }
}