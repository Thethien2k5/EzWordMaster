package com.example.ezwordmaster.ui.screens.regime.practice.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.EssayQuizActionButton
import com.example.ezwordmaster.ui.common.QuizQuestionCard
import kotlinx.coroutines.delay

@Composable
fun EssayQuizScreen(
    navController: NavHostController,
    topicId: String?,
    showAnswer: Boolean = true,
    viewModel: QuizViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val currentQuestion = state.questions.getOrNull(state.currentIndex)

    var optionsVisible by remember { mutableStateOf(true) }

    // Tải chủ đề theo ID (giống FlashcardScreen)
    LaunchedEffect(topicId) {
        viewModel.loadTopic(topicId ?: "Lỗi không thấy id")
    }

    // Animation cho options
    LaunchedEffect(state.currentIndex) {
        optionsVisible = false
        delay(200)
        optionsVisible = true
    }

    // Chuyển đến màn hình kết quả khi hoàn thành (giống FlashcardScreen)
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            val finalTopicId = state.topic?.id ?: topicId ?: "unknown"
            val topicName = state.topic?.name ?: "Unknown"
            val totalWords = state.questions.size

            navController.navigate("result/${finalTopicId}/${topicName}/${state.knownWords}/${state.learningWords}/${totalWords}") {
                popUpTo("practice") { inclusive = false }
            }
        }
    }

    // Auto next nếu showAnswer = false
    LaunchedEffect(state.showResult) {
        if (!showAnswer && state.showResult) {
            delay(800)
            viewModel.nextQuestion()
        }
    }

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
                .padding(16.dp)
        ) {
            // Header với nút back và progress (giống FlashcardScreen)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.return_),
                    contentDescription = "Back",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { navController.navigate("wordpractice/$topicId") }
                )

                // Chỉ báo tiến độ
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "${state.currentIndex + 1}/${state.questions.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Chỗ trống để cân bằng bố cục
                Spacer(modifier = Modifier.size(45.dp))
            }

            Spacer(Modifier.height(20.dp))

            // Huy hiệu thống kê (giống FlashcardScreen)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Chưa nhớ
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "${state.learningWords}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                // Đã nhớ
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "${state.knownWords}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Card câu hỏi với animation
            AnimatedContent(
                targetState = state.currentIndex,
                transitionSpec = {
                    (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn(tween(300))) togetherWith
                            (slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut(
                                tween(300)
                            ))
                },
                label = "cardSlide"
            ) {
                QuizQuestionCard(
                    questionText = currentQuestion?.questionWord?.word ?: "Đang tải...",
                    answerText = currentQuestion?.questionWord?.meaning ?: "",
                    helperText = "Nhập nghĩa của từ trên",
                    showAnswer = showAnswer,
                    showResult = state.showResult,
                    lastAnswerCorrect = state.lastAnswerCorrect
                )
            }

            Spacer(Modifier.height(16.dp))

            // Phần nhập đáp án
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedVisibility(
                    visible = optionsVisible,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(200))
                ) {
                    Column {
                        Text(
                            "Đáp án của bạn:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            BasicTextField(
                                value = state.essayAnswer,
                                onValueChange = viewModel::onEssayAnswerChanged,
                                enabled = !state.showResult,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                ),
                                decorationBox = { innerTextField ->
                                    if (state.essayAnswer.isEmpty() && !state.showResult) {
                                        Text(
                                            "Nhập...",
                                            color = Color.Gray,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }
            }

            // Văn bản hướng dẫn (giống FlashcardScreen)
            if (!showAnswer) {
                Text(
                    text = "Nhập đáp án và gửi",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Nút action
            EssayQuizActionButton(
                showAnswer = showAnswer,
                userAnswerIsNotBlank = state.essayAnswer.isNotBlank(),
                isCompleted = state.isCompleted,
                showResult = state.showResult,
                onNext = { viewModel.nextQuestion() },
                onSubmit = {
                    if (state.essayAnswer.isNotBlank()) {
                        viewModel.submitEssay()
                    }
                },
                onComplete = {
                    val topicId = state.topic?.id ?: "unknown"
                    val topicName = state.topic?.name ?: "Unknown"
                    navController.navigate("result/$topicId/$topicName/${state.knownWords}/${state.learningWords}/${state.questions.size}")
                }
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}