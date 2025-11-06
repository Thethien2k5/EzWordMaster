package com.example.ezwordmaster.ui.screens.translation

import androidx.compose.foundation.Image // <-- SỬA 1: THÊM IMPORT NÀY
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder // <-- SỬA 2: THÊM IMPORT NÀY
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz // <-- SỬA 3: THÊM IMPORT NÀY
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource // <-- SỬA 4: THÊM IMPORT NÀY
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.ui.common.CommonTopAppBar
import java.net.URLEncoder

@Composable
fun TranslationScreen(
    navController: NavHostController,
    viewModel: TranslationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEnToVi by viewModel.isEnToVi.collectAsState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.inputText) {
        inputText = uiState.inputText
    }

    val displayResult = remember { mutableStateOf(uiState.currentTranslation) }
    var displayedWord by remember { mutableStateOf("") }

    LaunchedEffect(uiState.currentTranslation) {
        if (uiState.currentTranslation != null && uiState.currentTranslation?.error == null) {
            displayResult.value = uiState.currentTranslation
            displayedWord = uiState.inputText
        }
    }

    LaunchedEffect(inputText) {
        if (inputText.isBlank()) {
            displayResult.value = null
            displayedWord = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CommonTopAppBar(
                title = if (isEnToVi) "Từ Điển Anh - Việt" else "Từ Điển Việt - Anh",
                canNavigateBack = false,
                onNavigateUp = { /* No action */ },
                onLogoClick = { /* No action */ },
                actions = {
                    IconButton(onClick = { viewModel.swapLanguage() }) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Tráo đổi ngôn ngữ",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )

            // Search Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Tìm kiếm từ điển",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Input
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = {
                                inputText = it
                                viewModel.setInputText(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (inputText.isEmpty()) {
                                        Text(
                                            text = if (isEnToVi) "Nhập từ tiếng Anh..." else "Nhập từ tiếng Việt...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        if (inputText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    inputText = ""
                                    viewModel.setInputText("")
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    if (uiState.isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Đang dịch...")
                        }
                    }
                }
            }

            // Results Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    displayResult.value != null -> {
                        val translation = displayResult.value!!
                        if (translation.error == null) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    WordInfoCard(
                                        word = displayedWord,
                                        phonetic = translation.phonetic,
                                        partOfSpeech = translation.partOfSpeech,
                                        onSaveClick = {
                                            val word = displayedWord
                                            val meaning = translation.translatedText

                                            val encodedWord = URLEncoder.encode(word, "UTF-8")
                                            val encodedMeaning = URLEncoder.encode(meaning, "UTF-8")

                                            // Điều hướng đến màn hình Thêm từ (topicId là "new" để tạo mới)
                                            navController.navigate("edittopic/new?word=$encodedWord&meaning=$encodedMeaning")
                                        }
                                    )
                                }

                                item {
                                    DefinitionCard(
                                        title = if (isEnToVi) "Nghĩa tiếng Việt" else "Nghĩa tiếng Anh",
                                        definition = translation.translatedText,
                                        englishDefinition = if (isEnToVi) translation.englishDefinition else "",
                                        example = translation.example
                                    )
                                }

                                if (translation.synonyms.isNotEmpty()) {
                                    item {
                                        WordListCard(
                                            title = "Từ đồng nghĩa",
                                            words = translation.synonyms
                                        )
                                    }
                                }

                                if (translation.antonyms.isNotEmpty()) {
                                    item {
                                        WordListCard(
                                            title = "Từ trái nghĩa",
                                            words = translation.antonyms
                                        )
                                    }
                                }
                            }
                        } else {
                            // (Code Error Card của bạn)
                        }
                    }

                    uiState.error != null -> {
                        // (Code Error State của bạn)
                    }

                    else -> {
                        // (Code Empty State của bạn)
                    }
                }
            }
        }
    }
}

@Composable
fun WordInfoCard(
    word: String,
    phonetic: String,
    partOfSpeech: String,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Lưu từ",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (phonetic.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Phiên âm",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "/$phonetic/",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                if (partOfSpeech.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Từ loại",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = partOfSpeech,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefinitionCard(
    title: String,
    definition: String,
    englishDefinition: String,
    example: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = definition,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (englishDefinition.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "English definition",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = englishDefinition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontStyle = FontStyle.Italic
                )
            }

            if (example.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "📝 Ví dụ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordListCard(title: String, words: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                words.take(10).forEach { word ->
                    Text(
                        text = "• $word",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}