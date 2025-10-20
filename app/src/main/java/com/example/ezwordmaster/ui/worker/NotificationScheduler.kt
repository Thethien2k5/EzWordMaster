package com.example.ezwordmaster.worker

import android.content.Context
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    fun scheduleDailyReminder(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Tính toán thời gian để chạy vào 9h sáng mỗi ngày
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        // Nếu 9h sáng hôm nay đã qua, lên lịch cho ngày mai
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder_work",
            ExistingPeriodicWorkPolicy.KEEP, // Giữ lịch cũ nếu đã có
            workRequest
        )
    }
}