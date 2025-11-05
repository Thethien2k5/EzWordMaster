package com.example.ezwordmaster.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizHeader(
    onBack: () -> Unit,
    currentIndex: Int,
    totalQuestions: Int,
    progressTarget: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE3F2FD),
            modifier = Modifier.clickable { onBack() }) {
            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                Text("←", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
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
                    targetState = currentIndex,
                    transitionSpec = {
                        (fadeIn(tween(200)) + slideInVertically(initialOffsetY = { -10 })) togetherWith
                                (fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { 10 }))
                    }, label = "counter"
                ) { idx ->
                    Text(
                        text = "${idx + 1}/${if (totalQuestions > 0) totalQuestions else 1}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                val progress by animateFloatAsState(
                    targetValue = if (totalQuestions > 0) (currentIndex + 1f) / totalQuestions.toFloat() else 0f,
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
        Spacer(modifier = Modifier.size(64.dp))
    }
}

@Composable
fun QuizQuestionCard(
    questionText: String,
    answerText: String,
    helperText: String,
    showAnswer: Boolean,
    showResult: Boolean,
    lastAnswerCorrect: Boolean? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = questionText,
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color.Black, thickness = 1.dp)
            Spacer(Modifier.height(24.dp))

            if (showAnswer && showResult) {
                Text(
                    text = "Đáp án: $answerText",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (lastAnswerCorrect == true) Color(0xFF1B5E20) else Color(0xFFD32F2F),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = helperText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizOption(
    text: String,
    isSelected: Boolean,
    showResult: Boolean,
    isCorrect: Boolean,
    showAnswer: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (isPressed) 0.98f else 1f, tween(100), label = "")
    val targetColor = when {
        showResult && isCorrect && showAnswer -> Color(0xFF4CAF50)
        showResult && isSelected && !isCorrect && showAnswer -> Color(0xFFFF5252)
        isSelected -> Color(0xFFE3F2FD)
        else -> Color(0xFFF5F5F5)
    }
    val animatedBg by animateColorAsState(targetColor, tween(300), label = "")
    val bounceScale = remember { Animatable(1f) }
    LaunchedEffect(showResult, isSelected, isCorrect, showAnswer) {
        if (showResult && isSelected && isCorrect && showAnswer) {
            bounceScale.animateTo(1.05f, tween(200)); bounceScale.animateTo(1f, tween(200))
        } else {
            bounceScale.snapTo(1f)
        }
    }
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(showResult, isSelected, isCorrect, showAnswer) {
        if (showResult && isSelected && !isCorrect && showAnswer) {
            shakeX.animateTo(-10f, tween(60)); shakeX.animateTo(
                10f,
                tween(120)
            ); shakeX.animateTo(-5f, tween(80)); shakeX.animateTo(5f, tween(80)); shakeX.animateTo(
                0f,
                tween(80)
            )
        } else {
            shakeX.snapTo(0f)
        }
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = animatedBg,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(if (isPressed) 6.dp else 1.dp, RoundedCornerShape(12.dp))
            .graphicsLayer {
                scaleX = pressScale * bounceScale.value; scaleY =
                pressScale * bounceScale.value; translationX = shakeX.value
            }
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = text,
                color = Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (showResult && isCorrect && showAnswer) {
                Text(
                    "✓",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuizActionButton(
    showAnswer: Boolean,
    userAnswerIsNotBlank: Boolean,
    isCompleted: Boolean,
    showResult: Boolean,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onComplete: () -> Unit
) {
    if ((showAnswer && userAnswerIsNotBlank) || (!showAnswer && userAnswerIsNotBlank)) {
        Button(
            onClick = {
                if (isCompleted) onComplete()
                else if (!showAnswer && !showResult) onSubmit()
                else onNext()
            },
            enabled = if (showAnswer) showResult else userAnswerIsNotBlank,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (isCompleted) "Xem kết quả" else if (!showAnswer && !showResult) "Kiểm tra" else "Câu tiếp theo",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EssayQuizActionButton(
    showAnswer: Boolean,
    userAnswerIsNotBlank: Boolean,
    isCompleted: Boolean,
    showResult: Boolean,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onComplete: () -> Unit
) {
    Button(
        onClick = {
            when {
                isCompleted -> onComplete()
                showResult -> onNext()
                else -> onSubmit()
            }
        },
        enabled = when {
            isCompleted -> true
            showResult -> true
            else -> userAnswerIsNotBlank
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        )
    ) {
        Text(
            text = when {
                isCompleted -> "Xem kết quả"
                showResult -> "Câu tiếp theo"
                else -> "Kiểm tra"
            },
            fontWeight = FontWeight.Bold
        )
    }
}