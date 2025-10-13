//package com.example.ezwordmaster.worker
//
//import android.Manifest
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.ezwordmaster.MainActivity
//import com.example.ezwordmaster.R
//
//class NotificationWorker(
//    private val context: Context,
//    workerParams: WorkerParameters
//) : CoroutineWorker(context, workerParams) {
//
//    companion object {
//        const val CHANNEL_ID = "ezwordmaster_channel"
//        const val NOTIFICATION_ID = 1
//    }
//
//    // Thực hiện công việc khi được kích hoạt
//    override suspend fun doWork(): Result {
//        val randomWordTitle = "It's time to review!"
//        val randomWordContent = "Don't forget to practice your vocabulary today."
//
//        showNotification(randomWordTitle, randomWordContent)
//        return Result.success()
//    }
//
//    // Hiển thị thông báo
//    private fun showNotification(title: String, content: String) {
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.logo)
//            .setContentTitle(title)
//            .setContentText(content)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .build()
//
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
//        }
//    }
//}



package com.example.ezwordmaster.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ezwordmaster.MainActivity
import com.example.ezwordmaster.R

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "ezwordmaster_channel"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_NAME = "EzWordMaster Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications for vocabulary reminders"
    }

    override suspend fun doWork(): Result {
        val randomWordTitle = "📚 It's time to review!"
        val randomWordContent = "Don't forget to practice your vocabulary today. Tap to continue learning!"

        showNotification(randomWordTitle, randomWordContent)
        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        // 1. Tạo Intent để mở app khi click thông báo
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Thêm extra data nếu cần
            putExtra("from_notification", true)
            putExtra("target_fragment", "home")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Tạo action buttons (nếu cần)
        val laterIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("action", "snooze")
        }
        val laterPendingIntent = PendingIntent.getActivity(
            context,
            1,
            laterIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. Tạo large icon (logo app)
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.logo)

        // 4. Tạo small icon cho status bar (có thể dùng logo hoặc icon riêng)
        val smallIcon = R.drawable.logo

        // 5. Xây dựng thông báo hoàn chỉnh
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // Basic content
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))

            // Priority & importance
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

            // Visual enhancements
            .setLargeIcon(largeIcon)
            .setColor(0xFF6B35)
            .setColorized(true)

            // Sound & Vibration
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Âm thanh, vibration mặc định
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000)) // Pattern: wait, vibrate, wait, vibrate

            // Lights (nếu device hỗ trợ)
            .setLights(Color.BLUE, 1000, 1000) // Màu, on ms, off ms

            // Auto behavior
            .setAutoCancel(true)
            .setOnlyAlertOnce(false) // Alert mỗi lần hiển thị

            // Timing
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)

            // Actions
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_snooze,
                "Remind Later",
                laterPendingIntent
            )
            .addAction(
                R.drawable.ic_open,
                "Open App",
                pendingIntent
            )

            // Grouping (nếu có nhiều thông báo)
            .setGroup("vocabulary_group")
            .setGroupSummary(false)

            // Progress (nếu cần hiển thị tiến trình)
            // .setProgress(100, 50, false)

            // Badge (hiển thị trên icon app)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setNumber(1) // Số hiển thị trên badge

            // Visibility
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Timeout (Android 10+)
            .setTimeoutAfter(60000) // 60 seconds

            .build()

        // 6. Hiển thị thông báo với permission check
        if (hasNotificationPermission()) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Trước Android 13, permission được grant tự động
            true
        }
    }
}