package com.example.ezwordmaster.ui.screens.admin

import android.util.Patterns
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ezwordmaster.ui.common.AuthLayout
import com.example.ezwordmaster.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    navController: NavController? = null,
    factory: ViewModelProvider.Factory,
    authViewModel: AuthViewModel = viewModel(factory = factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val authUiState by authViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }

    // Lắng nghe LỖI từ ViewModel
    LaunchedEffect(authUiState.errorMessage) {
        authUiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            authViewModel.clearError()
        }
    }

    // SỬA: Lắng nghe THÀNH CÔNG từ ViewModel
    LaunchedEffect(authUiState.successMessage) {
        authUiState.successMessage?.let { message ->
            if (message.contains("Đã gửi email")) {
                snackbarHostState.showSnackbar(message)
                authViewModel.clearSuccessMessage()
            }
        }
    }

    fun showMessage(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    // SỬA: Dùng AuthLayout
    AuthLayout(snackbarHostState = snackbarHostState) {
        // Bắt đầu nội dung Column từ đây
        Text(
            text = "Quên mật khẩu",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A)
        )

        Text(
            text = "Nhập email để nhận liên kết đặt lại mật khẩu",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color(0xFF475569)
        )

        Text(
            text = "⚠️ Kiểm tra mục Spam/Thư rác nếu chưa thấy email",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color(0xFFDC2626)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trimStart() },
            label = { Text("Email") },
            singleLine = true,
            enabled = !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                when {
                    trimmedEmail.isEmpty() -> showMessage("Vui lòng nhập email")
                    !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail)
                        .matches() -> showMessage("Email không hợp lệ. Vui lòng kiểm tra lại.")

                    else -> {
                        focusManager.clearFocus(force = true)
                        // SỬA: Chỉ gọi VM
                        authViewModel.resetPassword(trimmedEmail) {
                            // VM tự hiển thị snackbar, chỉ cần điều hướng
                            navController?.popBackStack()
                        }
                    }
                }
            },
            enabled = email.isNotBlank() && !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            )
        ) {
            if (authUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text("Gửi email đặt lại mật khẩu")
            }
        }

        TextButton(
            onClick = { navController?.popBackStack() },
            enabled = !authUiState.isLoading
        ) {
            Text("Quay lại đăng nhập")
        }
    }
}