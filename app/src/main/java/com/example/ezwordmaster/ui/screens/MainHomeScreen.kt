package com.example.ezwordmaster.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.MainTab
import com.example.ezwordmaster.ui.screens.regime.PracticeScreen
import com.example.ezwordmaster.ui.screens.regime.PracticeViewModel
import com.example.ezwordmaster.ui.screens.settings.SettingsScreen
import com.example.ezwordmaster.ui.screens.settings.SettingsViewModel
import com.example.ezwordmaster.ui.screens.topic_managment.TopicManagementScreen
import com.example.ezwordmaster.ui.screens.topic_managment.TopicViewModel

@Composable
fun MainHomeScreen(
    navController: NavHostController,
    topicViewModel: TopicViewModel,
    practiceViewModel: PracticeViewModel,
    settingsViewModel: SettingsViewModel,
    initialTab: MainTab = MainTab.MANAGEMENT

) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    val title = when (selectedTab) {
        MainTab.MANAGEMENT -> "Quản lý"
        MainTab.PRACTICE -> "Ôn tập"
        MainTab.SETTINGS -> "Cài đặt"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA2EAF8),
                        Color(0xFFAAFFA7)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar với icons
            TopBarWithIcons(navController = navController, title = title)
            // Content area với animation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            // Slide left when moving forward
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300))
                        } else {
                            // Slide right when moving backward
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300))
                        }
                    },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        MainTab.MANAGEMENT -> {
                            TopicManagementScreen(
                                navController = navController,
                                viewModel = topicViewModel
                            )
                        }

                        MainTab.PRACTICE -> {
                            PracticeScreen(
                                navController = navController,
                                viewModel = practiceViewModel
                            )
                        }

                        MainTab.SETTINGS -> {
                            SettingsScreen(
                                navController = navController,
                                viewModel = settingsViewModel
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            ModernBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Composable
fun TopBarWithIcons(navController: NavHostController, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Tiêu đề được đặt ở chính giữa Box
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        // Icon thông báo được đặt ở cuối
        Image(
            painter = painterResource(id = R.drawable.ic_bell),
            contentDescription = "Notifications",
            modifier = Modifier
                .align(Alignment.CenterEnd) // Đặt icon ở cuối Box
                .size(28.dp)
                .clickable { navController.navigate("notificationscreen") }
        )
    }
}

@Composable
fun ModernBottomNavBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab Quản lý
            BottomNavItem(
                icon = R.drawable.topic,
                label = "Quản lý",
                isSelected = selectedTab == MainTab.MANAGEMENT,
                onClick = { onTabSelected(MainTab.MANAGEMENT) },
                selectedColor = Color(0xFF2196F3)
            )

            // Tab Ôn tập
            BottomNavItem(
                icon = R.drawable.practice,
                label = "Ôn tập",
                isSelected = selectedTab == MainTab.PRACTICE,
                onClick = { onTabSelected(MainTab.PRACTICE) },
                selectedColor = Color(0xFF4CAF50)
            )

            // Tab Cài đặt
            BottomNavItem(
                icon = R.drawable.ic_settings,
                label = "Cài đặt",
                isSelected = selectedTab == MainTab.SETTINGS,
                onClick = { onTabSelected(MainTab.SETTINGS) },
                selectedColor = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color
) {
    val backgroundColor = if (isSelected) {
        selectedColor.copy(alpha = 0.15f)
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) selectedColor else Color.Gray

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.4f else 1.0f, // Phóng to 170% khi được chọn
        animationSpec = tween(durationMillis = 200), // Tốc độ animation
        label = "scale_animation"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
//            .then(
//                if (isSelected) {
//                    Modifier.shadow(
//                        elevation = 4.dp,
//                        shape = RoundedCornerShape(16.dp),
//                        clip = false
//                    )
//                } else {
//                    Modifier
//                })
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(26.dp),
            colorFilter = if (isSelected) null else ColorFilter.tint(Color.Gray)
            // Có thể điều chỉnh colorFilter tùy theo nhu cầu
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
    }
}