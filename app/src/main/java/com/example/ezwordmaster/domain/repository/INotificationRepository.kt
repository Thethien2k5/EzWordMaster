package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface INotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    suspend fun getNotificationById(id: String): NotificationEntity?
    suspend fun insertNotification(notification: NotificationEntity)
    suspend fun insertAllNotifications(notifications: List<NotificationEntity>)
    suspend fun deleteNotification(notification: NotificationEntity)
    suspend fun deleteNotificationById(id: String)
    suspend fun deleteAllNotifications()
    suspend fun markAsRead(id: String)
    fun getUnreadCount(): Flow<Int>
}