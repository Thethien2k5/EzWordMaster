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
import androidx.navigation.NavHostController
import com.example.ezwordmaster.ui.common.AppBackground
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background

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
        TrueFalseQuizViewModel(context.applicationContext as Application, showAnswer)
    }
    val state by vm.state.collectAsState()
    // Trình tự chuyển cảnh giữa các câu
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

            // Thẻ câu hỏi và nghĩa với slide
            // Card câu hỏi với chiều cao tối thiểu
            AnimatedContent(
                targetState = state.currentIndex,
                transitionSpec = {
                    (slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn(tween(300))) togetherWith
                    (slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut(tween(300)))
                }, label = "cardSlide"
            ) { idx ->
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
            }

            // Vùng lựa chọn chiếm phần còn lại
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "Các lựa chọn:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                // 2 nút Đúng/Sai
                // Fade in/out các nút lựa chọn
                AnimatedVisibility(
                    visible = optionsVisible,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(200))
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Nút SAI (Đỏ)
                val isSelectedFalse = state.selectedOption == false
                val showResult = state.showResult
                val isCorrectFalse = !state.isCorrectAnswer // Nút Sai đúng khi nghĩa hiển thị là sai

                // PRESS: scale nhanh 1 -> 0.98
                val falseInteraction = remember { MutableInteractionSource() }
                val falsePressed by falseInteraction.collectIsPressedAsState()
                val falsePressScale by animateFloatAsState(if (falsePressed) 0.98f else 1f, tween(100), label = "pressFalse")

                // Background có animation theo trạng thái
                val falseTarget = when {
                    showResult && isSelectedFalse && isCorrectFalse && showAnswer -> Color(0xFF4CAF50)
                    showResult && isSelectedFalse && !isCorrectFalse && showAnswer -> Color(0xFFFF5252)
                    isSelectedFalse -> Color(0xFFE53935)
                    else -> Color(0xFFF5F5F5)
                }
                val falseBg by animateColorAsState(falseTarget, tween(300), label = "falseBg")

                // BOUNCE khi chọn đúng
                val falseBounce = remember { Animatable(1f) }
                LaunchedEffect(showResult, isSelectedFalse, isCorrectFalse, showAnswer) {
                    if (showResult && isSelectedFalse && isCorrectFalse && showAnswer) {
                        falseBounce.animateTo(1.05f, tween(200))
                        falseBounce.animateTo(1f, tween(200))
                    } else falseBounce.snapTo(1f)
                }

                // SHAKE khi chọn sai
                val falseShake = remember { Animatable(0f) }
                LaunchedEffect(showResult, isSelectedFalse, isCorrectFalse, showAnswer) {
                    if (showResult && isSelectedFalse && !isCorrectFalse && showAnswer) {
                        falseShake.animateTo(-10f, tween(60))
                        falseShake.animateTo(10f, tween(120))
                        falseShake.animateTo(-5f, tween(80))
                        falseShake.animateTo(5f, tween(80))
                        falseShake.animateTo(0f, tween(80))
                    } else falseShake.snapTo(0f)
                }

                Button(
                    onClick = { vm.selectOption(false) },
                    enabled = !showResult,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .shadow(if (falsePressed) 6.dp else 1.dp, RoundedCornerShape(12.dp))
                        .graphicsLayer {
                            scaleX = falsePressScale * falseBounce.value
                            scaleY = falsePressScale * falseBounce.value
                            translationX = falseShake.value
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = falseBg,
                        contentColor = if (falseBg == Color(0xFFF5F5F5)) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = falseInteraction
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

                // PRESS
                val trueInteraction = remember { MutableInteractionSource() }
                val truePressed by trueInteraction.collectIsPressedAsState()
                val truePressScale by animateFloatAsState(if (truePressed) 0.98f else 1f, tween(100), label = "pressTrue")

                val trueTarget = when {
                    showResult && isSelectedTrue && isCorrectTrue && showAnswer -> Color(0xFF4CAF50)
                    showResult && isSelectedTrue && !isCorrectTrue && showAnswer -> Color(0xFFFF5252)
                    isSelectedTrue -> Color(0xFF4CAF50)
                    else -> Color(0xFFF5F5F5)
                }
                val trueBg by animateColorAsState(trueTarget, tween(300), label = "trueBg")

                // BOUNCE
                val trueBounce = remember { Animatable(1f) }
                LaunchedEffect(showResult, isSelectedTrue, isCorrectTrue, showAnswer) {
                    if (showResult && isSelectedTrue && isCorrectTrue && showAnswer) {
                        trueBounce.animateTo(1.05f, tween(200))
                        trueBounce.animateTo(1f, tween(200))
                    } else trueBounce.snapTo(1f)
                }

                // SHAKE
                val trueShake = remember { Animatable(0f) }
                LaunchedEffect(showResult, isSelectedTrue, isCorrectTrue, showAnswer) {
                    if (showResult && isSelectedTrue && !isCorrectTrue && showAnswer) {
                        trueShake.animateTo(-10f, tween(60))
                        trueShake.animateTo(10f, tween(120))
                        trueShake.animateTo(-5f, tween(80))
                        trueShake.animateTo(5f, tween(80))
                        trueShake.animateTo(0f, tween(80))
                    } else trueShake.snapTo(0f)
                }

                Button(
                    onClick = { vm.selectOption(true) },
                    enabled = !showResult,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .shadow(if (truePressed) 6.dp else 1.dp, RoundedCornerShape(12.dp))
                        .graphicsLayer {
                            scaleX = truePressScale * trueBounce.value
                            scaleY = truePressScale * trueBounce.value
                            translationX = trueShake.value
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = trueBg,
                        contentColor = if (trueBg == Color(0xFFF5F5F5)) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = trueInteraction
                ) {
                    Text(
                        text = "ĐÚNG",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                }
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


