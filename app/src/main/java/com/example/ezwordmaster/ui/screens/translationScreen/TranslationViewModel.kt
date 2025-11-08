package com.example.ezwordmaster.ui.screens.translationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.local.entity.TranslationHistoryEntity
import com.example.ezwordmaster.domain.repository.ITranslationRepository
import com.example.ezwordmaster.model.TranslationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TranslationViewModel(
    private val translationRepository: ITranslationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    val translationHistory = translationRepository.getAllTranslationHistory()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /**
     * Dịch văn bản dựa trên state hiện tại (inputText, sourceLang, targetLang)
     * Không cần tham số `text` vì nó đã có trong _uiState.value.inputText
     */
    fun translateText() {
        // Lấy text từ state nội bộ
        val text = _uiState.value.inputText
        if (text.isBlank()) return

        val source = _uiState.value.sourceLang
        val target = _uiState.value.targetLang

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                // Dùng source và target từ state
                val result = translationRepository.translateText(text, source, target)
                _uiState.value = _uiState.value.copy(
                    currentTranslation = result,
                    isLoading = false,
                    error = null,
                    inputText = text // Đồng bộ lại inputText
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Lỗi dịch: ${e.message}"
                )
            }
        }
    }

    /**
     * Hàm mới để hoán đổi ngôn ngữ
     */
    fun swapLanguages() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            sourceLang = currentState.targetLang,
            targetLang = currentState.sourceLang
        )
    }

    /**
     * Cập nhật inputText trong state khi người dùng gõ
     */
    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun clearTranslation() {
        _uiState.value = TranslationUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun deleteTranslationHistory(translation: TranslationHistoryEntity) {
        viewModelScope.launch {
            translationRepository.deleteTranslation(translation)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            translationRepository.deleteAllTranslationHistory()
        }
    }

    /**
     * Dọn dẹp ML Kit translator khi ViewModel bị hủy
     */
    override fun onCleared() {
        super.onCleared()
        translationRepository.cleanup()
    }
}