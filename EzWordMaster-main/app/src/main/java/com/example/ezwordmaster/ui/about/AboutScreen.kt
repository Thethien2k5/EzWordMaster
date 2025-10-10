package com.example.ezwordmaster.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezwordmaster.ui.theme.EzWordMasterTheme // Giả sử bạn có file theme

@Composable
fun AboutScreen() {
    // Scaffold cung cấp cấu trúc cơ bản cho màn hình (app bar, content)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Us") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE0F7FA), // Màu xanh nhạt từ Figma
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        // Box để căn giữa nội dung
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Thêm padding cho nội dung
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "All the text, scroll to know.\n\n" +
                        "Đây là nội dung giới thiệu về ứng dụng EzWordMaster. " +
                        "Mục tiêu của chúng tôi là giúp bạn học từ vựng một cách hiệu quả và thú vị. " +
                        "Cảm ơn bạn đã sử dụng ứng dụng!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    EzWordMasterTheme {
        AboutScreen()
    }
}