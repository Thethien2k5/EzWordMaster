package com.example.ezwordmaster.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.domain.model.FilterSortType
import com.example.ezwordmaster.domain.model.Topic
import com.example.ezwordmaster.domain.repository.TopicRepository

@Composable
fun PracticeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { TopicRepository(context) }
    
    var topics by remember { mutableStateOf<List<Topic>>(emptyList()) }
    var filterSortType by remember { mutableStateOf(FilterSortType.ALL) }
    var showDropdown by remember { mutableStateOf(false) }
    
    // Tải chủ đề
    LaunchedEffect(Unit) {
        topics = repository.loadTopics()
    }
    
    // Lọc và sắp xếp chủ đề
    val filteredAndSortedTopics = remember(topics, filterSortType) {
        when (filterSortType) {
            FilterSortType.ALL -> topics
            FilterSortType.BY_TOPIC -> topics.filter { !it.category.isNullOrEmpty() }
            FilterSortType.BY_OWNER -> topics.filter { !it.owner.isNullOrEmpty() }
            FilterSortType.NEWEST -> topics.sortedByDescending { it.lastModified ?: it.createdDate ?: 0L }
            FilterSortType.A_TO_Z -> topics.sortedBy { it.name ?: "" }
            FilterSortType.WORD_COUNT -> topics.sortedByDescending { it.words.size }
        }
    }
    
    val dropdownText = when (filterSortType) {
        FilterSortType.ALL -> "Tất cả"
        FilterSortType.BY_TOPIC -> "Theo chủ đề"
        FilterSortType.BY_OWNER -> "Theo người dùng"
        FilterSortType.NEWEST -> "Mới nhất"
        FilterSortType.A_TO_Z -> "A - Z"
        FilterSortType.WORD_COUNT -> "Số lượng từ"
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
                
                // Logo ứng dụng
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDropdown = !showDropdown },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dropdownText,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_triangle),
                        contentDescription = "Dropdown",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Menu thả xuống
            if (showDropdown) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        FilterSortType.values().forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        filterSortType = type
                                        showDropdown = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (type) {
                                        FilterSortType.ALL -> "Tất cả"
                                        FilterSortType.BY_TOPIC -> "Theo chủ đề"
                                        FilterSortType.BY_OWNER -> "Theo người dùng"
                                        FilterSortType.NEWEST -> "Mới nhất"
                                        FilterSortType.A_TO_Z -> "A - Z"
                                        FilterSortType.WORD_COUNT -> "Số lượng từ"
                                    },
                                    fontSize = 14.sp,
                                    color = if (filterSortType == type) Color(0xFF2196F3) else Color.Black
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chủ Đề",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Số lượng từ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Danh sách chủ đề
            LazyColumn {
                items(filteredAndSortedTopics.filter { topic ->
                    // Chỉ hiển thị thẻ khi có đủ thông tin
                    !topic.name.isNullOrEmpty() && 
                    !topic.owner.isNullOrEmpty() && 
                    topic.words.isNotEmpty()
                }) { topic ->
                    TopicCard(
                        topic = topic,
                        onTopicClick = { topicId ->
                            navController.navigate("wordpractice/$topicId")
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TopicCard(topic: Topic, onTopicClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        // Nhấn vào thẻ để chuyển đến WordPracticeScreen
                        topic.id?.let { onTopicClick(it) }
                    }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = topic.name ?: "Không có tên",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.owner ?: "Không có chủ sở hữu",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${topic.words.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_triangle),
                        contentDescription = "Expand",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { expanded = !expanded }
                            .rotate(rotation)
                    )
                }
            }
            
            // Danh sách từ vựng khi mở rộng
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    topic.words.take(10).forEach { word ->
                        Text(
                            text = "• ${word.word}: ${word.meaning}",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    
                    if (topic.words.size > 10) {
                        Text(
                            text = "... và ${topic.words.size - 10} từ khác",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
