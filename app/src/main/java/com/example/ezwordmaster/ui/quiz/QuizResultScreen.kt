package com.example.ezwordmaster.ui.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.AppBackground

/**
 * Dữ liệu chi tiết của một câu hỏi đã làm.
 */
data class QuizAnswerDetail(
    val questionNumber: Int,
    val question: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)

/**
 * Màn hình kết quả quiz với thông tin chi tiết đầy đủ.
 */
@Composable
fun QuizResultScreen(
    navController: NavHostController,
    score: Int = 0,
    totalQuestions: Int = 0,
    answerDetails: List<QuizAnswerDetail> = emptyList()
) {
    // Tính điểm số thực tế
    val actualScore = if (answerDetails.isEmpty()) 1 else score
    val actualTotal = if (answerDetails.isEmpty()) 5 else totalQuestions
    val correctCount = if (answerDetails.isEmpty()) 1 else answerDetails.count { it.isCorrect }
    val wrongCount = if (answerDetails.isEmpty()) 4 else answerDetails.size - correctCount
    
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Làm tốt lắm",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "kết quả bài kiểm tra",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }
                
                // Icon logo (chỉ logo, không có nền trắng)
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Kết quả tổng quan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Vòng tròn điểm số
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${(actualScore.toFloat() / actualTotal * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Số câu đúng/sai
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                                Text(
                                    text = "Đúng $correctCount",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                        }
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE53935)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                                Text(
                                    text = "Sai $wrongCount",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Các nút hành động
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { 
                        navController.navigate("quiz_setting") {
                            popUpTo("quiz_setting") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text("Làm lại", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = { 
                        navController.navigate("home") {
                            popUpTo("quiz_setting") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text("Quay lại trang chủ", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Chi tiết đáp án
            Text(
                text = "Đáp án của bạn",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Danh sách câu hỏi và đáp án (có thể cuộn)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // Cố định chiều cao để có thể cuộn
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (answerDetails.isEmpty()) {
                        // Hiển thị dữ liệu mẫu nếu chưa có dữ liệu thực
                        val sampleDetails = listOf(
                            QuizAnswerDetail(1, "happy", "sad", "joyful", false),
                            QuizAnswerDetail(2, "quick", "slow", "rapid", false),
                            QuizAnswerDetail(3, "cold", "warm", "warm", true),
                            QuizAnswerDetail(4, "big", "small", "large", false),
                            QuizAnswerDetail(5, "beautiful", "ugly", "pretty", false)
                        )
                        
                        sampleDetails.forEach { detail ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8F9FA),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    // Câu hỏi
                                    Text(
                                        text = "Câu hỏi ${detail.questionNumber}: ${detail.question}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(Modifier.height(8.dp))
                                    
                                    // Đáp án của người dùng
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Đáp án của bạn: ${detail.userAnswer}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black
                                        )
                                        
                                        if (detail.isCorrect) {
                                            Spacer(Modifier.width(8.dp))
                                            Text("✓", color = Color(0xFF4CAF50), fontSize = 16.sp)
                                        }
                                    }
                                    
                                    Spacer(Modifier.height(4.dp))
                                    
                                    // Đáp án chính xác (màu xanh)
                                    Text(
                                        text = "Đáp án chính xác: ${detail.correctAnswer}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        // Hiển thị dữ liệu thực từ quiz
                        answerDetails.forEach { detail ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8F9FA),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    // Câu hỏi
                                    Text(
                                        text = "Câu hỏi ${detail.questionNumber}: ${detail.question}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(Modifier.height(8.dp))
                                    
                                    // Đáp án của người dùng
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Đáp án của bạn: ${detail.userAnswer}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black
                                        )
                                        
                                        if (detail.isCorrect) {
                                            Spacer(Modifier.width(8.dp))
                                            Text("✓", color = Color(0xFF4CAF50), fontSize = 16.sp)
                                        }
                                    }
                                    
                                    Spacer(Modifier.height(4.dp))
                                    
                                    // Đáp án chính xác (màu xanh)
                                    Text(
                                        text = "Đáp án chính xác: ${detail.correctAnswer}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
