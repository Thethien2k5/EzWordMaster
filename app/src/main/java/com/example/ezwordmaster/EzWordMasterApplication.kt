package com.example.ezwordmaster

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.ezwordmaster.worker.NotificationWorker

// yêu cầu quyền thông báo khi mở app
class EzWordMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "EzWordMaster"
            val descriptionText = "Thông báo học từ mới" // Mô tả channel
            val importance = NotificationManager.IMPORTANCE_DEFAULT // Mức độ ưu tiên
            val channel = NotificationChannel(NotificationWorker.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}