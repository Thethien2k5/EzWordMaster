package com.example.ezwordmaster.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RedirectToLogin(navController: NavController, nextRoute: String) {
    val triggered = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!triggered.value) {
            triggered.value = true
            val encoded = URLEncoder.encode(nextRoute, StandardCharsets.UTF_8.name())
            navController.navigate("login?next=$encoded") {
                launchSingleTop = true
            }
        }
    }
}