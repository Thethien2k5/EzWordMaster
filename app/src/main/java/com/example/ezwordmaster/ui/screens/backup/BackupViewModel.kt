package com.example.ezwordmaster.ui.screens.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.ICloudStudyResultRepository
import com.example.ezwordmaster.domain.repository.ICloudTopicRepository
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.BackupUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý backup/restore dữ liệu
 */
class BackupViewModel(
    private val topicRepository: ITopicRepository,
    private val studyResultRepository: IStudyResultRepository,
    private val cloudTopicRepo: ICloudTopicRepository,
    private val cloudStudyResultRepo: ICloudStudyResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        loadLocalData()
    }

    /**
     * Load dữ liệu local để hiển thị trong Upload tab
     */
    private fun loadLocalData() {
        viewModelScope.launch {
            try {
                val topics = topicRepository.loadTopics()
                val results = studyResultRepository.loadStudyResults().results

                _uiState.value = _uiState.value.copy(
                    localTopics = topics,
                    localStudyResults = results,
                    // Mặc định chọn tất cả
                    selectedTopicIds = topics.mapNotNull { it.id }.toSet(),
                    selectedResultIds = results.map { it.id }.toSet()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "❌ Lỗi load dữ liệu local: ${e.message}"
                )
            }
        }
    }

    /**
     * Load dữ liệu cloud để hiển thị trong Download tab
     */
    fun loadCloudData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val topicsResult = cloudTopicRepo.loadTopics()
                val resultsResult = cloudStudyResultRepo.loadStudyResults()

                val cloudTopics = topicsResult.getOrNull() ?: emptyList()
                val cloudResults = resultsResult.getOrNull() ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cloudTopics = cloudTopics,
                    cloudStudyResults = cloudResults,
                    // Mặc định chọn tất cả dữ liệu cloud
                    selectedTopicIds = cloudTopics.mapNotNull { it.id }.toSet(),
                    selectedResultIds = cloudResults.map { it.id }.toSet()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "❌ Lỗi load dữ liệu cloud: ${e.message}"
                )
            }
        }
    }

    /**
     * Upload dữ liệu đã chọn lên Firestore
     */
    fun uploadSelectedData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                uploadProgress = 0,
                errorMessage = null,
                successMessage = null
            )

            try {
                val state = _uiState.value
                var progress = 0
                val totalSteps = 2

                // Step 1: Upload topics
                if (state.selectAllTopics || state.selectedTopicIds.isNotEmpty()) {
                    val topicsToUpload = if (state.selectAllTopics) {
                        state.localTopics
                    } else {
                        state.localTopics.filter { it.id in state.selectedTopicIds }
                    }

                    if (topicsToUpload.isNotEmpty()) {
                        val result = cloudTopicRepo.saveTopics(topicsToUpload)
                        if (result.isFailure) {
                            throw result.exceptionOrNull() ?: Exception("Lỗi upload topics")
                        }
                    }

                    progress++
                    _uiState.value = _uiState.value.copy(
                        uploadProgress = (progress * 100) / totalSteps
                    )
                }

                // Step 2: Upload study results
                if (state.selectAllResults || state.selectedResultIds.isNotEmpty()) {
                    val resultsToUpload = if (state.selectAllResults) {
                        state.localStudyResults
                    } else {
                        state.localStudyResults.filter { it.id in state.selectedResultIds }
                    }

                    if (resultsToUpload.isNotEmpty()) {
                        val result = cloudStudyResultRepo.saveStudyResults(resultsToUpload)
                        if (result.isFailure) {
                            throw result.exceptionOrNull() ?: Exception("Lỗi upload study results")
                        }
                    }

                    progress++
                    _uiState.value = _uiState.value.copy(
                        uploadProgress = (progress * 100) / totalSteps
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    uploadProgress = 100,
                    successMessage = "✅ Đã sao lưu dữ liệu thành công!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    uploadProgress = 0,
                    errorMessage = "❌ Lỗi sao lưu: ${e.message}"
                )
            }
        }
    }

    /**
     * Download dữ liệu đã chọn từ Firestore về local
     */
    fun downloadSelectedData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                downloadProgress = 0,
                errorMessage = null,
                successMessage = null
            )

            try {
                // Load cloud data nếu chưa có
                if (_uiState.value.cloudTopics.isEmpty()) {
                    loadCloudData()
                }

                val state = _uiState.value
                var progress = 0
                val totalSteps = 2

                // Step 1: Download topics
                if (state.selectAllTopics || state.selectedTopicIds.isNotEmpty()) {
                    val topicsToDownload = if (state.selectAllTopics) {
                        state.cloudTopics
                    } else {
                        state.cloudTopics.filter { it.id in state.selectedTopicIds }
                    }

                    topicsToDownload.forEach { topic ->
                        topicRepository.addOrUpdateTopic(topic)
                    }

                    progress++
                    _uiState.value = _uiState.value.copy(
                        downloadProgress = (progress * 100) / totalSteps
                    )
                }

                // Step 2: Download study results
                if (state.selectAllResults || state.selectedResultIds.isNotEmpty()) {
                    val resultsToDownload = if (state.selectAllResults) {
                        state.cloudStudyResults
                    } else {
                        state.cloudStudyResults.filter { it.id in state.selectedResultIds }
                    }

                    resultsToDownload.forEach { result ->
                        studyResultRepository.addStudyResult(result)
                    }

                    progress++
                    _uiState.value = _uiState.value.copy(
                        downloadProgress = (progress * 100) / totalSteps
                    )
                }

                // Reload local data để cập nhật UI
                loadLocalData()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    downloadProgress = 100,
                    successMessage = "✅ Đã tải xuống dữ liệu thành công!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    downloadProgress = 0,
                    errorMessage = "❌ Lỗi tải xuống: ${e.message}"
                )
            }
        }
    }

    // ========== Selection Helpers ==========

    /**
     * Toggle chọn/bỏ chọn một topic
     */
    fun toggleTopicSelection(topicId: String) {
        val current = _uiState.value.selectedTopicIds
        _uiState.value = _uiState.value.copy(
            selectedTopicIds = if (topicId in current) {
                current - topicId
            } else {
                current + topicId
            },
            selectAllTopics = false
        )
    }

    /**
     * Toggle chọn/bỏ chọn một study result
     */
    fun toggleResultSelection(resultId: String) {
        val current = _uiState.value.selectedResultIds
        _uiState.value = _uiState.value.copy(
            selectedResultIds = if (resultId in current) {
                current - resultId
            } else {
                current + resultId
            },
            selectAllResults = false
        )
    }

    /**
     * Toggle "Chọn tất cả topics"
     */
    fun toggleSelectAllTopics() {
        val newValue = !_uiState.value.selectAllTopics
        val topics = _uiState.value.localTopics.takeIf { it.isNotEmpty() }
            ?: _uiState.value.cloudTopics

        _uiState.value = _uiState.value.copy(
            selectAllTopics = newValue,
            selectedTopicIds = if (newValue) {
                topics.mapNotNull { it.id }.toSet()
            } else {
                emptySet()
            }
        )
    }

    /**
     * Toggle "Chọn tất cả study results"
     */
    fun toggleSelectAllResults() {
        val newValue = !_uiState.value.selectAllResults
        val results = _uiState.value.localStudyResults.takeIf { it.isNotEmpty() }
            ?: _uiState.value.cloudStudyResults

        _uiState.value = _uiState.value.copy(
            selectAllResults = newValue,
            selectedResultIds = if (newValue) {
                results.map { it.id }.toSet()
            } else {
                emptySet()
            }
        )
    }

    /**
     * Clear error/success messages sau khi hiển thị
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}