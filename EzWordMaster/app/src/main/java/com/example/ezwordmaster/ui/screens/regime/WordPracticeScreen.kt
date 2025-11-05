package com.example.ezwordmaster.ui.screens.regime

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.AppBackground


//Chọn chế dộd ôn tập
@Composable
fun WordPracticeScreen(
    navController: NavHostController,
    topicId: String?,
    viewModel: PracticeViewModel
) {
    val SELECTEDTOPIC by viewModel.selectedTopic.collectAsState()
    val SCROLLSTATE = rememberScrollState()
    val SHOWANSWER =
        true // Dùng để hiển thị/ tắt hiển thị câu trả lời trong Tự Luận, trắc nghiệm, đúng sai

    LaunchedEffect(topicId) {
        if (!topicId.isNullOrEmpty()) {
            viewModel.loadTopicById(topicId)
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header với nút back và tên chủ đề
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.return_),
                    contentDescription = "Back",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { navController.navigate("home/PRACTICE") }
                )

                Text(
                    text = SELECTEDTOPIC?.name ?: "Đang tải...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // Chỗ trống để cân bằng bố cục
                Spacer(modifier = Modifier.size(45.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nội dung có thể cuộn
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(SCROLLSTATE)
            ) {
                // ============ PHẦN ÔN TẬP ============
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📚 ÔN TẬP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )

                    // Nút lịch sử ôn tập (nhỏ gọn, nằm ngang)
                    Card(
                        modifier = Modifier.clickable { navController.navigate("studyhistory") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Lịch sử",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card container cho mục ôn tập (chỉ còn Flashcard)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Flashcard (card ngang với icon lớn)
                        StudyModeCardHorizontal(
                            icon = "📇",
                            title = "Flashcard",
                            description = "Lật thẻ để học từ vựng",
                            backgroundColor = Color(0xFFE3F2FD),
                            onClick = { navController.navigate("flashcard/$topicId") }
                        )

                        // Chế độ trắc nghiệm
                        StudyModeCardHorizontal(
                            icon = "🎯",
                            title = "Trắc nghiệm",
                            description = "Chọn 1 trong 4 đáp án đúng",
                            backgroundColor = Color(0xFFF3E5F5),
                            onClick = {
                                navController.navigate("practice_quiz_multi/$topicId/$SHOWANSWER")
                            }
                        )

                        // Đúng
                        StudyModeCardHorizontal(
                            icon = "⚖️",
                            title = "Đúng / Sai",
                            description = "Xác định nghĩa của từ là đúng hay sai",
                            backgroundColor = Color(0xFFE0F2F1),
                            onClick = {
                                navController.navigate("practice_quiz_truefalse/$topicId/$SHOWANSWER")
                            }
                        )

                        // Placeholder cho chế độ ôn tập khác
                        StudyModeCardHorizontal(
                            icon = "✍️",
                            title = "Tự luận",
                            description = "Viết lại nghĩa đúng của từ vựng",
                            backgroundColor = Color(0xFFFFEBEE),
                            onClick = {
                                navController.navigate("practice_quiz_essay/$topicId/$SHOWANSWER")
                            }
                        )

                        // Placeholder cho chế độ ôn tập khác
                        StudyModeCardHorizontal(
                            icon = "✨",
                            title = "Chế độ khác",
                            description = "Đang cập nhật...",
                            backgroundColor = Color(0xFFF3E5F5),
                            enabled = false,
                            onClick = { /* Coming soon */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ============ PHẦN GIẢI TRÍ ============
                Text(
                    text = "🎮 GIẢI TRÍ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.padding(horizontal = 0.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card container cho giải trí (có thể cuộn)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Lật thẻ
                        EntertainmentModeCard(
                            icon = "🎴",
                            title = "Lật thẻ",
                            description = "Ghép từ với nghĩa tương ứng",
                            backgroundColor = Color(0xFFFFF3E0),
                            onClick = { navController.navigate("wordselection/$topicId") }
                        )

                        // Placeholder cho các chế độ khác (có thể thêm nhiều)
                        EntertainmentModeCard(
                            icon = "🎯",
                            title = "Trắc nghiệm",
                            description = "Đang cập nhật...",
                            backgroundColor = Color(0xFFF3E5F5),
                            enabled = false,
                            onClick = { /* Coming soon */ }
                        )

                        EntertainmentModeCard(
                            icon = "🎲",
                            title = "Trò chơi ghép từ",
                            description = "Đang cập nhật...",
                            backgroundColor = Color(0xFFE0F2F1),
                            enabled = false,
                            onClick = { /* Coming soon */ }
                        )

                        EntertainmentModeCard(
                            icon = "🏆",
                            title = "Thử thách tốc độ",
                            description = "Đang cập nhật...",
                            backgroundColor = Color(0xFFFFEBEE),
                            enabled = false,
                            onClick = { /* Coming soon */ }
                        )

                        EntertainmentModeCard(
                            icon = "🎪",
                            title = "Trò chơi khác",
                            description = "Đang cập nhật...",
                            backgroundColor = Color(0xFFFCE4EC),
                            enabled = false,
                            onClick = { /* Coming soon */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logo ở cuối
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp),
                        alpha = 0.3f
                    )
                }
            }
        }
    }
}

/**
 * Card ngang cho chế độ ôn tập (có icon lớn và mô tả)
 */
@Composable
fun StudyModeCardHorizontal(
    icon: String,
    title: String,
    description: String,
    backgroundColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) backgroundColor else Color.LightGray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 48.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Thông tin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.Black else Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = if (enabled) Color.Gray else Color.LightGray
                )
            }

            // Arrow icon
            if (enabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_triangle),
                    contentDescription = "Go",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Card cho giải trí (design ngang, có mô tả)
 */
@Composable
fun EntertainmentModeCard(
    icon: String,
    title: String,
    description: String,
    backgroundColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) backgroundColor else Color.LightGray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Thông tin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.Black else Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = if (enabled) Color.Gray else Color.LightGray
                )
            }

            // Arrow icon
            if (enabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_triangle),
                    contentDescription = "Go",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}