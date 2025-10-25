package com.example.ezwordmaster.ui.screens.quiz

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
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
import androidx.compose.foundation.background
// Thêm import cho chuyển cảnh giữa câu và fade options
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

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
        MultiChoiceQuizViewModel(context.applicationContext as Application, showAnswer)
    }
    val state by vm.state.collectAsState()
    
    // Thêm state cục bộ để điều khiển trình tự chuyển cảnh giữa các câu
    // optionsVisible: điều khiển fade out/in của vùng lựa chọn
    // progressTarget: cập nhật sau khi slide card (bước 3)
    var optionsVisible by remember { mutableStateOf(true) }
    var progressTarget by remember { mutableStateOf(0f) }

    LaunchedEffect(state.totalQuestions) {
        progressTarget = if (state.totalQuestions == 0) 0f else (state.currentIndex + 1f) / state.totalQuestions.toFloat()
    }

    LaunchedEffect(state.currentIndex) {
        // 1) Fade out options (200ms)
        optionsVisible = false
        delay(200)
        // 2) Slide card (300ms) do AnimatedContent đảm nhiệm, mình chờ cho đủ nhịp
        delay(300)
        // 3) Cập nhật progress mượt (200ms)
        progressTarget = if (state.totalQuestions == 0) 0f else (state.currentIndex + 1f) / state.totalQuestions.toFloat()
        delay(200)
        // 4) Fade in options (300ms)
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

                        // Dùng progressTarget để cập nhật ở bước 3 cho mượt
                        val progress by animateFloatAsState(
                            targetValue = progressTarget,
                            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
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

            // Thẻ câu hỏi và đáp án
            // Sử dụng AnimatedContent để slide card cũ ra trái, card mới vào từ phải
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
            }

            Spacer(Modifier.height(16.dp))

            // Vùng lựa chọn (chiếm phần còn lại)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Các lựa chọn
                Text(
                    text = "Các lựa chọn:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                // Fade options theo optionsVisible
                AnimatedVisibility(
                    visible = optionsVisible,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(200))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        state.shuffledOptions.forEachIndexed { index, option ->
                            val isCorrect = option == state.currentQuestion?.answer
                            val isSelected = state.selectedOption == option
                            val showResult = state.showCorrectAnswer

                            // interactionSource để bắt trạng thái nhấn (press)
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()

                            // Scale khi nhấn (press): 1 -> 0.98 (100ms)
                            val pressScale by animateFloatAsState(
                                targetValue = if (isPressed) 0.98f else 1f,
                                animationSpec = tween(durationMillis = 100),
                                label = "pressScale"
                            )

                            // Màu nền có animation mượt theo trạng thái
                            val targetColor = when {
                                showResult && isCorrect && showAnswer -> Color(0xFF4CAF50) // đúng khi hiển thị đáp án
                                showResult && isSelected && !isCorrect && showAnswer -> Color(0xFFFF5252) // sai khi hiển thị đáp án
                                isSelected -> Color(0xFFE3F2FD) // đang chọn
                                else -> Color(0xFFF5F5F5) // mặc định
                            }
                            val animatedBg by animateColorAsState(targetValue = targetColor, animationSpec = tween(300), label = "bg")

                            // Bounce khi chọn đúng: scale 1 -> 1.05 -> 1 (400ms)
                            val bounceScale = remember { Animatable(1f) }
                            LaunchedEffect(showResult, isSelected, isCorrect, showAnswer) {
                                if (showResult && isSelected && isCorrect && showAnswer) {
                                    bounceScale.animateTo(1.05f, animationSpec = tween(200))
                                    bounceScale.animateTo(1f, animationSpec = tween(200))
                                } else {
                                    bounceScale.snapTo(1f)
                                }
                            }

                            // Shake khi chọn sai: dịch X qua lại (500ms)
                            val shakeX = remember { Animatable(0f) }
                            LaunchedEffect(showResult, isSelected, isCorrect, showAnswer) {
                                if (showResult && isSelected && !isCorrect && showAnswer) {
                                    // 0 -> -10 -> 10 -> -5 -> 5 -> 0
                                    shakeX.animateTo(-10f, tween(60))
                                    shakeX.animateTo(10f, tween(120))
                                    shakeX.animateTo(-5f, tween(80))
                                    shakeX.animateTo(5f, tween(80))
                                    shakeX.animateTo(0f, tween(80))
                                } else {
                                    shakeX.snapTo(0f)
                                }
                            }

                            // Pulse cho nút đúng khi hiển thị đáp án (2 lần)
                            val pulseAlpha = remember { Animatable(1f) }
                            LaunchedEffect(showResult, isCorrect, showAnswer) {
                                if (showResult && isCorrect && showAnswer) {
                                    repeat(2) {
                                        pulseAlpha.animateTo(0.7f, tween(200))
                                        pulseAlpha.animateTo(1f, tween(200))
                                    }
                                } else {
                                    pulseAlpha.snapTo(1f)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = animatedBg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    // Bóng (shadow) tăng nhẹ khi nhấn
                                    .shadow(if (isPressed) 6.dp else 1.dp, RoundedCornerShape(12.dp))
                                    // Kết hợp press scale, bounce scale, và shake X
                                    .graphicsLayer {
                                        scaleX = pressScale * bounceScale.value
                                        scaleY = pressScale * bounceScale.value
                                        translationX = shakeX.value
                                        alpha = pulseAlpha.value
                                    }
                                    .clickable(
                                        enabled = !state.showCorrectAnswer,
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
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
                                    // Icon check mờ dần xuất hiện khi đúng và đang hiển thị đáp án
                                    if (showResult && isCorrect && showAnswer) {
                                        Text(
                                            text = "✓",
                                            color = Color.White,
                                            modifier = Modifier.align(Alignment.CenterEnd),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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
}


