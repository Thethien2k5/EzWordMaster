package com.example.ezwordmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ezwordmaster.model.MainTab
import com.example.ezwordmaster.ui.ViewModelFactory
import com.example.ezwordmaster.ui.common.AuthState
import com.example.ezwordmaster.ui.common.RedirectToLogin
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
import com.example.ezwordmaster.ui.screens.admin.ForgotPasswordScreen
import com.example.ezwordmaster.ui.screens.admin.RegisterScreen
import com.example.ezwordmaster.ui.screens.admin.UserLoginScreen

@Composable
fun AppNavHost(
    factory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home/MANAGEMENT"
    ) {
        composable(
            route = "login?next={next}",
            arguments = listOf(
                navArgument("next") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            UserLoginScreen(navController = navController, factory = factory)
        }

        composable("register") {
            RegisterScreen(navController = navController, factory = factory)
        }

        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController, factory = factory)
        }

        //*** ======= HOME và VÀI THỨ KHÁC ======= ***///
        composable("intro") { IntroScreen(navController = navController) }

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
        composable("about") { AboutScreen(navController = navController) }
        composable("help") { HelpScreen(navController = navController) }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel(factory = factory),
                factory = factory
            )
        }
        composable("notificationscreen") {
            NotificationScreen(navController = navController)
        }


        //*** ======= QUẢN LÝ CHỦ ĐỀ ======= ***///
        //danh sách chủ đề
        composable("topicmanagementscreen") {
            TopicManagementScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }
        // chỉnh sửa chủ đề cụ để
        composable("edittopic/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            EditTopicScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }

        // ======= ÔN TẬP =======///
        // chọn chủ đề muốn ôn tập
        composable("practice") {
            PracticeScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }
        // lịch sử
        composable("studyhistory") {
            StudyHistoryScreen(
                navController = navController,
                viewModel = viewModel(factory = factory)
            )
        }
        // chọn chế độ ôn tập / lịch sử ôn tập
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
            val learningWords =
                backStackEntry.arguments?.getString("learningWords")?.toIntOrNull() ?: 0
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
        // chế độ ôn tập flash card
        composable("flashcard/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            FlashcardScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }
        //================= LẬT THẺ ===========================
        // chế độ ôn tập lật thẻ
        composable("wordselection/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordSelectionScreen(
                navController = navController,
                topicId = topicId,
                viewModel = viewModel(factory = factory)
            )
        }
        // bắt đầu lật thẻ
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
        //kết quả thể thẻ
        composable("flipresult/{topicId}/{topicName}/{matchedPairs}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val topicName = backStackEntry.arguments?.getString("topicName")
            val matchedPairs =
                backStackEntry.arguments?.getString("matchedPairs")?.toIntOrNull() ?: 0
            FlipResultScreen(
                navController = navController,
                topicId = topicId,
                topicName = topicName,
                matchedPairs = matchedPairs
            )
        }

        //*** ======= ÔN TẬP MỞ RỘNG QUIZ ======= ***///
        // Màn hình Đúng/Sai
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
        // Màn hình Tự luận
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
        // Màn hình Nhiều lựa chọn
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