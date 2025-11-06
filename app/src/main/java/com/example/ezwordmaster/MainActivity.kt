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
import com.example.ezwordmaster.ui.AppContainer
import com.example.ezwordmaster.ui.ViewModelFactory
import com.example.ezwordmaster.ui.navigation.AppNavHost
import com.example.ezwordmaster.ui.theme.EzWordMasterTheme

class MainActivity : ComponentActivity() {

    private val REQUESTPERMISSIONLAUNCHER = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Xử lý trường hợp người dùng từ chối cấp quyền
    }

    // Hiển thị thông báo yêu cầu cấp quyền thông báo
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                REQUESTPERMISSIONLAUNCHER.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()

        val APPCONTAINER = AppContainer(applicationContext)
        val VIEWMODELFACTORY = ViewModelFactory(APPCONTAINER)

        setContent {
            EzWordMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(factory = VIEWMODELFACTORY)
                }
            }
        }
    }
}