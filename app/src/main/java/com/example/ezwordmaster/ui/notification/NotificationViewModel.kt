package com.example.ezwordmaster.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.model.NotificationHistoryItem
import com.example.ezwordmaster.data.repository.NotificationHistoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val historyManager: NotificationHistoryManager
) : ViewModel() {

    val notifications = historyManager.historyFlow

    fun deleteNotification(item: NotificationHistoryItem) {
        viewModelScope.launch {
            historyManager.deleteNotification(item)
        }
    }
}