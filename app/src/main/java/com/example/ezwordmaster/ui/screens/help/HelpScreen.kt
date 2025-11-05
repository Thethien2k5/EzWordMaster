package com.example.ezwordmaster.ui.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.model.HelpItem
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import com.example.ezwordmaster.ui.common.GradientBackground

@Composable
fun HelpScreen(
    navController: NavHostController,
    helpViewModel: HelpViewModel = viewModel()
) {
    val uiState by helpViewModel.uiState.collectAsState()

    // Sử dụng nền gradient
    GradientBackground {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Help",
                    canNavigateBack = true,
                    onNavigateUp = { navController.popBackStack() },
                    onLogoClick = {
                        navController.popBackStack()
                    }
                )
            },
            // Đặt nền trong suốt để thấy gradient
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Thêm dòng mô tả giống Figma
                Text(
                    text = "Describe anything you need to help!!",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Khung trả lời
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Help Answer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.selectedItem?.answer
                                ?: "Please select a question below.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Danh sách câu hỏi
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.helpItems, key = { it.id }) { item ->
                        QuestionItem(
                            item = item,
                            isSelected = uiState.selectedItem == item,
                            onClick = { helpViewModel.onQuestionClicked(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItem(item: HelpItem, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFB2EBF2) else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = item.question,
            modifier = Modifier.padding(16.dp),
            color = Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ✅ HÀM PREVIEW ĐÃ ĐƯỢC SỬA LẠI
@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    // Tạo một NavController giả để preview có thể chạy
    HelpScreen(navController = rememberNavController())
}