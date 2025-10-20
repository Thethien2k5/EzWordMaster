// DÁN VÀ THAY THẾ TOÀN BỘ NỘI DUNG FILE NÀY
package com.example.ezwordmaster.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Lấy trạng thái Chế độ tối từ DataStore
    val isDarkMode = settingsDataStore.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    // Lấy trạng thái Bật/tắt thông báo từ DataStore
    val areNotificationsEnabled = settingsDataStore.notificationsEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true
        )

    // Hàm này được gọi từ SettingsScreen
    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(isDark)
        }
    }

    // Hàm này được gọi từ SettingsScreen
    fun toggleNotifications(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationsEnabled(isEnabled)
        }
    }
}