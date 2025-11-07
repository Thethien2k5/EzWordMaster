package com.example.ezwordmaster.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IAuthRepository
import com.example.ezwordmaster.domain.repository.IUserRepository
import com.example.ezwordmaster.model.AuthUiState
import com.example.ezwordmaster.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    // Sử dụng AuthUiState đã được cập nhật (với successMessage)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    /** Đồng bộ trạng thái khi ViewModel khởi tạo (ví dụ user đã đăng nhập từ lần trước). */
    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                // Sửa: Lấy UserData từ Firestore
                var userData = userRepository.getUser(currentUser.uid).getOrNull()

                // Sửa: Nếu không có hoặc thiếu info, tạo/cập nhật từ Firebase Auth
                if (userData == null || userData.displayName.isBlank() || userData.photoUrl.isNullOrBlank()) {
                    userData = UserData(
                        userId = currentUser.uid,
                        email = currentUser.email.orEmpty(),
                        username = currentUser.displayName.orEmpty(),
                        displayName = currentUser.displayName.orEmpty(),
                        photoUrl = currentUser.photoUrl?.toString()
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    currentUser = userData
                )
            } else {
                _uiState.value = AuthUiState()
            }
        }
    }

    /** Đăng nhập truyền thống, trả kết quả qua callback onSuccess CHỈ để điều hướng. */
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Reset state, sẵn sàng cho request mới
            _uiState.value =
                _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)

            val result = authRepository.login(email, password)
            result.onSuccess { firebaseUser ->
                // Update last login
                userRepository.updateLastLogin(firebaseUser.uid)

                // Get user data
                val userData = userRepository.getUser(firebaseUser.uid).getOrNull()
                    ?: UserData(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email.orEmpty(),
                        username = firebaseUser.displayName.orEmpty(),
                        photoUrl = firebaseUser.photoUrl?.toString(),
                        displayName = firebaseUser.displayName.orEmpty()
                    )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUser = userData,
                    successMessage = "Đăng nhập thành công!" // <- Set success message
                )
                // Đã xóa: AuthState.setFromFirebaseUser(userData)
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Đăng nhập thất bại"
                )
            }
        }
    }

    /** Tạo tài khoản mới, sau đó logout để buộc user đăng nhập lại. */
    fun register(email: String, password: String, username: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)

            val result = authRepository.register(email, password, username) //
            result.onSuccess { firebaseUser ->
                // Create user data in Firestore
                val userData = UserData(
                    userId = firebaseUser.uid,
                    email = email,
                    username = username,
                    displayName = username,
                    role = "user"
                )
                userRepository.createUser(firebaseUser.uid, userData) //

                // Sau khi tạo tài khoản mới, đăng xuất để yêu cầu đăng nhập lại
                authRepository.logout() //
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    currentUser = null,
                    successMessage = "Đăng ký thành công! Vui lòng đăng nhập." // <- Set success message
                )
                // Đã xóa: AuthState.setFromFirebaseUser(null)
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Đăng ký thất bại"
                )
            }
        }
    }

    /** Đăng nhập Google, đảm bảo document người dùng tồn tại trong Firestore. */
    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)

            val result = authRepository.loginWithGoogle(idToken) //
            result.onSuccess { firebaseUser ->
                // Ensure user document exists
                val existing = userRepository.getUser(firebaseUser.uid).getOrNull() //
                val resolvedUser = if (existing == null) {
                    val userData = UserData(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email.orEmpty(),
                        username = firebaseUser.displayName.orEmpty(),
                        displayName = firebaseUser.displayName.orEmpty(),
                        role = "user"
                    )
                    userRepository.createUser(firebaseUser.uid, userData) //
                    userData
                } else existing

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    currentUser = resolvedUser,
                    successMessage = "Đăng nhập Google thành công!" // <- Set success message
                )
                // Đã xóa: AuthState.setFromFirebaseUser(resolvedUser)
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Đăng nhập Google thất bại"
                )
            }
        }
    }

    /** Gửi email đặt lại mật khẩu, xử lý message lỗi cụ thể. */
    fun resetPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)

            val result = authRepository.resetPassword(email) //
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư!" // <- Set success message
                )
                onSuccess()
            }.onFailure { exception ->
                val errorMsg = when {
                    exception.message?.contains("user-not-found", ignoreCase = true) == true ->
                        "Không tìm thấy tài khoản với email này. Vui lòng kiểm tra lại email."

                    exception.message?.contains("invalid-email", ignoreCase = true) == true ->
                        "Email không hợp lệ. Vui lòng kiểm tra lại."

                    exception.message?.contains("network", ignoreCase = true) == true ->
                        "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet và thử lại."

                    else -> exception.message
                        ?: "Gửi email đặt lại mật khẩu thất bại. Vui lòng thử lại sau."
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    /** Đăng xuất và reset UI state. */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout() //
            _uiState.value = AuthUiState() // Reset về trạng thái ban đầu
            // Đã xóa: AuthState.setFromFirebaseUser(null)
        }
    }

    /** Xóa message lỗi sau khi hiển thị snackbar. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /** Xóa message thành công sau khi hiển thị snackbar. */
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}