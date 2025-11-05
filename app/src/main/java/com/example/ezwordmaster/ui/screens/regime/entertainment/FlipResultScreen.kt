package com.example.ezwordmaster.ui.screens.regime.entertainment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R

@Composable
fun FlipResultScreen(
    navController: NavHostController,
    topicId: String?,
    topicName: String?,
    matchedPairs: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA2EAF8),
                        Color(0xFFAAFFA7)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Biểu tượng chính
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Success Icon",
                    modifier = Modifier.size(400.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Thông điệp chúc mừng
            Text(
                text = "Làm tốt lắm!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            // Hiển thị thông tin chi tiết hơn
            Text(
                text = "Bạn đã ghép đúng $matchedPairs cặp từ\ntrong chủ đề \"${topicName ?: "Không rõ"}\"",
                fontSize = 18.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Các nút hành động
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nút làm lại
                Button(
                    onClick = {
                        // Điều hướng về màn hình chọn từ để bắt đầu lại
                        navController.navigate("wordselection/$topicId") {
                            // Xóa màn hình kết quả và màn hình lật thẻ khỏi back stack
                            popUpTo("practice") { inclusive = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Làm lại",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // Nút trang chủ
                Button(
                    onClick = {
                        navController.navigate("home/PRACTICE") {
                            // Xóa toàn bộ back stack để Home trở thành màn hình duy nhất
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Trang chủ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}