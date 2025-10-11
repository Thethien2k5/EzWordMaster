package com.example.ezwordmaster.ui.screens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import android.util.Log
import com.example.ezwordmaster.R
import com.example.ezwordmaster.domain.model.Topic
import com.example.ezwordmaster.ui.common.AppBackground
import com.example.ezwordmaster.domain.repository.TopicRepository


@Composable
fun TopicManagementScreen(navController: NavHostController) {
    val CONTEXT = LocalContext.current
    val REPOSITORY = remember { TopicRepository(CONTEXT) }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) } // nội dung tìm kiếm
    var topics by remember { mutableStateOf<List<Topic>>(emptyList()) } // danh sách chủ đề

    // Tải chủ đề
    LaunchedEffect(true) {
        topics = REPOSITORY.loadTopics()
        Log.d("TopicScreen", "✅ Đã tải ${topics.size} topics")
    }
    AppBackground {
        Column() {
            // *** ======= NÚT QUAY VỀ + TÌM KIẾM + KÍNH LÚP + LOGO ======= *** //
            // Back button
            Image(
                painter = painterResource(id = R.drawable.return_),
                contentDescription = "Back",
                modifier = Modifier
                    .size(40.dp).offset(10.dp)
                    .clickable { navController.popBackStack() }
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

            // *** ======= DANH SÁCH CHỦ ĐỀ ======= *** //
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .offset(y= (-80).dp)
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
                            modifier = Modifier.padding(end = 4.dp)
                                .clickable {
                                "#"}
                        )
                        Text(
                            "Chủ Đề",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Text(
                        "Số lượng từ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Danh sách chủ đề
                LazyColumn{
                    val FILTEREDTOPICS = topics.filter {
                        it.name?.contains(searchQuery.text, ignoreCase = true) ?: false
                    }

                    items(FILTEREDTOPICS) { topic ->

                        ExpandableTopicItem(topic)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun ExpandableTopicItem(topic: Topic) {
    var expanded by remember { mutableStateOf(false) }
    val ROTATION by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier.fillMaxWidth()
            .animateContentSize(), // co giãn mượt
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

                Row(verticalAlignment = Alignment.CenterVertically) {
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

            // Danh sách từ vựng khi mở rộng
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    topic.words.forEach { word ->
                        Text(
                            text = "• ${word.word}: ${word.meaning}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}