package com.example.ezwordmaster.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ezwordmaster.domain.repository.SettingsViewModel
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import com.example.ezwordmaster.ui.common.GradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val notificationInterval by viewModel.notificationInterval.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val intervalOptions = listOf(2L, 4L, 6L, 8L, 12L)

    GradientBackground {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Settings",
                    canNavigateBack = true,
                    onNavigateUp = { navController.popBackStack() },
                    onLogoClick = {
                        navController.popBackStack()
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bật thông báo ôn tập", fontSize = 18.sp)
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.onNotificationToggled(it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gửi thông báo mỗi", fontSize = 18.sp)
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = "$notificationInterval giờ",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = isDropdownExpanded
                                    )
                                },
                                modifier = Modifier.menuAnchor()
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
        }
    }
}