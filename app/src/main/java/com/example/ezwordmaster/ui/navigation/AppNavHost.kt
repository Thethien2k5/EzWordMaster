package com.example.ezwordmaster.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.ui.screens.about.AboutScreen
import com.example.ezwordmaster.ui.screens.help.HelpScreen
import com.example.ezwordmaster.ui.screens.notification.NotificationScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.quiz.QuizScreen
import com.example.ezwordmaster.ui.screens.quiz.QuizSettingScreen
import com.example.ezwordmaster.ui.screens.quiz.TrueFalseQuizScreen
import com.example.ezwordmaster.ui.screens.quiz.EssayQuizScreen
import com.example.ezwordmaster.ui.screens.quiz.MultiChoiceQuizScreen
import com.example.ezwordmaster.ui.screens.quiz.QuizResultScreen
import com.example.ezwordmaster.ui.screens.topic_managment.TopicManagementScreen
import com.example.ezwordmaster.ui.screens.practice.PracticeScreen
import com.example.ezwordmaster.ui.screens.practice.WordPracticeScreen
import com.example.ezwordmaster.ui.screens.practice.FlashcardScreen
import com.example.ezwordmaster.ui.screens.practice.ResultScreen
import com.example.ezwordmaster.ui.screens.practice.WordSelectionScreen
import com.example.ezwordmaster.ui.screens.practice.FlipCardScreen
import com.example.ezwordmaster.ui.screens.practice.FlipResultScreen
import com.example.ezwordmaster.ui.screens.history.StudyHistoryScreen
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.settings.SettingsScreen
import com.example.ezwordmaster.ui.screens.topic_managment.EditTopicScreen
import com.example.ezwordmaster.ui.ViewModelFactory

@Composable
fun AppNavHost(
    factory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        //*** ======= HOME và VÀI THỨ KHÁC ======= ***///
        composable("intro") { IntroScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("about") { AboutScreen(navController = navController) }
        composable("help") { HelpScreen(navController = navController) }
//        composable("translate") { TranslateScreen(navController = navController) }
        composable("settings") { SettingsScreen(navController = navController) }
        composable("notificationscreen") { NotificationScreen(navController = navController) }


        //*** ======= QUẢN LÝ CHỦ ĐỀ ======= ***///
        //danh sách chủ đề
        composable("topicmanagementscreen") { TopicManagementScreen(navController = navController, viewModel = viewModel(factory = factory)) }
        // chỉnh sửa chủ đề cụ để
        composable("edittopic/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            EditTopicScreen(navController = navController, topicId = topicId, viewModel = viewModel(factory = factory))
        }

        // ======= ÔN TẬP =======///
        // chọn chủ đề muốn ôn tập
        composable("practice") { PracticeScreen(navController = navController, viewModel = viewModel(factory = factory)) }
        // chọn chế độ ôn tập / lịch sử ôn tập
        composable("wordpractice/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordPracticeScreen(navController = navController, topicId = topicId, viewModel = viewModel(factory = factory))
        }
        //======== FLASH CARD =================
        // chế độ ôn tập flash card
        composable("flashcard/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            FlashcardScreen(navController = navController, topicId = topicId, viewModel = viewModel(factory = factory))
        }
        // kết quả flashcard
        composable("result/{topicId}/{knownWords}/{learningWords}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val knownWords = backStackEntry.arguments?.getString("knownWords")?.toIntOrNull() ?: 0
            val learningWords = backStackEntry.arguments?.getString("learningWords")?.toIntOrNull() ?: 0
            ResultScreen(navController = navController, topicId = topicId, knownWords = knownWords, learningWords = learningWords, viewModel = viewModel(factory = factory))
        }
        //================= LẬT THẺ ===========================
        // chế độ ôn tập lật thẻ
        composable("wordselection/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordSelectionScreen(navController = navController, topicId = topicId,viewModel = viewModel(factory = factory))
        }
        // bắt đầu lật thẻ
        composable("flipcard/{topicId}/{wordsJson}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val wordsJson = backStackEntry.arguments?.getString("wordsJson")
            FlipCardScreen(navController = navController, topicId = topicId, wordsJson = wordsJson, viewModel = viewModel(factory = factory))
        }
        //kết quả thể thẻ
        composable("flipresult/{topicId}/{flippedCount}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val flippedCount = backStackEntry.arguments?.getString("flippedCount")?.toIntOrNull() ?: 0
            FlipResultScreen(navController = navController, topicId = topicId, flippedCount = flippedCount)
        }

        //*** ======= QUIZ ======= ***///
        composable("quiz_setting") { QuizSettingScreen(navController) }
        composable("quiz") { QuizScreen() }
        composable("quiz_true_false/{showAnswer}") { backStackEntry ->
            val showAnswer = backStackEntry.arguments?.getString("showAnswer")?.toBoolean() ?: true
            TrueFalseQuizScreen(navController, showAnswer)
        }
        composable("quiz_essay/{showAnswer}") { backStackEntry ->
            val showAnswer = backStackEntry.arguments?.getString("showAnswer")?.toBoolean() ?: true
            EssayQuizScreen(navController, showAnswer)
        }
        composable("quiz_multi/{showAnswer}") { backStackEntry ->
            val showAnswer = backStackEntry.arguments?.getString("showAnswer")?.toBoolean() ?: true
            MultiChoiceQuizScreen(navController, showAnswer)
        }
        composable("quiz_result/{score}/{total}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            // Tạm thời truyền danh sách rỗng, sẽ cập nhật sau
            QuizResultScreen(navController, score, total, emptyList())


        //*** ======= LỊCH SỬ ======= ***///
        composable("studyhistory") {
            StudyHistoryScreen(navController = navController)
        }
    }
}
}