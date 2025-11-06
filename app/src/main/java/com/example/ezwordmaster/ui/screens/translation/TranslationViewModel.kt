package com.example.ezwordmaster.ui.screens.translation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.ITranslationRepository
import com.example.ezwordmaster.data.local.entity.TranslationHistoryEntity
import com.example.ezwordmaster.data.remote.model.DetailedTranslationResult
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
     * SỬA LỖI:
     * Hàm này được gọi từ màn hình "Anh - Việt",
     * vì vậy chúng ta hardcode sourceLang = "en" và targetLang = "vi".
     * Chúng ta không dùng "auto" nữa để tránh lỗi.
     */
    fun translateText(text: String) { // Xóa tham số source/target
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                // Hardcode "en" và "vi" ngay tại đây
                val result = translationRepository.translateText(text, "en", "vi")
                _uiState.value = _uiState.value.copy(
                    currentTranslation = result,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                // Lỗi này chủ yếu xảy ra khi ML Kit tải model thất bại
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Lỗi dịch: ${e.message}"
                )
            }
        }
    }

    fun clearTranslation() {
        _uiState.value = TranslationUiState()
    }

    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
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
}

data class TranslationUiState(
    val inputText: String = "",
    val currentTranslation: DetailedTranslationResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
