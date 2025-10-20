package com.example.ezwordmaster.data.repository

import android.content.Context
import com.example.ezwordmaster.data.model.NotificationHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHistoryManager @Inject constructor(
    private val context: Context
) {
    private val historyFile = File(context.filesDir, "notification_history.json")
    private val json = Json { isLenient = true; prettyPrint = true }

    private val _historyFlow = MutableStateFlow<List<NotificationHistoryItem>>(emptyList())
    val historyFlow: StateFlow<List<NotificationHistoryItem>> = _historyFlow.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        if (historyFile.exists()) {
            val fileContent = historyFile.readText()
            if (fileContent.isNotBlank()) {
                _historyFlow.value = json.decodeFromString<List<NotificationHistoryItem>>(fileContent).sortedByDescending { it.timestamp }
            }
        }
    }

    fun addNotificationToHistory(title: String, content: String) {
        val newItem = NotificationHistoryItem(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        val currentList = _historyFlow.value.toMutableList()
        currentList.add(0, newItem) // Thêm vào đầu danh sách
        _historyFlow.value = currentList

        saveHistoryToFile()
    }

    fun deleteNotification(item: NotificationHistoryItem) {
        val currentList = _historyFlow.value.toMutableList()
        currentList.removeAll { it.id == item.id }
        _historyFlow.value = currentList

        saveHistoryToFile()
    }

    private fun saveHistoryToFile() {
        val jsonString = json.encodeToString(_historyFlow.value)
        historyFile.writeText(jsonString)
    }
}