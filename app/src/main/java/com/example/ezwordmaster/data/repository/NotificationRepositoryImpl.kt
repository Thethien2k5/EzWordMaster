package com.example.ezwordmaster.data.repository


import com.example.ezwordmaster.data.local.dao.NotificationDao
import com.example.ezwordmaster.data.local.entity.NotificationEntity
import com.example.ezwordmaster.domain.repository.INotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : INotificationRepository {

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun getNotificationById(id: String): NotificationEntity? {
        return notificationDao.getNotificationById(id)
    }

    override suspend fun insertNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    override suspend fun insertAllNotifications(notifications: List<NotificationEntity>) {
        notificationDao.insertAllNotifications(notifications)
    }

    override suspend fun deleteNotification(notification: NotificationEntity) {
        notificationDao.deleteNotification(notification)
    }

    override suspend fun deleteNotificationById(id: String) {
        notificationDao.deleteNotificationById(id)
    }

    override suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    override fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }
}