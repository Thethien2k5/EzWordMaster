package com.example.ezwordmaster.ui.translate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ezwordmaster.data.remote.dto.WordInfoDto
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import com.example.ezwordmaster.ui.common.GradientBackground
import com.example.ezwordmaster.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(
    navController: NavHostController,
    viewModel: TranslateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    GradientBackground {
        Scaffold(
            topBar = {
                CommonTopAppBar(
                    title = "Translate",
                    canNavigateBack = true,
                    onNavigateUp = { navController.popBackStack() },
                    onLogoClick = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Ô nhập liệu
                OutlinedTextField(
                    value = uiState.searchInput,
                    onValueChange = viewModel::onSearchInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter an English word...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.searchWord()
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.searchWord()
                        focusManager.clearFocus()
                    })
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Khu vực kết quả
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        uiState.isLoading -> CircularProgressIndicator()
                        uiState.error != null -> Text(uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                        uiState.wordInfo != null -> {
                            // ✅ Gọi Composable con để hiển thị kết quả chi tiết
                            WordResult(
                                wordInfo = uiState.wordInfo!!,
                                translatedMeanings = uiState.translatedMeanings
                            )
                        }
                        else -> Text("Bản dịch chi tiết sẽ xuất hiện ở đây.", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

// ✅ Composable mới để hiển thị kết quả chi tiết, giống ZIM
@Composable
fun WordResult(wordInfo: WordInfoDto, translatedMeanings: Map<String, String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Từ và phiên âm
        Text(text = wordInfo.word, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        wordInfo.phonetic?.let {
            Text(text = it, fontSize = 18.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 2. Lặp qua các loại từ (danh từ, động từ...)
        wordInfo.meanings.forEach { meaning ->
            Text(
                text = meaning.partOfSpeech.replaceFirstChar { it.uppercase() },
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 3. Lặp qua các định nghĩa của loại từ đó
            meaning.definitions.forEachIndexed { index, definition ->
                // Định nghĩa gốc (tiếng Anh)
                Text(
                    text = "${index + 1}. ${definition.definition}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
                // Bản dịch (tiếng Việt)
                val vietnameseTranslation = translatedMeanings[definition.definition]
                if (vietnameseTranslation != null) {
                    Text(
                        text = vietnameseTranslation,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 16.dp),
                        lineHeight = 22.sp
                    )
                }
                // Câu ví dụ
                definition.example?.let {
                    Text(
                        text = "e.g. \"$it\"",
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF6A6A6A),
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslateScreenPreview() {
    TranslateScreen(navController = rememberNavController())
}