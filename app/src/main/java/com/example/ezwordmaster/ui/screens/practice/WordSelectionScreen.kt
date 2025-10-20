package com.example.ezwordmaster.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.domain.model.Topic
import com.example.ezwordmaster.domain.model.Word
import com.example.ezwordmaster.domain.repository.TopicRepository

@Composable
fun WordSelectionScreen(navController: NavHostController, topicId: String?) {
    val context = LocalContext.current
    val repository = remember { TopicRepository(context) }
    
    var topic by remember { mutableStateOf<Topic?>(null) }
    var selectedWords by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectAll by remember { mutableStateOf(false) }
    
    // Tải chủ đề theo ID
    LaunchedEffect(topicId) {
        if (topicId != null) {
            val topics = repository.loadTopics()
            topic = topics.find { it.id == topicId }
        }
    }
    
    val words = topic?.words ?: emptyList()
    
    // Xử lý chọn tất cả
    LaunchedEffect(selectAll) {
        if (selectAll) {
            selectedWords = words.mapNotNull { it.word }.toSet()
        } else {
            selectedWords = emptySet()
        }
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
            // Header với nút back
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
                        .clickable { navController.navigate("practice") }
                )
                
                // Chỗ trống để cân bằng bố cục
                Spacer(modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Phần chọn tất cả
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chọn hết",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectAll) Color(0xFF4CAF50) else Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { selectAll = !selectAll }
                ) {
                    if (selectAll) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info), // Tạm dùng icon này
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Chọn từ vựng để học",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Danh sách từ vựng trong thẻ trắng
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(words) { word ->
                        WordSelectionItem(
                            word = word,
                            isSelected = selectedWords.contains(word.word),
                            onToggle = { wordText ->
                                selectedWords = if (selectedWords.contains(wordText)) {
                                    selectedWords - wordText
                                } else {
                                    selectedWords + wordText
                                }
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Nút bắt đầu
            Button(
                onClick = { 
                    if (selectedWords.isNotEmpty()) {
                        val selectedWordsList = words.filter { selectedWords.contains(it.word) }
                        val wordsJson = selectedWordsList.joinToString(",") { "${it.word}:${it.meaning}" }
                        navController.navigate("flipcard/$topicId/$wordsJson")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedWords.isNotEmpty()
            ) {
                Text(
                    text = "Bắt đầu lật thẻ (${selectedWords.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun WordSelectionItem(
    word: Word,
    isSelected: Boolean,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) Color(0xFFE8F5E8) else Color.Transparent
            )
            .clickable { word.word?.let { onToggle(it) } }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = word.word ?: "",
            fontSize = 14.sp,
            color = Color.Black
        )
        
        Text(
            text = word.meaning ?: "",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
    
    HorizontalDivider(
        color = Color.LightGray,
        thickness = 1.dp
    )
}
