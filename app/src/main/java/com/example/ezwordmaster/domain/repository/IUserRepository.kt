package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.model.UserData

/**
 * Hợp đồng thao tác với dữ liệu người dùng (Firestore). Giúp UI không biết chi tiết storage.
 */
interface IUserRepository {
    /** Tạo document mới cho user. */
    suspend fun createUser(userId: String, userData: UserData): Result<Unit>

    /** Đọc thông tin user theo id. */
    suspend fun getUser(userId: String): Result<UserData?>

    /** Cập nhật một phần dữ liệu user (merge). */
    suspend fun updateUser(userId: String, userData: Map<String, Any>): Result<Unit>

    /** Cập nhật mốc đăng nhập gần nhất. */
    suspend fun updateLastLogin(userId: String): Result<Unit>

    /** Lấy danh sách user (nếu cần cho admin). */
    suspend fun getAllUsers(): Result<List<UserData>>

    suspend fun deleteUser(userId: String): Result<Unit>

    suspend fun setUserRole(userId: String, role: String): Result<Unit>
}
