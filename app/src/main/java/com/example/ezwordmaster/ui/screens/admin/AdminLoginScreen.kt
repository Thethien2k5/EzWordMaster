package com.example.ezwordmaster.ui.screens.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ezwordmaster.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun UserLoginScreen(
    modifier: Modifier = Modifier,
    factory: ViewModelProvider.Factory,
    authViewModel: AuthViewModel = viewModel(factory = factory),
    navController: NavController? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val authUiState by authViewModel.uiState.collectAsState()
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(authUiState.errorMessage) {
        authUiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
                authViewModel.clearError()
            }
        }

    /**
     * Hiển thị snackbar chào mừng và điều hướng về màn hình tương ứng.
     * - Nếu không truyền `next` → quay lại `home/MANAGEMENT` (tab quản lý mặc định).
     * - Nếu `next` = `home/SETTINGS` → quay về tab Settings.
     * - Nếu là route khác → điều hướng thẳng route đó.
     */
    fun navigateAfterAuth(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
        navController?.let { nav ->
            val rawNext = nav.currentBackStackEntry?.arguments?.getString("next").orEmpty()
            val destination = when {
                rawNext.isBlank() -> "home/MANAGEMENT"
                rawNext.equals("home", ignoreCase = true) -> "home/MANAGEMENT"
                rawNext.equals("settings", ignoreCase = true) -> "home/SETTINGS"
                rawNext.startsWith("home/") -> rawNext
                else -> rawNext
            }
            nav.navigate(destination) {
                popUpTo("login?next={next}") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

                val context = LocalContext.current
                val webClientId = stringResource(id = R.string.default_web_client_id)
    val googleSignInOptions = remember(webClientId) {
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(webClientId)
                        .requestEmail()
                        .build()
                }
    val googleSignInClient = remember(googleSignInOptions) {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val googleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val idToken = account.idToken
                        if (!idToken.isNullOrEmpty()) {
                            authViewModel.loginWithGoogle(idToken) {
                    navigateAfterAuth("Đăng nhập Google thành công!")
                            }
                        } else {
                scope.launch { snackbarHostState.showSnackbar("Không lấy được Google ID token") }
                        }
                    } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar(e.message ?: "Đăng nhập Google thất bại") }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFA2EAF8), Color(0xFFAAFFA7))
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        data,
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)) {
                                    append("EzWord")
                                }
                                withStyle(style = SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                                    append("Master")
                                }
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.width(144.dp)
                        )

                        Text(
                            text = "Chào mừng trở lại",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF0F172A)
                        )

                        Text(
                            text = "Đăng nhập để tiếp tục hành trình học từ vựng của bạn",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF475569)
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
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                                    )
                                }
                            },
                            enabled = !authUiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { navController?.navigate("forgot_password") },
                                enabled = !authUiState.isLoading,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Text("Quên mật khẩu?", color = Color(0xFF2196F3))
                    }
                }

                Button(
                    onClick = {
                                val emailInput = email.trim()
                                if (emailInput.isEmpty() || password.isEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Vui lòng nhập đầy đủ thông tin")
                                    }
                                    return@Button
                                }
                                focusManager.clearFocus(force = true)
                                authViewModel.login(emailInput, password) {
                                    navigateAfterAuth("Đăng nhập thành công!")
                                }
                            },
                            enabled = email.isNotBlank() && password.isNotBlank() && !authUiState.isLoading,
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
                            Text("Đăng nhập")
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = googleSignInClient.signInIntent
                                googleLauncher.launch(intent)
                            },
                            enabled = !authUiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2196F3))
                ) {
                    Text("Đăng nhập với Google")
                }
                
                        Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = { navController?.navigate("register") },
                            enabled = !authUiState.isLoading
                ) {
                    Text("Chưa có tài khoản? Đăng ký")
                }
            }
                }
            }
        }
    }
}
