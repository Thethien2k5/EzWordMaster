// DÁN VÀ THAY THẾ TOÀN BỘ NỘI DUNG FILE NÀY
package com.example.ezwordmaster.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.data.local.NotificationEntity
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import com.example.ezwordmaster.ui.common.GradientBackground
import com.example.ezwordmaster.ui.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationViewModel
) {
    val notifications by viewModel.notifications.collectAsState()

    GradientBackground {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Thông Báo",
                    canNavigateBack = true,
                    onNavigateUp = { navController.popBackStack() },
                    onLogoClick = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (notifications.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bạn chưa có thông báo nào.", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(items = notifications, key = { it.id }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onDeleteClick = { viewModel.deleteNotification(notification) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(notification.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(notification.content, fontSize = 14.sp, color = Color.Gray)
            }
            // THÊM NÚT XÓA (ICON THÙNG RÁC)
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa thông báo",
                    tint = Color.Gray
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(formatTimestamp(notification.timestamp), fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}


private fun formatTimestamp(timestamp: Long): String {
    val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val now = Calendar.getInstance()
    return when {
        now.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        }
        now.get(Calendar.DAY_OF_YEAR) - 1 == messageDate.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) -> {
            "Hôm qua"
        }
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    }
}