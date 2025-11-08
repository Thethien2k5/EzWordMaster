package com.example.ezwordmaster.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.ViewModelFactory
import com.example.ezwordmaster.ui.common.AppBackground
import com.example.ezwordmaster.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
//    viewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    factory: ViewModelFactory,
) {
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    // State của SettingsViewModel (Thông báo)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val notificationInterval by viewModel.notificationInterval.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val intervalOptions = listOf(2L, 4L, 6L, 8L, 12L)

    // SỬA: Lấy state từ AuthViewModel (SSoT)
    val authUiState by authViewModel.uiState.collectAsState()
    val isLoggedIn = authUiState.isLoggedIn
    val currentUser = authUiState.currentUser

    // Logic hiển thị tên từ SSoT
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: currentUser?.username?.takeIf { it.isNotBlank() }
        ?: currentUser?.email?.takeIf { it.isNotBlank() }
        ?: "Khách"

    val userEmail = currentUser?.email?.takeIf { it.isNotBlank() }
    val profileInitial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    AppBackground {
        Scaffold(containerColor = Color.Transparent) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (isLoggedIn) Color(0xFF2196F3) else Color(0xFF94A3B8)),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageUrl = currentUser?.photoUrl

                        if (!imageUrl.isNullOrBlank()) {
                            // Nếu có ảnh, dùng Coil để tải
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Ảnh đại diện",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop // Đảm bảo ảnh lấp đầy hình tròn
                            )
                        } else {
                            // Nếu không có ảnh, hiển thị chữ cái đầu (fallback)
                            Text(
                                text = profileInitial,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isLoggedIn) "Xin chào, $displayName" else "Bạn đang sử dụng tài khoản khách",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )

                        if (isLoggedIn) {
                            userEmail?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = it, fontSize = 14.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    authViewModel.logout()
                                    // UI sẽ tự cập nhật vì đang observe SSoT
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE53935),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Đăng xuất")
                            }
                        } else { // Chưa đăng nhập
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    // Điều hướng tới login, yêu cầu quay lại tab SETTINGS
                                    navController.navigate("login?next=home/SETTINGS") {
                                        launchSingleTop = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Đăng nhập")
                            }
                        }
                    }
                }
                // Kết thúc UI Hồ sơ

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Card (Thông báo)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Notification Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Thông báo ôn tập",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    "Nhắc nhở bạn học từ mới",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { viewModel.onNotificationToggled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF4CAF50),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.LightGray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Interval Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Tần suất thông báo",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text("Gửi thông báo mỗi", fontSize = 12.sp, color = Color.Gray)
                            }

                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = "$notificationInterval giờ",
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .menuAnchor(),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = isDropdownExpanded
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color.LightGray
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    intervalOptions.forEach { interval ->
                                        DropdownMenuItem(
                                            text = { Text("$interval giờ") },
                                            onClick = {
                                                viewModel.onIntervalChanged(interval)
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Additional Settings Card (Về ứng dụng, Trợ giúp)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        SettingMenuItem(
                            icon = R.drawable.ic_info,
                            title = "Về ứng dụng",
                            onClick = { navController.navigate("about") }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                        SettingMenuItem(
                            icon = R.drawable.ic_help,
                            title = "Trợ giúp",
                            onClick = { navController.navigate("help") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingMenuItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_triangle),
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}