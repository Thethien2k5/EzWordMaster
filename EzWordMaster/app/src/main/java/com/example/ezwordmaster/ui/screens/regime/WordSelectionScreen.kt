package com.example.ezwordmaster.ui.screens.regime

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.ezwordmaster.model.Word

// Màn hình chọn từ trước khi vào FlipCard
@Composable
fun WordSelectionScreen(
    navController: NavHostController,
    topicId: String?,
    viewModel: PracticeViewModel
) {
    val TOPIC by viewModel.selectedTopic.collectAsState()
    val SELECTEDWORDS by viewModel.selectedWords.collectAsState()

    LaunchedEffect(topicId) {
        viewModel.loadTopicById(topicId ?: "Lỗi không id WordSelectionScreen")
    }

    val ALLWORDS = TOPIC?.words ?: emptyList()
    // Trạng thái của checkbox "Chọn hết" được suy ra từ state trong ViewModel
    val ISALLSELECTED = SELECTEDWORDS.isNotEmpty() && SELECTEDWORDS.size == ALLWORDS.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFA2EAF8), Color(0xFFAAFFA7))
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Phần chọn tất cả
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleSelectAll() }, // THAY ĐỔI 4: Gửi sự kiện về ViewModel
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chọn hết",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Checkbox(
                    checked = ISALLSELECTED,
                    onCheckedChange = { viewModel.toggleSelectAll() },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Chọn từ vựng để học",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Danh sách từ vựng
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        ALLWORDS,
                        key = { it.word ?: "lỗi WordSelectionScreeen dòng 111" }) { word ->
                        WordSelectionItem(
                            word = word,
                            isSelected = SELECTEDWORDS.contains(word.word),
                            onToggle = { viewModel.toggleWordSelection(word) } // THAY ĐỔI 5: Gửi sự kiện
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nút bắt đầu
            Button(
                onClick = {
                    if (SELECTEDWORDS.isNotEmpty()) {
                        // Logic tạo chuỗi JSON vẫn có thể ở đây vì nó liên quan đến navigation
                        val SELECTEDWORDSLIST = ALLWORDS.filter { SELECTEDWORDS.contains(it.word) }
                        val WORDSJSON =
                            SELECTEDWORDSLIST.joinToString(",") { "${it.word}:${it.meaning}" }
                        navController.navigate("flipcard/$topicId/$WORDSJSON")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = SELECTEDWORDS.isNotEmpty()
            ) {
                Text(text = "Bắt đầu lật thẻ (${SELECTEDWORDS.size})")
            }
        }
    }
}

@Composable
fun WordSelectionItem(
    word: Word,
    isSelected: Boolean,
    onToggle: () -> Unit // THAY ĐỔI 6: Nhận một lambda đơn giản
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if (isSelected) Color(0xFFE8F5E8) else Color.Transparent)
            .clickable(onClick = onToggle)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = word.word ?: "Lỗi từ vựng WordSelectionScreeen")
        Text(text = word.meaning ?: "Lỗi nghĩa WordSelectionScreeen", color = Color.DarkGray)
    }
    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
}