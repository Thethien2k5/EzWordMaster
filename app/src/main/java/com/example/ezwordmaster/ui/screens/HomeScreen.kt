// DÁN VÀ THAY THẾ TOÀN BỘ NỘI DUNG FILE NÀY
package com.example.ezwordmaster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.AppBackground
import com.example.ezwordmaster.ui.navigation.Routes
import com.example.ezwordmaster.worker.NotificationWorker
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(navController: NavHostController, progress: Int = 75, total: Int = 100) {
    // Lấy context hiện tại để sử dụng cho WorkManager
    val context = LocalContext.current

    AppBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(170.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Tiến độ ôn tập hôm nay", fontSize = 16.sp, color = Color.Black)
                        Text(
                            text = "${(progress.toFloat() / total * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = Color(0xFF00C853),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress.toFloat() / total },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF00C853),
                        trackColor = Color(0xFF455A64)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(text = "$progress/$total ", fontSize = 12.sp, color = Color.Black)
                }

                MenuImageButton(R.drawable.topic) { navController.navigate(Routes.TOPIC_MANAGEMENT) }
                MenuImageButton(R.drawable.practice) { /* TODO */ }
                MenuImageButton(R.drawable.quiz) { /* TODO */ }
                MenuImageButton(R.drawable.translate) { navController.navigate(Routes.TRANSLATE) }
                MenuImageButton(R.drawable.ranking) { /* TODO */ }

                Spacer(modifier = Modifier.height(32.dp))

                // ================================================================
                // NÚT TEST THÔNG BÁO - ĐÃ THÊM VÀO ĐÂY
                // ================================================================
                Button(onClick = {
                    val testRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                        .setInitialDelay(5, TimeUnit.SECONDS)
                        .build()
                    WorkManager.getInstance(context).enqueue(testRequest)
                }) {
                    Text("Test Thông Báo Ngay")
                }
                // ================================================================

                Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách cho đẹp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_help),
                        contentDescription = "Help",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate(Routes.HELP) }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Info",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate(Routes.ABOUT) }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.navigate(Routes.SETTINGS) }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.navigate(Routes.NOTIFICATION) }
                )
            }
        }
    }
}

@Composable
fun MenuImageButton(imageRes: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        contentScale = ContentScale.FillBounds
    )
}