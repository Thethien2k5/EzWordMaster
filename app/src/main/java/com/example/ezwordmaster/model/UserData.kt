package com.example.ezwordmaster.model

data class UserData(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentUser: UserData? = null
)