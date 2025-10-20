package com.example.ezwordmaster.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.background
import androidx.navigation.NavHostController
import com.example.ezwordmaster.ui.common.AppBackground
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/**
 * Màn hình Quiz tự luận với ô nhập đáp án.
 */
@Composable
fun EssayQuizScreen(
    navController: NavHostController,
    showAnswer: Boolean = true
) {
    val context = LocalContext.current
    val vm: EssayQuizViewModel = viewModel { 
        EssayQuizViewModel(context.applicationContext as android.app.Application, showAnswer) 
    }
    val state by vm.state.collectAsState()
    // Trình tự chuyển cảnh
    var optionsVisible by remember { mutableStateOf(true) }
    var progressTarget by remember { mutableStateOf(0f) }
    LaunchedEffect(state.totalQuestions) {
        progressTarget = if (state.totalQuestions == 0) 0f else (state.currentIndex + 1f) / state.totalQuestions.toFloat()
    }
    LaunchedEffect(state.currentIndex) {
        optionsVisible = false
        delay(200)
        delay(300)
        progressTarget = if (state.totalQuestions == 0) 0f else (state.currentIndex + 1f) / state.totalQuestions.toFloat()
        delay(200)
        optionsVisible = true
    }
    
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
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = state.currentIndex,
                            transitionSpec = {
                                (fadeIn(tween(200)) + slideInVertically(initialOffsetY = { -10 })) togetherWith
                                (fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { 10 }))
                            }, label = "counter"
                        ) { idx ->
                            Text(
                                text = "${idx + 1}/${state.totalQuestions}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val progress by animateFloatAsState(
                            targetValue = if (state.totalQuestions == 0) 0f else (state.currentIndex + 1f) / state.totalQuestions.toFloat(),
                            animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing),
                            label = "progress"
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .background(Color(0xFF1976D2), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // Placeholder cho cân bằng layout
                Spacer(modifier = Modifier.size(64.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Thẻ câu hỏi với slide giữa các câu
            AnimatedContent(
                targetState = state.currentIndex,
                transitionSpec = {
                    (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn(tween(300))) togetherWith
                    (slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut(tween(300)))
                }, label = "cardSlide"
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 420.dp)
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
                                color = if (state.lastAnswerCorrect == true) Color(0xFF1B5E20) else Color(0xFFD32F2F),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "Nhập nghĩa tiếng Việt của từ trên",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Vùng nhập đáp án (chiếm phần còn lại)
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
                Text(
                    text = "Đáp án của bạn:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

            Spacer(Modifier.height(8.dp))

                    // TextField để nhập đáp án
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = state.userAnswer,
                            onValueChange = vm::updateUserAnswer,
                            enabled = !state.showCorrectAnswer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 16.sp,
                                color = Color.Black
                            ),
                            decorationBox = { innerTextField ->
                                if (state.userAnswer.isEmpty() && !state.showCorrectAnswer) {
                                    Text(
                                        text = "Nhập nghĩa tiếng Việt...",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                Spacer(Modifier.height(16.dp))
                }
            }

            // Nút hành động: khi bật hiển thị đáp án thì giống cũ;
            // khi tắt hiển thị đáp án, hiển thị nút "Kiểm tra/Câu tiếp theo" để tiến trình
            if ((showAnswer && state.userAnswer.isNotBlank()) || (!showAnswer && state.userAnswer.isNotBlank())) {
                Button(
                    onClick = {
                        if (state.isCompleted) {
                            // Điều hướng đến trang kết quả
                            navController.navigate("quiz_result/${state.score}/${state.totalQuestions}")
                        } else {
                            if (!showAnswer && !state.showCorrectAnswer) {
                                vm.submitAnswer()
                            } else {
                                vm.nextQuestion()
                            }
                        }
                    },
                    enabled = if (showAnswer) state.showCorrectAnswer else state.userAnswer.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (state.isCompleted) "Xem kết quả" else if (!showAnswer && !state.showCorrectAnswer) "Kiểm tra" else "Câu tiếp theo",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


