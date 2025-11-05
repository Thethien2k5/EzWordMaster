//package com.example.ezwordmaster.ui.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.example.ezwordmaster.R
//import com.example.ezwordmaster.ui.common.AppBackground
//
////@Composable
////@Preview(
////    name = "Màn hình chính",
////    showBackground = true,
////    showSystemUi = false,
////    widthDp = 365,
////    heightDp = 815
////)
////fun PreviewDSS() {
////    HomeScreen(navController = rememberNavController(), progress = 75, total = 100)
////}
//@Composable
//fun HomeScreen(navController: NavHostController, progress: Int = 75, total: Int = 100) {
//    AppBackground {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
//                    .padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                Image(
//                    painter = painterResource(id = R.drawable.logo),
//                    contentDescription = "Logo",
//                    modifier = Modifier.size(170.dp)
//                )
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(text = "Tiến độ ôn tập hôm nay", fontSize = 16.sp, color = Color.Black)
//                        Text(
//                            text = "${(progress.toFloat() / total * 100).toInt()}%",
//                            fontSize = 12.sp,
//                            color = Color(0xFF00C853),
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//
//                    Spacer(Modifier.height(8.dp))
//
//                    LinearProgressIndicator(
//                        progress = { progress.toFloat() / total },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(12.dp)
//                            .clip(RoundedCornerShape(6.dp)),
//                        color = Color(0xFF00C853),
//                        trackColor = Color(0xFF455A64)
//                    )
//
//                    Spacer(Modifier.height(4.dp))
//
//                    Text(text = "$progress/$total ", fontSize = 12.sp, color = Color.Black)
//                }
//
//                // Các nút chức năng (dùng ảnh nguyên khối)
//                MenuImageButton(R.drawable.topic) { navController.navigate("topicmanagementscreen") }
//                MenuImageButton(R.drawable.practice) { navController.navigate("practice") }
//                MenuImageButton(R.drawable.quiz) { navController.navigate("quiz_setting") }
//                MenuImageButton(R.drawable.ranking) { navController.navigate("/*TO DO */") }
//
//                Image(
//                    painter = painterResource(R.drawable.translate),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
//                        .padding(vertical = 6.dp)
//                        .clickable { navController.navigate("/*TO DO */") }
//                        .offset(y = (-6).dp),
//                    contentScale = ContentScale.FillBounds
//                )
//
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.TopCenter),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_settings),
//                    contentDescription = "Settings",
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clickable { navController.navigate("settings") }
//                )
//                Image(
//                    painter = painterResource(id = R.drawable.ic_bell),
//                    contentDescription = "Notifications",
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clickable { navController.navigate("notification") }
//                )
//            }
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                // Icon Settings - góc dưới bên trái
//                Image(
//                    painter = painterResource(id = R.drawable.ic_help),
//                    contentDescription = "help",
//                    modifier = Modifier
//                        .size(28.dp)
//                        .align(Alignment.BottomStart)
//                        .clickable { navController.navigate("help") }
//                )
//
//                // - góc dưới bên phải
//                Image(
//                    painter = painterResource(id = R.drawable.ic_info),
//                    contentDescription = "about",
//                    modifier = Modifier
//                        .size(28.dp)
//                        .align(Alignment.BottomEnd) // ✅ Luôn ở góc dưới phải
//                        .clickable { navController.navigate("about") }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun MenuImageButton(imageRes: Int, onClick: () -> Unit) {
//    Image(
//        painter = painterResource(id = imageRes),
//        contentDescription = null,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(72.dp)
//            .padding(vertical = 6.dp)
//            .clickable(onClick = onClick),
//        contentScale = ContentScale.FillBounds
//    )
//    Spacer(modifier = Modifier.height(12.dp))
//}