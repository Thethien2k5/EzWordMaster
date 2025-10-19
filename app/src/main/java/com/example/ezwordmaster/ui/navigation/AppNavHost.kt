package com.example.ezwordmaster.ui.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.TopicManagementScreen
import com.example.ezwordmaster.ui.screens.PracticeScreen
import com.example.ezwordmaster.ui.screens.WordPracticeScreen
import com.example.ezwordmaster.ui.screens.FlashcardScreen
import com.example.ezwordmaster.ui.screens.ResultScreen
import com.example.ezwordmaster.ui.screens.WordSelectionScreen
import com.example.ezwordmaster.ui.screens.FlipCardScreen
import com.example.ezwordmaster.ui.screens.FlipResultScreen
import com.example.ezwordmaster.ui.screens.StudyHistoryScreen


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
        composable("practice") { PracticeScreen(navController = navController) }
        composable("wordpractice/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordPracticeScreen(navController = navController, topicId = topicId)
        }
        composable("flashcard/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            FlashcardScreen(navController = navController, topicId = topicId)
        }
        composable("result/{topicId}/{knownWords}/{learningWords}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val knownWords = backStackEntry.arguments?.getString("knownWords")?.toIntOrNull() ?: 0
            val learningWords = backStackEntry.arguments?.getString("learningWords")?.toIntOrNull() ?: 0
            ResultScreen(navController = navController, topicId = topicId, knownWords = knownWords, learningWords = learningWords)
        }
        composable("wordselection/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            WordSelectionScreen(navController = navController, topicId = topicId)
        }
        composable("flipcard/{topicId}/{wordsJson}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val wordsJson = backStackEntry.arguments?.getString("wordsJson")
            FlipCardScreen(navController = navController, topicId = topicId, wordsJson = wordsJson)
        }
        composable("flipresult/{topicId}/{flippedCount}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            val flippedCount = backStackEntry.arguments?.getString("flippedCount")?.toIntOrNull() ?: 0
            FlipResultScreen(navController = navController, topicId = topicId, flippedCount = flippedCount)
        }
        composable("studyhistory") { 
            StudyHistoryScreen(navController = navController) 
        }

    }
}
