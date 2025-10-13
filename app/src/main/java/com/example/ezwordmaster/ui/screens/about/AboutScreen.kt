package com.example.ezwordmaster.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
                    title = "About Us",
                    canNavigateBack = true,
                    onNavigateUp = { navController.popBackStack() },
                    onLogoClick = {
                        navController.popBackStack()
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
                        text = "Version 1.0.0\n\nDeveloped by HKT2\n\nThis app is designed to help you learn new English words effectively through various methods like flashcards, quizzes, and daily notifications.\n\nThank you for using our application!",
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