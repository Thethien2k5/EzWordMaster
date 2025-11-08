package com.example.ezwordmaster.ui.screens.backup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.model.BackupUiState
import com.example.ezwordmaster.ui.common.CommonTopAppBar

@Composable
fun BackupScreen(
    navController: NavHostController,
    viewModel: BackupViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Lưu dữ liệu", "Tải xuống")

    // Show snackbar for messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Sao lưu dữ liệu",
                canNavigateBack = true,
                onNavigateUp = {
                    navController.navigate("home/SETTINGS")
                },
                onLogoClick = {
                    navController.navigate("home/SETTINGS")
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            if (index == 1) viewModel.loadCloudData()
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content
            when (selectedTab) {
                0 -> UploadTab(viewModel, uiState)
                1 -> DownloadTab(viewModel, uiState)
            }
        }
    }
}

@Composable
fun UploadTab(viewModel: BackupViewModel, uiState: BackupUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Info card
        InfoCard(
            icon = Icons.Default.CloudUpload,
            text = "Sao lưu dữ liệu từ thiết bị này lên cloud"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        if (uiState.isLoading) {
            LinearProgressIndicator(
                progress = uiState.uploadProgress / 100f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đang tải lên: ${uiState.uploadProgress}%")
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Data selection
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Topics section
            item {
                SectionHeader(
                    title = "Chủ đề (${uiState.localTopics.size})",
                    selectAll = uiState.selectAllTopics,
                    onSelectAllToggle = { viewModel.toggleSelectAllTopics() }
                )
            }

            if (!uiState.selectAllTopics) {
                items(uiState.localTopics) { topic ->
                    SelectableItem(
                        title = topic.name ?: "Không có tên",
                        subtitle = "${topic.words.size} từ",
                        isSelected = topic.id in uiState.selectedTopicIds,
                        onToggle = { topic.id?.let { viewModel.toggleTopicSelection(it) } }
                    )
                }
            }

            // Study results section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Lịch sử học tập (${uiState.localStudyResults.size})",
                    selectAll = uiState.selectAllResults,
                    onSelectAllToggle = { viewModel.toggleSelectAllResults() }
                )
            }

            if (!uiState.selectAllResults) {
                items(uiState.localStudyResults) { result ->
                    SelectableItem(
                        title = result.topicName,
                        subtitle = "${result.studyMode} - ${result.day}",
                        isSelected = result.id in uiState.selectedResultIds,
                        onToggle = { viewModel.toggleResultSelection(result.id) }
                    )
                }
            }
        }

        // Upload button
        Button(
            onClick = { viewModel.uploadSelectedData() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sao lưu dữ liệu", fontSize = 16.sp)
        }
    }
}

@Composable
fun DownloadTab(viewModel: BackupViewModel, uiState: BackupUiState) {
    LaunchedEffect(Unit) {
        viewModel.loadCloudData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Info card
        InfoCard(
            icon = Icons.Default.CloudDownload,
            text = "Tải xuống dữ liệu từ cloud về thiết bị này"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        if (uiState.isLoading) {
            LinearProgressIndicator(
                progress = uiState.downloadProgress / 100f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đang tải xuống: ${uiState.downloadProgress}%")
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cloud data
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Topics section
            item {
                SectionHeader(
                    title = "Chủ đề trên cloud (${uiState.cloudTopics.size})",
                    selectAll = uiState.selectAllTopics,
                    onSelectAllToggle = { viewModel.toggleSelectAllTopics() }
                )
            }

            if (uiState.cloudTopics.isEmpty()) {
                item {
                    Text(
                        "Không có dữ liệu trên cloud",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (!uiState.selectAllTopics) {
                items(uiState.cloudTopics) { topic ->
                    SelectableItem(
                        title = topic.name ?: "Không có tên",
                        subtitle = "${topic.words.size} từ",
                        isSelected = topic.id in uiState.selectedTopicIds,
                        onToggle = { topic.id?.let { viewModel.toggleTopicSelection(it) } }
                    )
                }
            }

            // Study results section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Lịch sử trên cloud (${uiState.cloudStudyResults.size})",
                    selectAll = uiState.selectAllResults,
                    onSelectAllToggle = { viewModel.toggleSelectAllResults() }
                )
            }

            if (!uiState.selectAllResults && uiState.cloudStudyResults.isNotEmpty()) {
                items(uiState.cloudStudyResults) { result ->
                    SelectableItem(
                        title = result.topicName,
                        subtitle = "${result.studyMode} - ${result.day}",
                        isSelected = result.id in uiState.selectedResultIds,
                        onToggle = { viewModel.toggleResultSelection(result.id) }
                    )
                }
            }
        }

        // Download button
        Button(
            onClick = { viewModel.downloadSelectedData() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && uiState.cloudTopics.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            )
        ) {
            Icon(Icons.Default.CloudDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tải xuống dữ liệu", fontSize = 16.sp)
        }
    }
}

@Composable
fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 14.sp)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    selectAll: Boolean,
    onSelectAllToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chọn tất cả", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = selectAll,
                    onCheckedChange = { onSelectAllToggle() }
                )
            }
        }
    }
}

@Composable
fun SelectableItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}