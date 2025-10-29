package com.example.ezwordmaster.domain.repository

import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    val notificationsEnabledFlow: Flow<Boolean>
    val notificationIntervalFlow: Flow<Long>

    suspend fun setNotificationsEnabled(isEnabled: Boolean)
    suspend fun setNotificationInterval(intervalHours: Long)
}