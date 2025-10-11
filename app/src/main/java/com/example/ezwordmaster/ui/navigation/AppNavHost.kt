package com.example.ezwordmaster.ui.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.TopicManagementScreen


// **** Định nghĩa đường đi giữa các trang *****
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController() //  tạo mặc định
) {
    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        composable("intro") { IntroScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("topicmanagementscreen") { TopicManagementScreen(navController = navController) }

    }
}
