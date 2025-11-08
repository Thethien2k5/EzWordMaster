package com.example.ezwordmaster.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.ezwordmaster.ui.AppContainer

// yêu cầu quyền thông báo khi mở app
class NotificationSettings : Application() {
    // Giờ đây Service có thể truy cập vào biến này
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // Khởi tạo container của bạn ngay khi app chạy
        appContainer = AppContainer(this)

        // (Code tạo channel của bạn vẫn giữ nguyên)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Channel for learning reminders
            val learningChannel = NotificationChannel(
                "learning_reminders",
                "Learning Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for learning reminders and study sessions"
            }

            // Channel for system notifications
            val systemChannel = NotificationChannel(
                "system_notifications",
                "System Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General system notifications"
            }

            // Channel của tôi (để FCM hoạt động song song)
            val fcmChannel = NotificationChannel(
                "ezwordmaster_channel",
                "EzWordMaster Thông Báo",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannels(
                listOf(
                    learningChannel,
                    systemChannel,
                    fcmChannel
                )
            )
        }
    }
}