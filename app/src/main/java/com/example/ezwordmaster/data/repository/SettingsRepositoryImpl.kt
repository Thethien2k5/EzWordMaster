package com.example.ezwordmaster.data.repository

import android.content.Context
import com.example.ezwordmaster.data.local.SettingsDataStore
import com.example.ezwordmaster.domain.repository.ISettingsRepository
import com.example.ezwordmaster.worker.NotificationScheduler
import kotlinx.coroutines.flow.first

class SettingsRepositoryImpl(private val context: Context) : ISettingsRepository {

    private val settingsDataStore = SettingsDataStore(context)

    override val notificationsEnabledFlow = settingsDataStore.notificationsEnabledFlow
    override val notificationIntervalFlow = settingsDataStore.notificationIntervalFlow

    override suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        settingsDataStore.setNotificationsEnabled(isEnabled)
        // Lấy giá trị interval hiện tại để lập lịch lại nếu cần
        val currentInterval = notificationIntervalFlow.first()
        if (isEnabled) {
            NotificationScheduler.scheduleReminder(context, currentInterval)
        } else {
            NotificationScheduler.cancelReminder(context)
        }
    }

    override suspend fun setNotificationInterval(intervalHours: Long) {
        settingsDataStore.setNotificationInterval(intervalHours)
        // Lấy giá trị isEnabled hiện tại
        val isEnabled = notificationsEnabledFlow.first()
        if (isEnabled) {
            NotificationScheduler.scheduleReminder(context, intervalHours)
        }
    }
}