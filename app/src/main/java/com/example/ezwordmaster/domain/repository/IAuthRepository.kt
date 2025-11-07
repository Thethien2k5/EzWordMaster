package com.example.ezwordmaster.domain.repository

import com.google.firebase.auth.FirebaseUser

/**
 * Abstraction for every authentication action. ViewModel chỉ làm việc với interface này
 * nên dễ dàng mock trong unit test hoặc thay đổi backend sau này.
 */
interface IAuthRepository {
    /** Người dùng hiện tại (null nếu chưa đăng nhập). */
    val currentUser: FirebaseUser?

    /** Convenience flag xem có FirebaseUser hay không. */
    val isLoggedIn: Boolean

    /** Đăng nhập bằng email/password FirebaseAuth. */
    suspend fun login(email: String, password: String): Result<FirebaseUser>

    /** Tạo tài khoản mới với email/password. */
    suspend fun register(email: String, password: String, username: String): Result<FirebaseUser>

    /** Đăng nhập bằng Google ID token. */
    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser>

    /** Đăng xuất FirebaseAuth. */
    suspend fun logout(): Result<Unit>

    /** Gửi email reset password. */
    suspend fun resetPassword(email: String): Result<Unit>

    /** Đổi mật khẩu cho user đang đăng nhập. */
    suspend fun updatePassword(newPassword: String): Result<Unit>
}


