package com.example.ezwordmaster.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import com.example.ezwordmaster.ui.common.GradientBackground
import com.example.ezwordmaster.ui.navigation.Routes

data class NotificationSample(val title: String, val content: String, val time: String)
val sampleNotifications = listOf(
    NotificationSample("It's time to learn !!", "Let's review some words in 'Technology' topic.", "9:00 AM"),
    NotificationSample("Daily Reminder", "A new word is waiting for you. Open app now!", "Yesterday"),
    NotificationSample("Keep up the great work!", "You're on a 3-day learning streak.", "Yesterday"),
    NotificationSample("It's time to learn !!", "Don't forget to practice your vocabulary today.", "2 days ago")
)

@Composable
fun NotificationScreen(navController: NavHostController) {
    GradientBackground {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Thông Báo",
                    canNavigateBack = true,
                    onNavigateUp = { navController.popBackStack() },
                    onLogoClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleNotifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationSample) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(notification.title, fontWeight = FontWeight.Bold)
            Text(notification.content, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(notification.time, fontSize = 12.sp)
    }
}