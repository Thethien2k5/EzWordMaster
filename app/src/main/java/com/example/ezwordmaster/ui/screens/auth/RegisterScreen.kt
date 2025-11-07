package com.example.ezwordmaster.ui.screens.admin

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ezwordmaster.ui.common.AuthLayout // <- SỬA: Import AuthLayout
import com.example.ezwordmaster.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController? = null,
    factory: ViewModelProvider.Factory,
    authViewModel: AuthViewModel = viewModel(factory = factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val authUiState by authViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

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
            if (message.contains("Đăng ký")) {
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
            text = "Tạo tài khoản",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A)
        )

        Text(
            text = "Nhập thông tin của bạn để bắt đầu học cùng EzWordMaster",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color(0xFF475569)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it.trimStart() },
            label = { Text("Tên người dùng") },
            singleLine = true,
            enabled = !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trimStart() },
            label = { Text("Email") },
            singleLine = true,
            enabled = !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                    )
                }
            },
            enabled = !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Xác nhận mật khẩu") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                    )
                }
            },
            enabled = !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val trimmedUsername = username.trim()
                val trimmedEmail = email.trim()
                when {
                    trimmedUsername.isEmpty() || trimmedEmail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                        showMessage("Vui lòng điền đầy đủ thông tin")
                    }

                    password != confirmPassword -> {
                        showMessage("Mật khẩu xác nhận không khớp")
                    }

                    password.length < 6 -> {
                        showMessage("Mật khẩu phải có ít nhất 6 ký tự")
                    }

                    else -> {
                        focusManager.clearFocus(force = true)
                        // SỬA: Chỉ gọi VM
                        authViewModel.register(
                            trimmedEmail,
                            password,
                            trimmedUsername
                        ) {
                            // VM sẽ tự hiển thị snackbar, chỉ cần điều hướng
                            navController?.navigate("login") {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            },
            enabled = username.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    !authUiState.isLoading,
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
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text("Đăng ký")
        }

        TextButton(
            onClick = { navController?.popBackStack() },
            enabled = !authUiState.isLoading
        ) {
            Text("Đã có tài khoản? Đăng nhập")
        }
    }
}