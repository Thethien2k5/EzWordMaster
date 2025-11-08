package com.example.ezwordmaster.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ezwordmaster.ui.ViewModelFactory
import com.example.ezwordmaster.ui.screens.translationScreen.TranslationViewModel

/**
 * Helper class để quản lý TranslationViewModel độc lập
 * Tránh truyền quá nhiều dependencies vào MainHomeScreen
 */
object TranslationHelper {
    /**
     * Tạo hoặc lấy instance của TranslationViewModel
     * Sử dụng trong composable với ViewModelFactory
     */
    @Composable
    fun rememberTranslationViewModel(
        factory: ViewModelFactory
    ): TranslationViewModel {
        return viewModel(factory = factory)
    }
}

/**
 * State holder cho Translation Popup
 * Quản lý việc mở/đóng popup
 */
@Composable
fun rememberTranslationPopupState(): MutableState<Boolean> {
    return remember { mutableStateOf(false) }
}

/**
 * Extension function để dễ dàng show/hide popup
 */
fun MutableState<Boolean>.show() {
    this.value = true
}

fun MutableState<Boolean>.hide() {
    this.value = false
}

fun MutableState<Boolean>.toggle() {
    this.value = !this.value
}