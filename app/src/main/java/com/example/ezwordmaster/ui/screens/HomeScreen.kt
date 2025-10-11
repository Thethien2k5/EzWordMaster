package com.example.ezwordmaster.ui.screens

import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.AppBackground
import androidx.compose.ui.draw.clip
import com.example.ezwordmaster.domain.repository.TopicRepository

@Composable
fun HomeScreen(navController: NavHostController, progress: Int = 75, total: Int = 100) {
    AppBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(170.dp)
                )

                // Thanh tiến độ
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header với số %
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tiến độ ôn tập hôm nay",
                            fontSize = 16.sp,
                            color = Color.Black,

                        )

                        Text(
                            text = "${(progress.toFloat() / total * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = Color(0xFF00C853),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Thanh progress
                    LinearProgressIndicator(
                        progress = { progress.toFloat() / total },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF00C853),
                        trackColor = Color(0xFF455A64)
                    )

                    Spacer(Modifier.height(4.dp))

                    // Text số lượng
                    Text(
                        text = "$progress/$total ",
                        fontSize = 12.sp,
                        color = Color(0xFF000000)
                    )
                }

                // Các nút chức năng (dùng ảnh nguyên khối)
                MenuImageButton(R.drawable.topic) { navController.navigate("topicmanagementscreen") }
                MenuImageButton(R.drawable.practice) { navController.navigate("practice") }
                MenuImageButton(R.drawable.quiz) { navController.navigate("quiz") }
                MenuImageButton(R.drawable.translate) { navController.navigate("translate") }
                MenuImageButton(R.drawable.ranking) { navController.navigate("ranking") }

                Spacer(modifier = Modifier.height(32.dp))

                // 2 nút góc dưới
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_help),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { /* TODO */ }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Info",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { /* TODO */ }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Icon cài đặt và chuông sát góc trên
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { /* TODO */ }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun MenuImageButton(imageRes: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        contentScale = ContentScale.FillBounds
    )
}