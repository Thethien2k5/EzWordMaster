package com.example.ezwordmaster.data.repository

import com.example.ezwordmaster.data.local.NotificationDao
import com.example.ezwordmaster.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getAllNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    suspend fun insert(notification: NotificationEntity) = notificationDao.insert(notification)

    suspend fun delete(notification: NotificationEntity) = notificationDao.delete(notification)
}