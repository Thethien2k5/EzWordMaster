package com.example.ezwordmaster.domain.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.remote.DictionaryApi
import com.example.ezwordmaster.domain.model.WordInfoDto
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// ✅ State mới chứa cả thông tin chi tiết và bản dịch
data class TranslateUiState(
    val isLoading: Boolean = false,
    val searchInput: String = "",
    val wordInfo: WordInfoDto? = null,
    val translatedMeanings: Map<String, String> = emptyMap(), // Map [English Definition -> Vietnamese Translation]
    val error: String? = null
)

class TranslateViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TranslateUiState())
    val uiState = _uiState.asStateFlow()

    // --- Cỗ máy 1: Dictionary API qua Retrofit ---
    private val dictionaryApi: DictionaryApi by lazy {
        Retrofit.Builder()
            .baseUrl(DictionaryApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApi::class.java)
    }

    // --- Cỗ máy 2: ML Kit Translator ---
    private val translatorOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
        .build()
    private val translator = Translation.getClient(translatorOptions)

    fun onSearchInputChange(input: String) {
        _uiState.update { it.copy(searchInput = input) }
    }

    fun searchWord() {
        val wordToSearch = _uiState.value.searchInput.trim()
        if (wordToSearch.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, wordInfo = null, translatedMeanings = emptyMap()) }

            // BƯỚC 1: LẤY THÔNG TIN CHI TIẾT BẰNG TIẾNG ANH
            try {
                val wordInfoResult = dictionaryApi.getWordInfo(wordToSearch).firstOrNull()
                if (wordInfoResult != null) {
                    _uiState.update { it.copy(wordInfo = wordInfoResult) }
                    // BƯỚC 2: DỊCH CÁC ĐỊNH NGHĨA SANG TIẾNG VIỆT
                    translateDefinitions(wordInfoResult)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không tìm thấy từ này.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Lỗi mạng hoặc từ không tồn tại.") }
            }
        }
    }

    private suspend fun translateDefinitions(wordInfo: WordInfoDto) {
        // Đảm bảo model dịch đã được tải
        val isModelDownloaded = suspendCoroutine { continuation ->
            translator.downloadModelIfNeeded()
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { continuation.resume(false) }
        }

        if (!isModelDownloaded) {
            _uiState.update { it.copy(isLoading = false, error = "Không thể tải gói ngôn ngữ.") }
            return
        }

        // Lấy tất cả định nghĩa tiếng Anh ra
        val definitionsToTranslate = wordInfo.meanings.flatMap { it.definitions }.map { it.definition }
        val translatedMap = mutableMapOf<String, String>()

        // Dịch từng định nghĩa
        for (def in definitionsToTranslate) {
            val translatedDef = suspendCoroutine { continuation ->
                translator.translate(def)
                    .addOnSuccessListener { result -> continuation.resume(result) }
                    .addOnFailureListener { continuation.resume("Bản dịch lỗi") }
            }
            translatedMap[def] = translatedDef
        }

        // Cập nhật state với bản đồ dịch thuật và kết thúc loading
        _uiState.update { it.copy(isLoading = false, translatedMeanings = translatedMap) }
    }


    override fun onCleared() {
        super.onCleared()
        translator.close()
    }
}