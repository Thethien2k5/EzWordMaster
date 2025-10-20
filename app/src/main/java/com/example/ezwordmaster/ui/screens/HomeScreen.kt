package com.example.ezwordmaster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.AppBackground

//@Composable
//@Preview(
//    name = "Màn hình chính",
//    showBackground = true,
//    showSystemUi = false,
//    widthDp = 365,
//    heightDp = 815
//)
//fun PreviewDSS() {
//    HomeScreen(navController = rememberNavController(), progress = 75, total = 100)
//}
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

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(170.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Tiến độ ôn tập hôm nay", fontSize = 16.sp, color = Color.Black)
                        Text(
                            text = "${(progress.toFloat() / total * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = Color(0xFF00C853),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

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

                    Text(text = "$progress/$total ", fontSize = 12.sp, color = Color.Black)
                }

                // Các nút chức năng (dùng ảnh nguyên khối)
                MenuImageButton(R.drawable.topic) { navController.navigate("topicmanagementscreen") }//quản lý
                MenuImageButton(R.drawable.practice) { navController.navigate("practice") }// ôn tập
                MenuImageButton(R.drawable.quiz) { navController.navigate("quiz") }//quiz
                MenuImageButton(R.drawable.translate) { navController.navigate("translate") } //dịch
                MenuImageButton(R.drawable.ranking) { navController.navigate("ranking") }// xếp hạng

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_help),
                        contentDescription = "Help",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate("help") }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Info",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate("about") }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

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
                        .clickable { navController.navigate("settings") }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.navigate("notification") }
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
            .clickable(onClick = onClick),
        contentScale = ContentScale.FillBounds
    )
    Spacer(modifier = Modifier.height(12.dp))
}