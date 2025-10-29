package com.example.ezwordmaster.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    val notificationsEnabled = settingsRepository.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val notificationInterval = settingsRepository.notificationIntervalFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, 4L)

    fun onNotificationToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            // Chỉ cần gọi Repository
            settingsRepository.setNotificationsEnabled(isEnabled)
        }
    }

    fun onIntervalChanged(intervalHours: Long) {
        viewModelScope.launch {
            // Chỉ cần gọi Repository
            settingsRepository.setNotificationInterval(intervalHours)
        }
    }
}