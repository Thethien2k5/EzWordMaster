// DÁN VÀ THAY THẾ TOÀN BỘ NỘI DUNG FILE NÀY
package com.example.ezwordmaster

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ezwordmaster.ui.navigation.AppNavHost
import com.example.ezwordmaster.ui.theme.EzWordMasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ================================================================
    // LOGIC XIN QUYỀN GỬI THÔNG BÁO - ĐÃ THÊM VÀO ĐÂY
    // ================================================================
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Bạn có thể thêm xử lý ở đây nếu người dùng từ chối
        // Ví dụ: hiển thị một thông báo giải thích tại sao cần quyền
    }

    private fun askNotificationPermission() {
        // Chỉ áp dụng cho Android 13 (API 33) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Nếu chưa có quyền, hiển thị hộp thoại xin quyền
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // ================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gọi hàm xin quyền ngay khi Activity được tạo
        askNotificationPermission()

        setContent {
            EzWordMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}