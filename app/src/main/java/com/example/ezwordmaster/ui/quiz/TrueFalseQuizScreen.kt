package com.example.ezwordmaster.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.ezwordmaster.ui.common.AppBackground
import kotlinx.coroutines.delay

/**
 * Màn hình Quiz Đúng/Sai với nghĩa random đúng/sai.
 */
@Composable
fun TrueFalseQuizScreen(
    navController: NavHostController,
    showAnswer: Boolean = true
) {
    val context = LocalContext.current
    val vm: TrueFalseQuizViewModel = viewModel { 
        TrueFalseQuizViewModel(context.applicationContext as android.app.Application, showAnswer) 
    }
    val state by vm.state.collectAsState()
    
    // Xử lý tự động chuyển câu khi tắt hiển thị đáp án
    LaunchedEffect(state.showResult, state.isCompleted) {
        if (!showAnswer && state.showResult) {
            delay(100) // Đợi một chút để state được cập nhật
            if (state.isCompleted) {
                navController.navigate("quiz_result/${state.score}/${state.totalQuestions}")
            } else {
                vm.nextQuestion()
            }
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header với nút quay lại và số câu hỏi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại (to hơn)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.clickable { navController.popBackStack() }
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                // Số câu hỏi hiện tại
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${state.currentIndex + 1}/${state.totalQuestions}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Placeholder cho cân bằng layout
                Spacer(modifier = Modifier.size(64.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Thẻ câu hỏi và nghĩa
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Từ tiếng Anh hiển thị to đùng ở giữa
                    Text(
                        text = state.currentQuestion?.question ?: "Đang tải...",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    // Đường phân cách
                    HorizontalDivider(color = Color.Black, thickness = 1.dp)

                    Spacer(Modifier.height(24.dp))

                    // Nghĩa hiển thị (có thể đúng hoặc sai)
                    Text(
                        text = state.displayedAnswer,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Hướng dẫn
                    Text(
                        text = "Nghĩa trên có đúng không?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Các lựa chọn Đúng/Sai
            Text(
                text = "Các lựa chọn:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            // 2 nút Đúng/Sai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nút SAI (Đỏ)
                val isSelectedFalse = state.selectedOption == false
                val showResult = state.showResult
                val isCorrectFalse = !state.isCorrectAnswer // Nút Sai đúng khi nghĩa hiển thị là sai
                
                        val falseButtonColor = when {
                            showResult && isSelectedFalse && isCorrectFalse && showAnswer -> Color(0xFF4CAF50) // Xanh khi chọn đúng (chỉ khi showAnswer = true)
                            showResult && isSelectedFalse && !isCorrectFalse && showAnswer -> Color(0xFFE53935) // Đỏ khi chọn sai (chỉ khi showAnswer = true)
                            isSelectedFalse -> Color(0xFFE53935) // Đỏ khi đang chọn
                            else -> Color(0xFFF5F5F5) // Xám nhạt khi chưa chọn
                        }

                Button(
                    onClick = { vm.selectOption(false) },
                    enabled = !showResult,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = falseButtonColor,
                        contentColor = if (falseButtonColor == Color(0xFFF5F5F5)) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "SAI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Nút ĐÚNG (Xanh)
                val isSelectedTrue = state.selectedOption == true
                val isCorrectTrue = state.isCorrectAnswer // Nút Đúng đúng khi nghĩa hiển thị là đúng
                
                        val trueButtonColor = when {
                            showResult && isSelectedTrue && isCorrectTrue && showAnswer -> Color(0xFF4CAF50) // Xanh khi chọn đúng (chỉ khi showAnswer = true)
                            showResult && isSelectedTrue && !isCorrectTrue && showAnswer -> Color(0xFFE53935) // Đỏ khi chọn sai (chỉ khi showAnswer = true)
                            isSelectedTrue -> Color(0xFF4CAF50) // Xanh khi đang chọn
                            else -> Color(0xFFF5F5F5) // Xám nhạt khi chưa chọn
                        }

                Button(
                    onClick = { vm.selectOption(true) },
                    enabled = !showResult,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = trueButtonColor,
                        contentColor = if (trueButtonColor == Color(0xFFF5F5F5)) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ĐÚNG",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

                    // Thông báo kết quả (chỉ hiển thị khi showAnswer = true)
                    if (state.showResult && showAnswer) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (state.lastAnswerCorrect == true) Color(0xFFE8F5E8) else Color(0xFFFFEBEE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.lastAnswerCorrect == true) {
                                "Chính xác! ✅"
                            } else {
                                "Không đúng! ❌"
                            },
                            color = if (state.lastAnswerCorrect == true) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Nút hành động (chỉ hiển thị khi bật "Hiển thị đáp án" và đã chọn đáp án)
            if (showAnswer && state.selectedOption != null) {
                Button(
                    onClick = {
                        if (state.isCompleted) {
                            // Điều hướng đến trang kết quả
                            navController.navigate("quiz_result/${state.score}/${state.totalQuestions}")
                        } else {
                            vm.nextQuestion()
                        }
                    },
                    enabled = state.showResult,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (state.isCompleted) "Xem kết quả" else "Câu tiếp theo",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


