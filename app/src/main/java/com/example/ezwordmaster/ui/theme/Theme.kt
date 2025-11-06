package com.example.ezwordmaster.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
// import androidx.compose.material3.Typography // <-- Dòng này phải bị xóa hoặc comment đi

@Composable
fun EzWordMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography, // <-- PHẢI LÀ "AppTypography"
        content = content
    )
}