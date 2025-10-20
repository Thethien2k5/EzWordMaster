// DÁN VÀ THAY THẾ TOÀN BỘ NỘI DUNG FILE NÀY
package com.example.ezwordmaster.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ezwordmaster.EzWordMasterApplication
import com.example.ezwordmaster.MainActivity
import com.example.ezwordmaster.R
import com.example.ezwordmaster.data.model.NotificationPhrase
import com.example.ezwordmaster.data.repository.NotificationHistoryManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import kotlin.random.Random

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val historyManager: NotificationHistoryManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Đọc file JSON từ assets và chọn ngẫu nhiên
            val phrase = getRandomPhrase()

            if (phrase != null) {
                // 2. Lưu vào file lịch sử
                historyManager.addNotificationToHistory(phrase.title, phrase.content)

                // 3. Hiển thị thông báo
                showNotification(phrase.title, phrase.content)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getRandomPhrase(): NotificationPhrase? {
        return try {
            val jsonString = context.assets.open("notification_phrases.json").bufferedReader().use { it.readText() }
            val phrases = Json.decodeFromString<List<NotificationPhrase>>(jsonString)
            phrases.randomOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun showNotification(title: String, content: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationId = Random.nextInt()
        val builder = NotificationCompat.Builder(context, EzWordMasterApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notify(notificationId, builder.build())
        }
    }
}