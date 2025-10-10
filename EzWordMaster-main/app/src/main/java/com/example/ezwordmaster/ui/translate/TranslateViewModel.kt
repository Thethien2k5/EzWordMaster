package com.example.ezwordmaster.ui.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslateViewModel : ViewModel() {
    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText = _translatedText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun performTranslation() {
        if (_inputText.value.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            // TODO: Gọi đến Repository để thực hiện dịch
            // Ví dụ: val result = repository.translate(_inputText.value)
            // Giả lập kết quả
            kotlinx.coroutines.delay(1000)
            _translatedText.value = "Đây là kết quả dịch cho: '${_inputText.value}'"
            _isLoading.value = false
        }
    }
}