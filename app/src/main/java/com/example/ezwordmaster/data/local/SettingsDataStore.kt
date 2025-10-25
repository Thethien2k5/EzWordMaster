package com.example.ezwordmaster.data.local
// Lưu trạng cài đặt tắt/ bật thông báo
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_INTERVAL_KEY = longPreferencesKey("notification_interval")
        const val DEFAULT_INTERVAL = 4L
    }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
    }

    val notificationIntervalFlow: Flow<Long> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_INTERVAL_KEY] ?: DEFAULT_INTERVAL
    }

    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = isEnabled
        }
    }

    suspend fun setNotificationInterval(intervalHours: Long) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_INTERVAL_KEY] = intervalHours
        }
    }
}