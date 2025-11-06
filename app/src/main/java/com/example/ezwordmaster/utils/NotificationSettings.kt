package com.example.ezwordmaster.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.ezwordmaster.ui.AppContainer // <-- SỬA 1: IMPORT APPCONTAINER

class NotificationSettings : Application() {

    // ############ SỬA 2: Thêm biến AppContainer ############
    // Giờ đây Service có thể truy cập vào biến này
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        // ############ SỬA 3: Khởi tạo AppContainer ############
        // Khởi tạo container của bạn ngay khi app chạy
        appContainer = AppContainer(this)

        // (Code tạo channel của bạn vẫn giữ nguyên)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

            notificationManager.createNotificationChannels(listOf(learningChannel, systemChannel, fcmChannel))
        }
    }
}