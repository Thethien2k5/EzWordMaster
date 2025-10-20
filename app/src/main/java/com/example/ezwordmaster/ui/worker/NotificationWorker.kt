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
import com.example.ezwordmaster.data.local.NotificationEntity
import com.example.ezwordmaster.data.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationRepository: NotificationRepository // Inject repository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val (title, content) = getRandomNotification()

            // Lưu thông báo vào database
            notificationRepository.insert(NotificationEntity(title = title, content = content))

            // Hiển thị thông báo
            showNotification(title, content)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(title: String, content: String) {
        // Intent để mở app khi người dùng nhấn vào thông báo
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationId = Random.nextInt()
        val builder = NotificationCompat.Builder(context, EzWordMasterApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo) // Đảm bảo bạn có icon tên là 'logo' trong 'drawable'
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Quan trọng để hiện nổi
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Tự xóa khi nhấn

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Cần xử lý quyền POST_NOTIFICATIONS trên Android 13+
                // Hiện tại, ta tạm bỏ qua để đơn giản, nhưng trong app thật cần có
                return
            }
            notify(notificationId, builder.build())
        }
    }

    private fun getRandomNotification(): Pair<String, String> {
        val list = listOf(
            "Đến giờ học rồi!" to "Cùng ôn lại vài từ vựng trong chủ đề 'Công nghệ' nào.",
            "Nhắc nhở hàng ngày" to "Một từ mới đang chờ bạn khám phá. Mở app ngay!",
            "Giữ vững chuỗi ngày học!" to "Bạn đã có chuỗi 3 ngày học liên tiếp. Cố lên!",
            "Đừng quên luyện tập" to "Luyện tập mỗi ngày là chìa khóa thành công."
        )
        return list.random()
    }
}