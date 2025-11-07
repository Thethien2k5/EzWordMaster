package com.example.ezwordmaster.model

data class UserData(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val displayName: String = "",
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)



