package com.example.ezwordmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.ui.about.AboutScreen
import com.example.ezwordmaster.ui.help.HelpScreen
import com.example.ezwordmaster.ui.screens.HomeScreen
import com.example.ezwordmaster.ui.screens.IntroScreen
import com.example.ezwordmaster.ui.screens.TopicManagementScreen
import com.example.ezwordmaster.ui.translate.TranslateScreen

/**
 * Quản lý tất cả các đường dẫn (route) trong ứng dụng.
 * Đưa ra ngoài object để dễ dàng truy cập từ mọi nơi.
 */
object Routes {
    // --- Các route cũ của bạn ---
    const val INTRO = "intro"
    const val HOME = "home"
    const val TOPIC_MANAGEMENT = "topicmanagementscreen"

    // --- Các route mới chúng ta đang làm ---
    const val ABOUT = "about"
    const val HELP = "help"
    const val TRANSLATE = "translate"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController() //  tạo mặc định
) {
    NavHost(
        navController = navController,
        startDestination = Routes.INTRO
    ) {
        composable(Routes.INTRO) { IntroScreen(navController = navController) }
        composable(Routes.HOME) { HomeScreen(navController = navController) }
        composable(Routes.TOPIC_MANAGEMENT) { TopicManagementScreen(navController = navController) }

        // ================== CÁC MÀN HÌNH MỚI THÊM VÀO ==================
        // Màn hình About Us
        composable(Routes.ABOUT) {
            AboutScreen(navController = navController)
        }

        // Màn hình Help
        composable(Routes.HELP) {
            HelpScreen(navController = navController)
        }

        // Các màn hình khác như Dịch, Cài đặt sẽ được thêm vào đây
        composable(Routes.TRANSLATE) {
            TranslateScreen(navController = navController)
        }
    }
}

// ✅ HÀM PREVIEW GÂY LỖI ĐÃ ĐƯỢC SỬA Ở ĐÂY
// Thêm hàm này vào cuối file để Android Studio không báo lỗi nữa
@Preview(showBackground = true)
@Composable
fun AppNavHostPreview() {
    AppNavHost(navController = rememberNavController())
}
