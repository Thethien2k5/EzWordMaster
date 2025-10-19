package com.example.ezwordmaster.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ezwordmaster.domain.model.Word
import com.example.ezwordmaster.domain.model.StudyResult
import com.example.ezwordmaster.domain.repository.StudyResultRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class CardItem(
    val id: String,
    val text: String,
    val isWord: Boolean, // true = từ, false = nghĩa
    val pairId: String, // ID để xác định cặp từ-nghĩa
    val isMatched: Boolean = false,
    val isFlipped: Boolean = false,
    val isWrong: Boolean = false // Thêm trạng thái để hiển thị viền đỏ khi sai
)

@Composable
fun FlipCardScreen(
    navController: NavHostController, 
    topicId: String?, 
    wordsJson: String?
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val studyResultRepository = remember { StudyResultRepository(context) }
    
    var flippedCards by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var matchedPairs by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    var cards by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var wrongCards by remember { mutableStateOf<Set<String>>(emptySet()) } // Lưu trữ ID của thẻ sai
    var correctCards by remember { mutableStateOf<Set<String>>(emptySet()) } // Lưu trữ ID của thẻ đúng
    var startTime by remember { mutableStateOf(0L) } // Thời gian bắt đầu chơi
    val coroutineScope = rememberCoroutineScope()
    
    // Phân tích từ từ chuỗi JSON và tạo thẻ
    LaunchedEffect(wordsJson) {
        if (!wordsJson.isNullOrEmpty()) {
            val words = wordsJson.split(",").mapNotNull { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    Word(word = parts[0], meaning = parts[1])
                } else null
            }
            
            val cardItems = mutableListOf<CardItem>()
            words.forEachIndexed { index, word ->
                val pairId = "pair_$index"
                // Thêm thẻ từ
                cardItems.add(CardItem(
                    id = "word_$index",
                    text = word.word ?: "",
                    isWord = true,
                    pairId = pairId
                ))
                // Thêm thẻ nghĩa
                cardItems.add(CardItem(
                    id = "meaning_$index",
                    text = word.meaning ?: "",
                    isWord = false,
                    pairId = pairId
                ))
            }
            cards = cardItems.shuffled() // Xáo trộn thẻ
            startTime = System.currentTimeMillis() // Ghi nhận thời gian bắt đầu
        }
    }
    
    // Kiểm tra xem tất cả cặp đã được ghép chưa
    LaunchedEffect(matchedPairs, cards.size) {
        if (cards.isNotEmpty() && matchedPairs >= cards.size / 2) {
            delay(500) // Độ trễ nhỏ để chuyển tiếp mượt mà
            isCompleted = true
        }
    }
    
    // Chuyển đến màn hình kết quả khi hoàn thành và lưu kết quả
    LaunchedEffect(isCompleted) {
        if (isCompleted && topicId != null) {
            // Lưu kết quả trò chơi
            val endTime = System.currentTimeMillis()
            val playTime = (endTime - startTime) / 1000 // chuyển từ ms sang giây
            val studyResult = StudyResult.createFlipCardResult(
                id = UUID.randomUUID().toString(),
                topicId = topicId,
                topicName = "FlipCard Game", // Có thể lấy từ topic nếu cần
                startTime = startTime,
                endTime = endTime,
                totalPairs = cards.size / 2,
                matchedPairs = matchedPairs,
                playTime = playTime
            )
            studyResultRepository.addStudyResult(studyResult)
            
            // Chuyển đến màn hình kết quả
            navController.navigate("flipresult/$topicId/$matchedPairs")
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA2EAF8),
                        Color(0xFFAAFFA7)
                    )
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
                        .clickable { navController.popBackStack() }
                )
                
                // Chỗ trống để cân bằng bố cục
                Spacer(modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Chỉ báo tiến độ
            Text(
                text = "Đã ghép: $matchedPairs/${cards.size / 2}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Lưới thẻ - tự động điều chỉnh số cột dựa trên số lượng từ
            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    when {
                        cards.size <= 4 -> 2  // Ít từ: 2 cột
                        cards.size <= 9 -> 3  // Trung bình: 3 cột  
                        cards.size <= 16 -> 4 // Nhiều từ: 4 cột
                        else -> 5              // Rất nhiều từ: 5 cột
                    }
                ),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards) { card ->
                    FlipCardItem(
                        card = card,
                        isFlipped = flippedCards.any { it.id == card.id },
                        isMatched = card.isMatched,
                        isWrong = wrongCards.contains(card.id),
                        isCorrect = correctCards.contains(card.id),
                        totalCards = cards.size,
                        onClick = {
                            if (card.isMatched) return@FlipCardItem // Không cho phép nhấn thẻ đã ghép
                            
                            if (flippedCards.size < 2) {
                                // Thêm thẻ vào danh sách đã lật
                                flippedCards = flippedCards + card
                                
                                if (flippedCards.size == 2) {
                                    // Kiểm tra khớp
                                    val card1 = flippedCards[0]
                                    val card2 = flippedCards[1]
                                    
                                    val isMatch = (card1.isWord != card2.isWord) && 
                                                (card1.pairId == card2.pairId)
                                    
                                    if (isMatch) {
                                        // Hiển thị viền xanh cho cặp đúng
                                        correctCards = correctCards + card1.id + card2.id
                                        
                                        // Đặt lại thẻ đã lật sau delay để người dùng thấy viền xanh
                                        coroutineScope.launch {
                                            delay(1000) // Độ trễ để người dùng thấy viền xanh và đọc nội dung
                                            
                                            // Sau delay, đánh dấu matched và ẩn thẻ
                                            matchedPairs++
                                            cards = cards.map { c ->
                                                if (c.id == card1.id || c.id == card2.id) {
                                                    c.copy(isMatched = true)
                                                } else c
                                            }
                                            flippedCards = emptyList()
                                            correctCards = emptySet()
                                        }
                                    } else {
                                        // Không khớp - hiển thị viền đỏ
                                        wrongCards = wrongCards + card1.id + card2.id
                                        
                                        // Đặt lại thẻ đã lật và xóa trạng thái sai sau delay dài hơn
                                        coroutineScope.launch {
                                            delay(1500) // Độ trễ dài hơn để người dùng thấy viền đỏ và đọc nội dung
                                            flippedCards = emptyList()
                                            wrongCards = emptySet()
                                        }
                                    }
                                }
                            }
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
    // Xác định màu viền dựa trên trạng thái
    val borderColor = when {
        isMatched -> Color.Transparent // Thẻ đã khớp không cần viền
        isCorrect -> Color(0xFF4CAF50) // Xanh lá cho thẻ đúng (trước khi matched)
        isWrong -> Color(0xFFF44336)   // Đỏ cho thẻ sai
        else -> Color.Transparent      // Không có viền cho thẻ bình thường
    }
    
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMatched) Color.Transparent else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (!isMatched) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (borderColor != Color.Transparent) {
                            Modifier.background(
                                color = borderColor,
                                shape = RoundedCornerShape(8.dp)
                            ).padding(2.dp)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFlipped) card.text else "?",
                            fontSize = when {
                                totalCards <= 4 -> 16.sp   // Ít từ: font lớn
                                totalCards <= 9 -> 14.sp   // Trung bình: font vừa
                                totalCards <= 16 -> 12.sp  // Nhiều từ: font nhỏ
                                else -> 10.sp              // Rất nhiều từ: font rất nhỏ
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
    }
}
