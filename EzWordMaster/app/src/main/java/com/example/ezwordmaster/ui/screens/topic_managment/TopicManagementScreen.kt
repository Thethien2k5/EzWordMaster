package com.example.ezwordmaster.ui.screens.topic_managment

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.FilterSortType
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.ui.common.AppBackground
import com.example.ezwordmaster.ui.common.Menu
import kotlinx.coroutines.launch


@Composable
fun TopicManagementScreen(navController: NavHostController, viewModel: TopicViewModel) {
    val TOPICS by viewModel.topics.collectAsState()
    val TOASTMESSAGE by viewModel.toastMessage.collectAsState()
    val COROUTINESCOPE = rememberCoroutineScope()
    val SNACKBARHOSTSTATE = remember { SnackbarHostState() }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }
    var filterSortType by remember { mutableStateOf(FilterSortType.ALL) }

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

    LaunchedEffect(TOASTMESSAGE) {
        TOASTMESSAGE?.let { message ->
            COROUTINESCOPE.launch {
                SNACKBARHOSTSTATE.showSnackbar(message)
            }
            viewModel.clearToastMessage() // Rất quan trọng: Xóa lỗi sau khi hiển thị
        }
    }
    // Tải chủ đề
    LaunchedEffect(true) {
        viewModel.loadAllTopics()
        Log.d("TopicScreen", "✅ Đã tải ${TOPICS.size} topics")
    }
    AppBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = SNACKBARHOSTSTATE) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // *** ======= TÌM KIẾM + KÍNH LÚP + LOGO ======= *** //

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(30.dp, (-39).dp),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
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

                // *** ======= DANH SÁCH CHỦ ĐỀ ======= *** //
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .offset(y = (-80).dp)
                ) {
                    // + Chủ đề --- Số lượng từ vựng
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "+",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clickable {
                                        showAddTopicDialog = !showAddTopicDialog
                                    }
                            )
                            Text(
                                "Chủ Đề",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_triangle),
                                contentDescription = "Sort",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(14.dp)
                                    .rotate(if (showDropdown) 180f else 0f)
                                    .clickable { showDropdown = !showDropdown }
                            )

                        }
                        Text(
                            "Số lượng từ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )


                    }
                    Menu(
                        showDropdown = showDropdown,
                        onDropdownToggle = { isVisible -> showDropdown = isVisible },
                        filterSortType = filterSortType,
                        onFilterSortTypeChange = { newType -> filterSortType = newType },
                        modifier = Modifier
                            .offset(x = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Danh sách chủ đề
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(filteredAndSortedTopics) { topic ->

                            ExpandableTopicItem(topic, navController = navController)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddTopicDialog) {
        AddTopicDialog(
            title = "Thêm chủ đề mới",
            currentName = "",
            onDismiss = { showAddTopicDialog = false },
            onConfirm = { topicName ->
                viewModel.addTopic(topicName)
                showAddTopicDialog = false
            }
        )
    }
}

// Hiển thị động các chủ đề
@Composable
fun ExpandableTopicItem(topic: Topic, navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }
    val ROTATION by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier.fillMaxWidth(),
//            .animateContentSize(), // co giãn mượt
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = topic.name ?: "Lỗi không tìm được tên Topic",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit topic",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                topic.id?.let { id ->
                                    navController.navigate("edittopic/$id")
                                }
                            }
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${topic.words.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_triangle),
                        contentDescription = "Expand",
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(ROTATION),
                        tint = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(7.dp))

            // Danh sách từ vựng khi mở rộng
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                // Phần cuộn chỉ cho danh sách từ vựng
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 270.dp) // giới hạn chiều cao, phần còn lại cuộn
                ) {
                    items(topic.words) { word ->
                        Text(
                            text = "• ${word.word}: ${word.meaning}",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Hộp thoại thêm chủ đề mới
@Composable
fun AddTopicDialog(
    title: String = "Thêm chủ đề mới",
    currentName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var topicName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFC2DDEF),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            OutlinedTextField(
                value = topicName,
                onValueChange = { topicName = it },
                placeholder = { Text("Tên chủ đề", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (topicName.isNotBlank()) {
                        onConfirm(topicName.trim())
                    }
                }
            ) {
                Text("OK", color = Color(0xFF00BCD4), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy bỏ", color = Color.Gray)
            }
        }
    )
}
