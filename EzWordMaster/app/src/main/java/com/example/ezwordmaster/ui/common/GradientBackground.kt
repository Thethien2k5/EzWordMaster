package com.example.ezwordmaster.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Định nghĩa 2 màu để tạo hiệu ứng chuyển màu
val gradientStartColor = Color(0xFFE0F7FA) // Xanh dương rất nhạt
val gradientEndColor = Color(0xFFAAF0D1)   // Xanh mint

@Composable
fun GradientBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientStartColor,
                        gradientEndColor
                    )
                )
            )
    ) {
        content()
    }
}