package com.example.ezwordmaster.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.datastore.SettingsDataStore
import com.example.ezwordmaster.worker.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

    val notificationsEnabled = settingsDataStore.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val notificationInterval = settingsDataStore.notificationIntervalFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsDataStore.DEFAULT_INTERVAL)

    fun onNotificationToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationsEnabled(isEnabled)
            if (isEnabled) {
                NotificationScheduler.scheduleReminder(getApplication(), notificationInterval.value)
            } else {
                NotificationScheduler.cancelReminder(getApplication())
            }
        }
    }

    fun onIntervalChanged(intervalHours: Long) {
        viewModelScope.launch {
            settingsDataStore.setNotificationInterval(intervalHours)
            if (notificationsEnabled.value) {
                NotificationScheduler.scheduleReminder(getApplication(), intervalHours)
            }
        }
    }
}