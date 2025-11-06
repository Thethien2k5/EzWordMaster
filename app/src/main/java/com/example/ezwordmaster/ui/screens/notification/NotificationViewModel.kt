package com.example.ezwordmaster.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.INotificationRepository
import com.example.ezwordmaster.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class NotificationViewModel(
    private val notificationRepository: INotificationRepository
) : ViewModel() {

    val notifications = notificationRepository.getAllNotifications()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val unreadCount = notificationRepository.getUnreadCount()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    fun deleteNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notification)
        }
    }

    fun deleteNotificationById(id: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotificationById(id)
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            notificationRepository.deleteAllNotifications()
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    // For testing - add sample notifications
    fun addSampleNotification() {
        viewModelScope.launch {
            val notification = NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Thông báo mới",
                description = "Đây là thông báo mẫu từ hệ thống",
                timestamp = Date(),
                type = "system"
            )
            notificationRepository.insertNotification(notification)
        }
    }
}