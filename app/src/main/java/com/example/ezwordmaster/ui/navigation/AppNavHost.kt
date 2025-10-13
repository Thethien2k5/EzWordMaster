package com.example.ezwordmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.ui.screens.about.AboutScreen
import com.example.ezwordmaster.ui.screens.help.HelpScreen
import com.example.ezwordmaster.ui.screens.notification.NotificationScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.topic_managment.TopicManagementScreen
import com.example.ezwordmaster.ui.screens.settings.SettingsScreen
import com.example.ezwordmaster.ui.screens.translate.TranslateScreen
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        composable("intro") { IntroScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("topicmanagementscreen") { TopicManagementScreen(navController = navController) }
        composable("about") { AboutScreen(navController = navController) }
        composable("help") { HelpScreen(navController = navController) }
        composable("translate") { TranslateScreen(navController = navController) }
        composable("settings") { SettingsScreen(navController = navController) }
        composable("notification") { NotificationScreen(navController = navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavHostPreview() {
    AppNavHost(navController = rememberNavController())
}