package com.example.ezwordmaster.data.fcm

// KHÔNG import EzWordMasterApplication nữa
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ezwordmaster.R
import com.example.ezwordmaster.data.local.entity.NotificationEntity
import com.example.ezwordmaster.domain.repository.INotificationRepository
import com.example.ezwordmaster.utils.NotificationSettings
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationRepository: INotificationRepository

    override fun onCreate() {
        super.onCreate()

        // ############ SỬA LỖI Ở ĐÂY ############
        // Ép kiểu (cast) về class "NotificationSettings" của BẠN
        // thay vì "EzWordMasterApplication" (file đã xóa)
        notificationRepository =
            (application as NotificationSettings).appContainer.notificationRepository
        // ######################################
    }

    /**
     * Được gọi khi có tin nhắn mới từ Firebase.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 1. Lấy nội dung thông báo
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Thông báo mới"
            val body = notification.body ?: "Bạn có tin nhắn mới"

            println("🔥 FCM Received: $title - $body")

            // 2. Tự hiển thị thông báo (vì app đang chạy)
            sendNotification(title, body)

            // 3. Lưu vào database của bạn
            saveToDatabase(title, body)
        }
    }

    private fun saveToDatabase(title: String, body: String) {
        val notificationEntity = NotificationEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = body, // Đã sửa ở lần trước
            timestamp = Date(),
            isRead = false
        )

        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.insertNotification(notificationEntity)
            println("💾 FCM Message saved to DB.")
        }
    }

    /**
     * Hàm này tạo và hiển thị thông báo lên thanh trạng thái
     */
    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "ezwordmaster_channel"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "EzWordMaster Thông Báo",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    /**
     * Được gọi khi Firebase cấp token mới cho thiết bị.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("🔑 FCM Token Mới: $token")
        // (Thêm code để gửi token này về server của bạn ở đây)
    }
}