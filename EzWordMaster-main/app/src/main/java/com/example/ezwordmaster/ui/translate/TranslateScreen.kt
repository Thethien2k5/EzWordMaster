package com.example.ezwordmaster.ui.translate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TranslateScreen(translateViewModel: TranslateViewModel = viewModel()) {
    val inputText by translateViewModel.inputText.collectAsState()
    val translatedText by translateViewModel.translatedText.collectAsState()
    val isLoading by translateViewModel.isLoading.collectAsState()

    Scaffold(topBar = { /* TopBar */ }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { translateViewModel.onInputTextChanged(it) },
                label = { Text("Enter text to translate") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { translateViewModel.performTranslation() },
                enabled = !isLoading
            ) {
                Text("Translate")
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = translatedText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}