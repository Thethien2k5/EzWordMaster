package com.example.ezwordmaster.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import com.example.ezwordmaster.ui.common.GradientBackground

@Composable
fun AboutScreen(navController: NavHostController) {
    // Sử dụng nền gradient
    GradientBackground {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Về chúng tôi",
                    canNavigateBack = true,
                    onNavigateUp = { navController.navigate("home/SETTINGS") },
                    onLogoClick = {
                        navController.navigate("home/SETTINGS")
                    }
                )
            },
            // Đặt nền trong suốt để thấy gradient
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Cho phép cuộn nếu text quá dài
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EzWordMaster",
                        fontSize = 28.sp,
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Phiên bản 2.0.0\n" +
                                "Phát triển bởi HKT2\n\n" +
                                "EzWordMaster là người bạn đồng hành lý tưởng trên hành trình chinh " +
                                "phục từ vựng tiếng Anh của bạn. Ứng dụng được thiết kế để giúp " +
                                "bạn học từ mới một cách hiệu quả và thú vị thông qua các phương " +
                                "pháp đa dạng như flashcard trực quan, các bài trắc nghiệm " +
                                "(quizzes) đầy thử thách, và tính năng nhắc nhở học tập hàng " +
                                "ngày.Mục tiêu của chúng tôi là mang đến một công cụ học tập " +
                                "linh hoạt, cá nhân hóa, giúp bạn xây dựng vốn từ vựng vững " +
                                "chắc mọi lúc, mọi nơi.\n\n Cảm ơn bạn đã tin tưởng và sử dụng ứng " +
                                "dụng của chúng tôi!",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

// ✅ HÀM PREVIEW ĐÃ ĐƯỢC SỬA LẠI
@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    // Tạo một NavController giả để preview có thể chạy
    AboutScreen(navController = rememberNavController())
}