package com.example.ezwordmaster.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.ezwordmaster.model.UserData

object AuthState {
    val isLoggedIn = mutableStateOf(false)
    val userName = mutableStateOf<String?>(null)

    fun setFromFirebaseUser(user: UserData?) {
        if (user != null) {
            isLoggedIn.value = true
            val name = user.displayName?.takeIf { it.isNotBlank() }
                ?: user.username?.takeIf { it.isNotBlank() }
                ?: user.email
            userName.value = name
        } else {
            isLoggedIn.value = false
            userName.value = null
        }
    }
}

@Composable
fun RequireLogin(navController: NavController, content: @Composable () -> Unit) {
    val loggedIn = AuthState.isLoggedIn.value
    if (loggedIn) {
        content()
        return
    }
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val alreadyOnLogin = currentRoute == "login"
    val hasNavigated = remember { mutableStateOf(false) }
    LaunchedEffect(alreadyOnLogin, hasNavigated.value) {
        if (!alreadyOnLogin && !hasNavigated.value) {
            hasNavigated.value = true
            navController.navigate("login") {
                launchSingleTop = true
            }
        }
    }
}


