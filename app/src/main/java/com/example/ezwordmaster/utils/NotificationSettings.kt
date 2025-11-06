package com.example.ezwordmaster.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.ezwordmaster.worker.NotificationWorker

// yêu cầu quyền thông báo khi mở app
class NotificationSettings : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NAME = "EzWordMaster"
            val DESCRIPTIONTEXT = "Thông báo học từ mới" // Mô tả channel
            val IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT // Mức độ ưu tiên
            val CHANNEL =
                NotificationChannel(
                    NotificationWorker.Companion.CHANNEL_ID,
                    NAME,
                    IMPORTANCE
                ).apply {
                    description = DESCRIPTIONTEXT
                }
            val NOTIFICATIONMANAGER: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            NOTIFICATIONMANAGER.createNotificationChannel(CHANNEL)
        }
    }
}