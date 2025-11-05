package com.example.ezwordmaster.ui.screens.regime.entertainment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.CardItem

@Composable
fun FlipCardScreen(
    navController: NavHostController,
    topicId: String?,
    wordsJson: String?,
    viewModel: FlipCardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Tải và thiết lập game lần đầu
    LaunchedEffect(wordsJson) {
        if (!topicId.isNullOrEmpty() && !wordsJson.isNullOrEmpty()) {
            viewModel.setupGame(topicId, wordsJson)
        }
    }

    // Xử lý điều hướng khi game hoàn thành
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            val topicName = uiState.topic?.name ?: "Unknown"
            navController.navigate("flipresult/$topicId/$topicName/${uiState.matchedPairs}") {
                popUpTo("practice") { inclusive = false }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFA2EAF8), Color(0xFFAAFFA7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header với nút back
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.return_),
                    contentDescription = "Back",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { navController.navigate("home/PRACTICE") }
                )
                Spacer(modifier = Modifier.size(45.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chỉ báo tiến độ
            Text(
                text = "Đã ghép: ${uiState.matchedPairs}/${uiState.cards.size / 2}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lưới thẻ
            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    when {
                        uiState.cards.size <= 4 -> 2
                        uiState.cards.size <= 9 -> 3
                        uiState.cards.size <= 16 -> 4
                        else -> 5
                    }
                ),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.cards, key = { it.id }) { card ->
                    FlipCardItem(
                        card = card,
                        isFlipped = uiState.flippedCards.any { it.id == card.id },
                        isMatched = card.isMatched,
                        // Sửa lại để lấy đúng trạng thái từ ViewModel
                        isWrong = uiState.wrongCardIds.contains(card.id),
                        isCorrect = uiState.correctCardIds.contains(card.id),
                        totalCards = uiState.cards.size,
                        onClick = {
                            viewModel.onCardClicked(card)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FlipCardItem(
    card: CardItem,
    isFlipped: Boolean,
    isMatched: Boolean,
    isWrong: Boolean,
    isCorrect: Boolean,
    totalCards: Int,
    onClick: () -> Unit
) {
    val BORDER_COLOR = when {
        isCorrect -> Color(0xFF4CAF50)
        isWrong -> Color(0xFFF44336)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = !isMatched, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMatched) Color.Transparent else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isMatched) 0.dp else 4.dp)
    ) {
        if (!isMatched) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BORDER_COLOR, shape = RoundedCornerShape(8.dp))
                    .padding(2.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFlipped) card.text else "?",
                    fontSize = when {
                        totalCards <= 4 -> 16.sp
                        totalCards <= 9 -> 14.sp
                        totalCards <= 16 -> 12.sp
                        else -> 10.sp
                    },
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}