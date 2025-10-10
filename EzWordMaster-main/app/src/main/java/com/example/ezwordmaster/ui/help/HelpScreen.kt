package com.example.ezwordmaster.ui.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HelpScreen(helpViewModel: HelpViewModel = viewModel()) {
    val helpItems by helpViewModel.helpItems.collectAsState()
    var selectedItem by remember { mutableStateOf<HelpItem?>(null) }

    Scaffold(
        topBar = { /* Tương tự AboutScreen */ }
    ) { paddingValues ->
        if (selectedItem == null) {
            // Hiển thị danh sách câu hỏi
            LazyColumn(
                modifier = Modifier.padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Help", style = MaterialTheme.typography.headlineMedium) }
                items(helpItems) { item ->
                    Text(
                        text = item.question,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedItem = item }
                            .padding(vertical = 16.dp)
                    )
                }
            }
        } else {
            // Hiển thị câu trả lời
            HelpAnswerContent(
                item = selectedItem!!,
                onBack = { selectedItem = null }
            )
        }
    }
}

@Composable
fun HelpAnswerContent(item: HelpItem, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Help Answer", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(item.question, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(item.answer)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack) {
            Text("Back to questions")
        }
    }
}