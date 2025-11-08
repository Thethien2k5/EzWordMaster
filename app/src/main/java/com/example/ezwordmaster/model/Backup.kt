package com.example.ezwordmaster.model

/**
 * UI State cho BackupScreen
 */
data class BackupUiState(
    val isLoading: Boolean = false,
    val uploadProgress: Int = 0,
    val downloadProgress: Int = 0,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Dữ liệu local (từ Room)
    val localTopics: List<Topic> = emptyList(),
    val localStudyResults: List<StudyResult> = emptyList(),

    // Dữ liệu cloud (từ Firestore)
    val cloudTopics: List<Topic> = emptyList(),
    val cloudStudyResults: List<StudyResult> = emptyList(),

    // Selection state
    val selectedTopicIds: Set<String> = emptySet(),
    val selectedResultIds: Set<String> = emptySet(),

    // Flags cho "Chọn tất cả"
    val selectAllTopics: Boolean = true,
    val selectAllResults: Boolean = true
)
