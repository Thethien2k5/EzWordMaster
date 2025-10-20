package com.example.ezwordmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.ui.about.AboutScreen
import com.example.ezwordmaster.ui.help.HelpScreen
import com.example.ezwordmaster.ui.notification.NotificationScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.TopicManagementScreen
import com.example.ezwordmaster.ui.settings.SettingsScreen
import com.example.ezwordmaster.ui.translate.TranslateScreen

object Routes {
    const val INTRO = "intro"
    const val HOME = "home"
    const val TOPIC_MANAGEMENT = "topicmanagementscreen"
    const val ABOUT = "about"
    const val HELP = "help"
    const val TRANSLATE = "translate"
    const val SETTINGS = "settings"
    const val NOTIFICATION = "notification"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.INTRO
    ) {
        composable(Routes.INTRO) { IntroScreen(navController = navController) }
        composable(Routes.HOME) { HomeScreen(navController = navController) }
        composable(Routes.TOPIC_MANAGEMENT) { TopicManagementScreen(navController = navController) }
        composable(Routes.ABOUT) { AboutScreen(navController = navController) }
        composable(Routes.HELP) { HelpScreen(navController = navController) }
        composable(Routes.TRANSLATE) { TranslateScreen(navController = navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController = navController) }

        // THAY ĐỔI: Inject ViewModel vào màn hình Notification
        composable(Routes.NOTIFICATION) {
            NotificationScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
    }
}