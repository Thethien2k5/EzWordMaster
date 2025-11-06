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
import kotlinx.coroutines.flow.debounce // <-- IMPORT MỚI
import kotlinx.coroutines.flow.distinctUntilChanged // <-- IMPORT MỚI
import kotlinx.coroutines.flow.map // <-- IMPORT MỚI
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update // <-- IMPORT MỚI
import kotlinx.coroutines.launch

class TranslationViewModel(
    private val translationRepository: ITranslationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    // --- CẢI TIẾN 2: Thêm State cho tráo đổi ngôn ngữ ---
    private val _isEnToVi = MutableStateFlow(true)
    val isEnToVi: StateFlow<Boolean> = _isEnToVi.asStateFlow()

    val translationHistory = translationRepository.getAllTranslationHistory()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // --- CẢI TIẾN 1: Logic "Debounce" (Tự động dịch) ---
    init {
        viewModelScope.launch {
            _uiState
                .map { it.inputText } // Chỉ lấy text
                .distinctUntilChanged() // Chỉ khi text thay đổi
                .debounce(500L) // Chờ 500ms sau khi ngừng gõ
                .collect { text ->
                    // Chỉ dịch khi text không rỗng
                    if (text.isNotBlank()) {
                        translateText(text)
                    } else {
                        // Nếu text rỗng, xóa kết quả cũ
                        _uiState.update { it.copy(currentTranslation = null, error = null) }
                    }
                }
        }
    }

    // Sửa lại hàm này, nó sẽ được gọi tự động bởi 'debounce'
    private fun translateText(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Lấy ngôn ngữ từ State
            val sourceLang = if (_isEnToVi.value) "en" else "vi"
            val targetLang = if (_isEnToVi.value) "vi" else "en"

            try {
                // Hardcode "en" và "vi" ngay tại đây
                val result = translationRepository.translateText(text, sourceLang, targetLang)
                _uiState.update {
                    it.copy(
                        currentTranslation = result,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Lỗi dịch: ${e.message}"
                    )
                }
            }
        }
    }

    // --- CẢI TIẾN 2: Hàm tráo đổi ngôn ngữ ---
    fun swapLanguage() {
        _isEnToVi.update { !_isEnToVi.value }
        // Khi tráo đổi, ta dịch lại từ đang nhập
        if (_uiState.value.inputText.isNotBlank()) {
            translateText(_uiState.value.inputText)
        }
    }

    fun clearTranslation() {
        _uiState.value = TranslationUiState()
    }

    // Hàm này giờ chỉ cập nhật text, 'debounce' sẽ lo phần còn lại
    fun setInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
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