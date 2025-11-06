package com.example.ezwordmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.model.MainTab
import com.example.ezwordmaster.ui.ViewModelFactory
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.MainHomeScreen
import com.example.ezwordmaster.ui.screens.about.AboutScreen
import com.example.ezwordmaster.ui.screens.help.HelpScreen
import com.example.ezwordmaster.ui.screens.history.StudyHistoryScreen
import com.example.ezwordmaster.ui.screens.notification.NotificationScreen
import com.example.ezwordmaster.ui.screens.regime.PracticeScreen
import com.example.ezwordmaster.ui.screens.regime.PracticeViewModel
import com.example.ezwordmaster.ui.screens.regime.ResultScreen
import com.example.ezwordmaster.ui.screens.regime.WordPracticeScreen
import com.example.ezwordmaster.ui.screens.regime.WordSelectionScreen
import com.example.ezwordmaster.ui.screens.regime.entertainment.FlipCardScreen
import com.example.ezwordmaster.ui.screens.regime.entertainment.FlipResultScreen
import com.example.ezwordmaster.ui.screens.regime.practice.flash.FlashcardScreen
import com.example.ezwordmaster.ui.screens.regime.practice.quiz.EssayQuizScreen
import com.example.ezwordmaster.ui.screens.regime.practice.quiz.MultiChoiceQuizScreen
import com.example.ezwordmaster.ui.screens.regime.practice.quiz.TrueFalseQuizScreen
import com.example.ezwordmaster.ui.screens.settings.SettingsScreen
import com.example.ezwordmaster.ui.screens.settings.SettingsViewModel
import com.example.ezwordmaster.ui.screens.topic_managment.EditTopicScreen
import com.example.ezwordmaster.ui.screens.topic_managment.TopicManagementScreen
import com.example.ezwordmaster.ui.screens.topic_managment.TopicViewModel
import com.example.ezwordmaster.ui.screens.translation.TranslationScreen

@Composable
fun AppNavHost(
    factory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home/MANAGEMENT"
    ) {
        //*** ======= HOME và VÀI THỨ KHÁC ======= ***///
        composable("intro") {
            IntroScreen(navController = navController)
        }

        composable("home/{selectedTab}") { backStackEntry ->
            val selectedTabString = backStackEntry.arguments?.getString("selectedTab")
            val initialTab = when (selectedTabString) {
                "PRACTICE" -> MainTab.PRACTICE
                "SETTINGS" -> MainTab.SETTINGS
                else -> MainTab.MANAGEMENT
            }
            MainHomeScreen(
                navController = navController,
                topicViewModel = viewModel<TopicViewModel>(factory = factory),
                practiceViewModel = viewModel<PracticeViewModel>(factory = factory),
                settingsViewModel = viewModel<SettingsViewModel>(factory = factory),
                factory = factory,
                initialTab = initialTab
            )
        }
        composable("about") {
            AboutScreen(navController = navController)
        }
        composable("help") {
            HelpScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }

        //*** ======= THÊM MÀN HÌNH MỚI ======= ***///
        // SỬA TỪ "notificationscreen" THÀNH "notifications"
        composable("notifications") {
            val viewModel = viewModel<com.example.ezwordmaster.ui.screens.notification.NotificationViewModel>(factory = factory)
            NotificationScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable("translation") {
            val viewModel = viewModel<com.example.ezwordmaster.ui.screens.translation.TranslationViewModel>(factory = factory)
            TranslationScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        //*** ======= QUẢN LÝ CHỦ ĐỀ ======= ***///
        composable("topicmanagementscreen") {
            TopicManagementScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("edittopic/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            EditTopicScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }

        // ======= ÔN TẬP =======///
        composable("practice") {
            PracticeScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("studyhistory") {
            StudyHistoryScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("wordpractice/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordPracticeScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }

        // Màn hình kết quả (DÙNG CHUNG CHO CẢ FLASHCARD VÀ QUIZ)
        composable("result/{topicId}/{topicName}/{knownWords}/{learningWords}/{totalWords}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val topicName = backStackEntry.arguments?.getString("topicName")
            val knownWords = backStackEntry.arguments?.getString("knownWords")?.toIntOrNull() ?: 0
            val learningWords = backStackEntry.arguments?.getString("learningWords")?.toIntOrNull() ?: 0
            val totalWords = backStackEntry.arguments?.getString("totalWords")?.toIntOrNull() ?: 0
            ResultScreen(
                navController = navController,
                topicId = topicId,
                topicName = topicName,
                knownWords = knownWords,
                learningWords = learningWords,
                totalWords = totalWords,
                viewModel = viewModel(factory = factory)
            )
        }

        //======== FLASH CARD =================
        composable("flashcard/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            FlashcardScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }

        //================= LẬT THẺ ===========================
        composable("wordselection/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordSelectionScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("flipcard/{topicId}/{wordsJson}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val wordsJson = backStackEntry.arguments?.getString("wordsJson")
            FlipCardScreen(
                navController = navController,
                topicId = topicId,
                wordsJson = wordsJson,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("flipresult/{topicId}/{topicName}/{matchedPairs}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val topicName = backStackEntry.arguments?.getString("topicName")
            val matchedPairs = backStackEntry.arguments?.getString("matchedPairs")?.toIntOrNull() ?: 0

            FlipResultScreen(
                navController = navController,
                topicId = topicId,
                topicName = topicName,
                matchedPairs = matchedPairs
            )
        }

        //*** ======= ÔN TẬP MỞ RỘNG QUIZ ======= ***///
        composable("practice_quiz_truefalse/{topicId}/{showAnswer}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val showAnswer = backStackEntry.arguments?.getString("showAnswer")?.toBoolean() ?: true
            TrueFalseQuizScreen(
                navController,
                topicId,
                showAnswer,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("practice_quiz_essay/{topicId}/{showAnswer}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val showAnswer = backStackEntry.arguments?.getString("showAnswer")?.toBoolean() ?: true
            EssayQuizScreen(
                navController,
                topicId,
                showAnswer,
                viewModel = viewModel(factory = factory)
            )
        }

        composable("practice_quiz_multi/{topicId}/{showAnswer}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val showAnswer = backStackEntry.arguments?.getString("showAnswer")?.toBoolean() ?: true
            MultiChoiceQuizScreen(
                navController,
                topicId,
                showAnswer,
                viewModel = viewModel(factory = factory)
            )
        }
    }
}