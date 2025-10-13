package com.example.ezwordmaster.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezwordmaster.ui.common.AppBackground
import androidx.compose.ui.draw.clip

/**
 * Màn hình Quiz theo thiết kế cơ bản: tiêu đề, câu hỏi, 4 lựa chọn, tiến trình và nút Next.
 */
@Composable
fun QuizScreen(vm: QuizViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tiêu đề và tiến trình
            Text(
                text = "Bài kiểm tra từ vựng",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF0A3D91),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))
            val total = state.questions.size.coerceAtLeast(1)
            val progress = (state.currentIndex + 1).coerceAtMost(total)
            LinearProgressIndicator(
                progress = { progress / total.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF00C853),
                trackColor = Color(0xFFB0BEC5)
            )
            Spacer(Modifier.height(16.dp))

            // Câu hỏi
            val question = state.questions.getOrNull(state.currentIndex)
            Text(
                text = question?.question ?: "Đang tải...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))

            // Lựa chọn
            Column(modifier = Modifier.fillMaxWidth()) {
                question?.options?.forEach { option ->
                    val selected = state.selectedOption == option
                    val showResult = state.showAnswerResult
                    val isCorrect = option.equals(question.answer, ignoreCase = true)
                    val bg = when {
                        showResult && isCorrect -> Color(0xFFB9F6CA)
                        showResult && selected && !isCorrect -> Color(0xFFFFCDD2)
                        selected -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF5F5F5)
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(enabled = !state.showAnswerResult) { vm.selectOption(option) }
                    ) {
                        Box(
                            modifier = Modifier
                                .background(bg)
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(option, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Nút hành động
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Điểm: ${state.score}")
                Button(
                    onClick = {
                        if (!state.showAnswerResult) vm.submitAnswer() else vm.nextQuestion()
                    },
                    enabled = question != null && (state.selectedOption != null || state.showAnswerResult)
                ) {
                    Text(if (!state.showAnswerResult) "Kiểm tra" else if (!state.isCompleted) "Next" else "Hoàn tất")
                }
            }

            if (state.isCompleted) {
                Spacer(Modifier.height(8.dp))
                Text("Hoàn thành: ${state.score}/${state.questions.size}", fontWeight = FontWeight.Bold)
            }
        }
    }
}


