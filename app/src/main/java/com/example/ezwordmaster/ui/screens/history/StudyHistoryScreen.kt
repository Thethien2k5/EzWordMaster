package com.example.ezwordmaster.ui.screens.history

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.StudyResult

@Composable
fun StudyHistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel
) {
    val studyResults by viewModel.studyResults.collectAsState()
    val todayProgress by viewModel.todayProgress.collectAsState()

    // Load data when the screen is initialized
    LaunchedEffect(Unit) {
        viewModel.loadStudyHistory()
        viewModel.loadTodayProgress()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
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
                        .clickable { navController.popBackStack() }
                )

                Text(
                    text = "Lịch sử ôn tập",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.size(45.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📚 Tiến trình hôm nay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Sessions
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${todayProgress?.totalSessions ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                            Text(text = "Buổi học", fontSize = 12.sp, color = Color.Gray)
                        }

                        // Known Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${todayProgress?.totalKnownWords ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(text = "Từ đã nhớ", fontSize = 12.sp, color = Color.Gray)
                        }

                        // Total Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${todayProgress?.totalWords ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA726)
                            )
                            Text(text = "Tổng từ", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // History List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lịch sử (${studyResults.size}/44)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                if (studyResults.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAllHistory() }) {
                        Text(text = "Xóa tất cả", fontSize = 14.sp, color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // History List
            if (studyResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📝", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Chưa có lịch sử ôn tập", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(studyResults, key = { it.id }) { result ->
                        HistoryItemCard(result = result)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(result: StudyResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (result.studyMode) {
                "flashcard" -> Color(0xFFE3F2FD)
                "flipcard" -> Color(0xFFFFF3E0)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Icon
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = when (result.studyMode) {
                        "flashcard" -> Color(0xFF2196F3)
                        "flipcard" -> Color(0xFFFF9800)
                        else -> Color.Gray
                    }
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (result.studyMode) {
                            "flashcard" -> "📇"
                            "flipcard" -> "🎴"
                            else -> "📚"
                        },
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Information
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.topicName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📅 ${result.day}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(2.dp))

                // Result Details
                when (result.studyMode) {
                    "flashcard" -> {
                        Text(
                            text = "✅ ${result.knownWords}/${result.totalWords} từ (${result.accuracy?.toInt()}%)",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    "flipcard" -> {
                        Text(
                            text = "🎯 ${result.matchedPairs}/${result.totalPairs} cặp (${result.completionRate?.toInt()}%)",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Duration
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDuration(result.duration),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(text = "⏱️", fontSize = 16.sp)
            }
        }
    }
}

/**
 * Formats duration from seconds to a minute:second string.
 */
private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}