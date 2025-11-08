package com.example.ezwordmaster.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    factory: ViewModelFactory,
) {
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State của SettingsViewModel (Thông báo)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val notificationInterval by viewModel.notificationInterval.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val intervalOptions = listOf(2L, 4L, 6L, 8L, 12L)

    // Lấy state từ AuthViewModel (SSoT)
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
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Card
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
                        // --- 1. HÀNG THÔNG TIN CÁ NHÂN ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    // *** BẮT ĐẦU SỬA ĐỔI ***
                                    // Thêm border tinh tế
                                    .border(
                                        width = 2.dp,
                                        color = if (isLoggedIn) Color(0xFF2196F3) else Color(
                                            0xFF94A3B8
                                        ),
                                        shape = CircleShape
                                    )
                                    // *** KẾT THÚC SỬA ĐỔI ***
                                    .padding(2.dp) // Thêm padding nhỏ bên trong border
                                    .clip(CircleShape)
                                    .background(
                                        if (isLoggedIn) Color(0xFF2196F3) else Color(
                                            0xFF94A3B8
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val imageUrl = currentUser?.photoUrl

                                if (!imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Ảnh đại diện",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = profileInitial,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Tên và Email
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isLoggedIn) "Xin chào, $displayName" else "Bạn đang sử dụng tài khoản khách",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                if (isLoggedIn) {
                                    userEmail?.let {
                                        Text(text = it, fontSize = 14.sp, color = Color.Gray)
                                    }
                                } else {
                                    Text(
                                        text = "Đăng nhập để đồng bộ dữ liệu",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } // Kết thúc hàng thông tin

                        Spacer(modifier = Modifier.height(20.dp))

                        // *** BẮT ĐẦU SỬA ĐỔI ***
                        // Thêm đường kẻ phân chia
                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            thickness = 1.dp
                        )
                        // *** KẾT THÚC SỬA ĐỔI ***

                        Spacer(modifier = Modifier.height(16.dp)) // Giảm spacer sau divider

                        // --- 2. HÀNG NÚT HÀNH ĐỘNG ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp) // Khoảng cách giữa các nút
                        ) {
                            // Nút Đăng nhập / Đăng xuất
                            if (isLoggedIn) {
                                Button(
                                    onClick = {
                                        authViewModel.logout()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFBE9E7), // Nền đỏ nhạt
                                        contentColor = Color(0xFFE53935)  // Chữ đỏ đậm
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Đăng xuất",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        navController.navigate("login?next=home/SETTINGS") {
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2196F3), // Xanh chính
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Đăng nhập",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Nút Sao lưu
                            Button(
                                onClick = {
                                    if (isLoggedIn) {
                                        navController.navigate("backup")
                                    } else {
                                        coroutineScope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Vui lòng đăng nhập trước khi sao lưu dữ liệu",
                                                actionLabel = "Đăng nhập",
                                                duration = SnackbarDuration.Long
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                navController.navigate("login?next=home/SETTINGS")
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = if (isLoggedIn) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE3F2FD), // Nền xanh nhạt
                                        contentColor = Color(0xFF2196F3)  // Chữ xanh đậm (sẽ không dùng)
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFECEFF1), // Nền xám
                                        contentColor = Color.Gray         // Chữ xám
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                // Dùng Image để giữ màu gốc của drawable
                                Image(
                                    painter = painterResource(id = R.drawable.ic_cloud),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sao lưu", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                        } // Kết thúc hàng nút
                    }
                }

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
                        // Sử dụng hình ảnh thay vì icon
                        ImageMenuItem(
                            imageRes = R.drawable.ic_info, // Thay bằng hình ảnh về ứng dụng
                            title = "Về ứng dụng",
                            onClick = { navController.navigate("about") }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                        ImageMenuItem(
                            imageRes = R.drawable.ic_help, // Thay bằng hình ảnh trợ giúp
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
private fun ImageMenuItem(
    imageRes: Int,
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
        AsyncImage(
            model = imageRes,
            contentDescription = title,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        AsyncImage(
            model = R.drawable.ic_triangle, // Thay bằng hình mũi tên của bạn
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}

// Giữ lại SettingMenuItem cũ nếu cần sử dụng ở nơi khác
@Composable
private fun SettingMenuItem(
    icon: Int,
    title: String,
    subtitle: String? = null,
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