package com.example.ezwordmaster.ui.navigation

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.TopicManagementScreen
import com.example.ezwordmaster.ui.quiz.QuizScreen
import com.example.ezwordmaster.ui.quiz.QuizSettingScreen
import com.example.ezwordmaster.ui.quiz.TrueFalseQuizScreen
import com.example.ezwordmaster.ui.quiz.EssayQuizScreen
import com.example.ezwordmaster.ui.quiz.MultiChoiceQuizScreen
import com.example.ezwordmaster.ui.quiz.QuizResultScreen


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
        }

    }
}
