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
 * Màn hình Quiz nhiều đáp án với logic random đáp án sai từ các câu khác.
 */
@Composable
fun MultiChoiceQuizScreen(
    navController: NavHostController,
    showAnswer: Boolean = true
) {
    val context = LocalContext.current
    val vm: MultiChoiceQuizViewModel = viewModel { 
        MultiChoiceQuizViewModel(context.applicationContext as android.app.Application, showAnswer) 
    }
    val state by vm.state.collectAsState()
    
    // Xử lý tự động chuyển câu khi tắt hiển thị đáp án
    LaunchedEffect(state.showCorrectAnswer, state.isCompleted) {
        if (!showAnswer && state.showCorrectAnswer) {
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

            // Thẻ câu hỏi và đáp án
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

                            // Đáp án (chỉ hiển thị khi showAnswer = true)
                            if (showAnswer && state.showCorrectAnswer) {
                                Text(
                                    text = "Đáp án: ${state.currentQuestion?.answer}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF1B5E20),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = "Chọn nghĩa đúng từ các lựa chọn bên dưới",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Các lựa chọn
            Text(
                text = "Các lựa chọn:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                state.shuffledOptions.forEachIndexed { index, option ->
                    val isCorrect = option == state.currentQuestion?.answer
                    val isSelected = state.selectedOption == option
                    val showResult = state.showCorrectAnswer
                    
                            val backgroundColor = when {
                                showResult && isCorrect && showAnswer -> Color(0xFF4CAF50) // Xanh lá đậm cho đáp án đúng (chỉ khi showAnswer = true)
                                showResult && isSelected && !isCorrect && showAnswer -> Color(0xFFE53935) // Đỏ đậm cho đáp án sai đã chọn (chỉ khi showAnswer = true)
                                isSelected -> Color(0xFFE3F2FD) // Xanh nhạt cho đáp án đang chọn
                                else -> Color(0xFFF5F5F5) // Xám nhạt cho đáp án chưa chọn
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = backgroundColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !state.showCorrectAnswer) {
                                        vm.selectOption(option)
                                    }
                            ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "${'A' + index}. $option",
                                color = Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
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
                    enabled = state.showCorrectAnswer,
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


