package com.example.ezwordmaster.ui.screens.regime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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
import com.example.ezwordmaster.ui.common.AppBackground
import com.example.ezwordmaster.ui.common.SortDropdownMenu

// Màng hình chọn chủ đề trước khi chọn chế độ
@Composable
fun PracticeScreen(navController: NavHostController, viewModel: PracticeViewModel) {
    val TOPICS by viewModel.topics.collectAsState()

    var showDropdown by remember { mutableStateOf(false) }
    var filterSortType by remember { mutableStateOf(FilterSortType.ALL) }
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

    AppBackground {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            // *** ======= TÌM KIẾM + KÍNH LÚP + LOGO ======= *** //

            Spacer(modifier = Modifier.height(37.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .offset(y = (-64).dp)
            ) {
                // ============= SẮP XẾP ======================
                SortDropdownMenu(
                    showDropdown = showDropdown,
                    onDropdownToggle = { isVisible ->
                        showDropdown = isVisible
                    },
                    filterSortType = filterSortType,
                    onFilterSortTypeChange = { newType ->
                        filterSortType = newType
                    },
                    modifier = Modifier
                        .offset(x = 16.dp)
                )
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
            }
        }
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