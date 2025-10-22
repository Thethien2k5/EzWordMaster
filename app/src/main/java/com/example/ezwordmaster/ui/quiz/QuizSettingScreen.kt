package com.example.ezwordmaster.ui.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.AppBackground

/** Loại hình kiểm tra. */
enum class QuizMode { TRUE_FALSE, ESSAY, MULTI_CHOICE }

/**
 * Màn cài đặt Quiz (UI theo mock):
 * - Tuỳ chọn số câu hỏi (UI giả lập)
 * - Nút Hiển thị đáp án (UI giả lập)
 * - Chọn cách trả lời: Đúng/Sai, Tự luận, Nhiều đáp án
 * - Nút Bắt đầu: điều hướng theo loại được chọn
 */
@Composable
fun QuizSettingScreen(navController: NavHostController) {
    var selectedMode by remember { mutableStateOf(QuizMode.MULTI_CHOICE) }
    var showAnswer by remember { mutableStateOf(true) }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header với nút quay lại
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại về trang chủ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.clickable { navController.navigate("home") }
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                // Tiêu đề
                Text(
                    text = "Cài đặt bài kiểm tra",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF0A3D91),
                    fontWeight = FontWeight.Bold
                )

                // Placeholder cho cân bằng layout
                Spacer(modifier = Modifier.size(64.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Logo ở giữa (chỉ logo, không có nền trắng)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(32.dp))

            // Hàng: Hiển thị đáp án (UI)
            SettingRow(
                label = "Hiển thị đáp án",
                trailing = {
                    Switch(
                        checked = showAnswer, 
                        onCheckedChange = { showAnswer = it }
                    )
                }
            )

            Spacer(Modifier.height(12.dp))
            Divider(color = Color(0x33000000))
            Spacer(Modifier.height(12.dp))

            Text("Cách trả lời", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // 3 loại cách trả lời
            ModeRow(
                label = "Đúng?Sai",
                selected = selectedMode == QuizMode.TRUE_FALSE,
                onClick = { selectedMode = QuizMode.TRUE_FALSE }
            )
            ModeRow(
                label = "Tự luận",
                selected = selectedMode == QuizMode.ESSAY,
                onClick = { selectedMode = QuizMode.ESSAY }
            )
            ModeRow(
                label = "Nhiều đáp án",
                selected = selectedMode == QuizMode.MULTI_CHOICE,
                onClick = { selectedMode = QuizMode.MULTI_CHOICE }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Điều hướng theo loại bài kiểm tra với thông tin hiển thị đáp án
                    val route = when (selectedMode) {
                        QuizMode.TRUE_FALSE -> "quiz_true_false/$showAnswer"
                        QuizMode.ESSAY -> "quiz_essay/$showAnswer"
                        QuizMode.MULTI_CHOICE -> "quiz_multi/$showAnswer"
                    }
                    navController.navigate(route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3578F4),
                    contentColor = Color.White
                )
            ) {
                Text("Bắt đầu", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SettingRow(label: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Black)
        trailing()
    }
}

@Composable
private fun ModeRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Black)
        RadioButton(selected = selected, onClick = onClick)
    }
}


