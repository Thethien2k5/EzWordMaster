package com.example.ezwordmaster.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ezwordmaster.ui.screens.topic_managment.TopicViewModel
import com.example.ezwordmaster.ui.screens.translationScreen.TranslationScreen
import com.example.ezwordmaster.ui.screens.translationScreen.TranslationViewModel

/**
 * Component popup dịch thuật với hiệu ứng bubble chat
 * Sử dụng ở TopicManagementScreen và EditTopicScreen
 */
@Composable
fun TranslationPopup(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: TranslationViewModel,
    topicViewModel: TopicViewModel // THÊM parameter
) {
    // Hiệu ứng scale và fade khi xuất hiện
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(200)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        exit = fadeOut(animationSpec = tween(150)) +
                scaleOut(targetScale = 0.9f, animationSpec = tween(150))
    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Container chính với hiệu ứng bubble
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {} // Prevent closing when clicking inside
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Nội dung màn hình dịch
                        TranslationScreen(
                            onBackClick = onDismiss,
                            viewModel = viewModel,
                            topicViewModel = topicViewModel
                        )

//                        // Nút đóng (X) ở góc trên phải
//                        IconButton(
//                            onClick = onDismiss,
//                            modifier = Modifier
//                                .align(Alignment.TopEnd)
//                                .padding(8.dp)
//                                .size(40.dp)
//                                .clip(CircleShape)
//                                .background(Color.Black.copy(alpha = 0.1f))
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "Đóng",
//                                tint = Color.Black.copy(alpha = 0.7f)
//                            )
//                        }
                    }
                }
            }
        }
    }
}

/**
 * Nút floating để mở popup dịch
 * Thiết kế bubble tròn với hiệu ứng pulse
 */
@Composable
fun TranslationFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hiệu ứng pulse nhẹ
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .scale(scale),
        containerColor = Color(0xFF00BCD4),
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        // Icon dịch thuật
        Text(
            text = "📖",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}