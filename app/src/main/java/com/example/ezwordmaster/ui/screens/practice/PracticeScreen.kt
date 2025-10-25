package com.example.ezwordmaster.ui.screens.practice

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.FilterSortType
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.data.repository.TopicRepositoryImpl
import com.example.ezwordmaster.ui.common.AppBackground

@Composable
fun PracticeScreen(navController: NavHostController, viewModel: PracticeViewModel) {
    val TOPICS by viewModel.topics.collectAsState()

    var filterSortType by remember { mutableStateOf(FilterSortType.ALL) }
    var showDropdown by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Tải chủ đề
    LaunchedEffect(Unit) {
        viewModel.loadTopics()
    }

    // Lọc và sắp xếp chủ đề
    val filteredAndSortedTopics = remember(TOPICS, filterSortType, searchQuery) {
        // 1. Lọc theo thanh tìm kiếm (tên chủ đề)
        val searchedTopics = if (searchQuery.text.isNotBlank()) {
            TOPICS.filter {
                it.name?.contains(searchQuery.text, ignoreCase = true) == true
            }
        } else {
            TOPICS
        }

        // 2. Sắp xếp danh sách đã lọc
        when (filterSortType) {
            FilterSortType.Z_TO_A -> searchedTopics.sortedByDescending { it.name ?: "" }
            FilterSortType.WORD_COUNT -> searchedTopics.sortedByDescending { it.words.size }
            else -> searchedTopics
        }
    }

    val DROPDOWNTEXT = when (filterSortType) {
        FilterSortType.ALL -> "Tất cả"
        FilterSortType.Z_TO_A -> "Sắp xếp: Z - A"
        FilterSortType.WORD_COUNT -> "Sắp xếp: Số lượng từ"
    }

    AppBackground(
    ) {
        Column(
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // *** ======= NÚT QUAY VỀ + TÌM KIẾM + KÍNH LÚP + LOGO ======= *** //
            // Back button
            Image(
                painter = painterResource(id = R.drawable.return_),
                contentDescription = "Back",
                modifier = Modifier
                    .size(45.dp).offset(10.dp)
                    .clickable { navController.navigate("home") }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(30.dp, (-43).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Search bar với kính lúp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm kiếm", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp)
                        ,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF00BCD4),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        trailingIcon = {
                            Box(modifier = Modifier.size(40.dp))
                        }
                    )
                    // Kính lúp
                    Icon(
                        painter = painterResource(id = R.drawable.magnifying_glass),
                        contentDescription = "Search Icon",
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier
                            .size(43.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 1.dp, 11.dp)
                    )
                }
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .offset(x = (-30).dp) // Rất gần thanh tìm kiếm
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .offset(y= (-80).dp)
        ) {
            // Dropdown chọn kiểu sắp xếp
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
                        text = DROPDOWNTEXT,
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
                    val AVAILABLESORTTYPES = listOf(
                        FilterSortType.ALL,
                        FilterSortType.Z_TO_A,
                        FilterSortType.WORD_COUNT
                    )
                    Column(modifier = Modifier.padding(8.dp)) {
                        AVAILABLESORTTYPES.forEach { type ->
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
                                        FilterSortType.Z_TO_A -> "Sắp xếp: Z - A"
                                        FilterSortType.WORD_COUNT -> "Sắp xếp: Số lượng từ"
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
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredAndSortedTopics.filter { topic ->
                    !topic.name.isNullOrEmpty() &&
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
        }}
    }
}

@Composable
fun TopicCard(topic: Topic, onTopicClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val ROTATION by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        topic.id?.let { onTopicClick(it) }
                    }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chỉ còn Text hiển thị tên chủ đề
                Text(
                    text = topic.name ?: "Không có tên",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f) // Giúp tên dài không đẩy các thành phần khác
                )

                // Phần hiển thị số lượng từ và icon không đổi
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
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
                            .clickable(onClick = { expanded = !expanded })
                            .rotate(ROTATION)
                    )
                }
            }

            // Phần danh sách từ vựng khi mở rộng không thay đổi
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
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
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}