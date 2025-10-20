package com.example.ezwordmaster.ui.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.topic_managment.TopicManagementScreen
import com.example.ezwordmaster.ui.screens.practice.PracticeScreen
import com.example.ezwordmaster.ui.screens.practice.WordPracticeScreen
import com.example.ezwordmaster.ui.screens.practice.FlashcardScreen
import com.example.ezwordmaster.ui.screens.practice.ResultScreen
import com.example.ezwordmaster.ui.screens.practice.WordSelectionScreen
import com.example.ezwordmaster.ui.screens.practice.FlipCardScreen
import com.example.ezwordmaster.ui.screens.practice.FlipResultScreen
import com.example.ezwordmaster.ui.screens.history.StudyHistoryScreen


// **** Định nghĩa đường đi giữa các trang *****
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController() //  tạo mặc định
) {
    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        //*** ======= HOME ======= ***///
        composable("intro") { IntroScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController) }

        //*** ======= QUẢN LÝ CHỦ ĐỀ ======= ***///
        //danh sách chủ đề
        composable("topicmanagementscreen") { TopicManagementScreen(navController = navController) }

        //*** ======= ÔN TẬP ======= ***///
        // chọn chủ đề muốn ôn tập
        composable("practice") { PracticeScreen(navController = navController) }
        // chọn chế độ ôn tập / lịch sử ôn tập
        composable("wordpractice/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordPracticeScreen(navController = navController, topicId = topicId)
        }
        // chế độ ôn tập flash card
        composable("flashcard/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            FlashcardScreen(navController = navController, topicId = topicId)
        }
        // kết quả flashcard
        composable("result/{topicId}/{knownWords}/{learningWords}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val knownWords = backStackEntry.arguments?.getString("knownWords")?.toIntOrNull() ?: 0
            val learningWords = backStackEntry.arguments?.getString("learningWords")?.toIntOrNull() ?: 0
            ResultScreen(navController = navController, topicId = topicId, knownWords = knownWords, learningWords = learningWords)
        }

        // chế độ ôn tập lật thẻ
        composable("wordselection/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordSelectionScreen(navController = navController, topicId = topicId)
        }
        // bắt đầu lật thẻ
        composable("flipcard/{topicId}/{wordsJson}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val wordsJson = backStackEntry.arguments?.getString("wordsJson")
            FlipCardScreen(navController = navController, topicId = topicId, wordsJson = wordsJson)
        }
        //kết quả thể thẻ
        composable("flipresult/{topicId}/{flippedCount}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val flippedCount = backStackEntry.arguments?.getString("flippedCount")?.toIntOrNull() ?: 0
            FlipResultScreen(navController = navController, topicId = topicId, flippedCount = flippedCount)
        }

        //*** ======= LỊCH SỬ ======= ***///
        composable("studyhistory") {
            StudyHistoryScreen(navController = navController)
        }
    }
}
