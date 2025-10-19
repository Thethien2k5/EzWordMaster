package com.example.ezwordmaster.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.domain.model.Topic
import com.example.ezwordmaster.domain.model.StudyResult
import com.example.ezwordmaster.domain.repository.TopicRepository
import com.example.ezwordmaster.domain.repository.StudyResultRepository
import kotlinx.coroutines.delay
import java.util.UUID

enum class SwipeDirection {
    LEFT, RIGHT
}

@Composable
fun FlashcardScreen(navController: NavHostController, topicId: String?) {
    val context = LocalContext.current
    val repository = remember { TopicRepository(context) }
    val studyResultRepository = remember { StudyResultRepository(context) }

    var topic by remember { mutableStateOf<Topic?>(null) }
    var currentIndex by remember { mutableStateOf(0) }
    var knownWords by remember { mutableStateOf(0) }
    var learningWords by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    var isFlipped by remember { mutableStateOf(false) } // Trạng thái lật thẻ
    var swipeDirection by remember { mutableStateOf<SwipeDirection?>(null) } // Hướng vuốt hiện tại
    var startTime by remember { mutableStateOf(0L) } // Thời gian bắt đầu học

    // Tải chủ đề theo ID và khởi tạo thời gian bắt đầu
    LaunchedEffect(topicId) {
        if (topicId != null) {
            val topics = repository.loadTopics()
            topic = topics.find { it.id == topicId }
            startTime = System.currentTimeMillis() // Ghi nhận thời gian bắt đầu
        }
    }

    val words = topic?.words ?: emptyList()
    val currentWord = if (words.isNotEmpty() && currentIndex < words.size) words[currentIndex] else null

    // Kiểm tra xem tất cả từ đã được xử lý chưa
    LaunchedEffect(currentIndex, words.size) {
        if (words.isNotEmpty() && currentIndex >= words.size) {
            delay(500) // Độ trễ nhỏ để chuyển tiếp mượt mà
            isCompleted = true
        }
    }
    
    // Đặt lại trạng thái lật và hướng vuốt khi chuyển sang từ tiếp theo
    LaunchedEffect(currentIndex) {
        isFlipped = false
        swipeDirection = null
    }

    // Chuyển đến màn hình kết quả khi hoàn thành và lưu kết quả
    LaunchedEffect(isCompleted) {
        if (isCompleted && topicId != null && topic != null) {
            // Lưu kết quả học tập
            val endTime = System.currentTimeMillis()
            val studyResult = StudyResult.createFlashcardResult(
                id = UUID.randomUUID().toString(),
                topicId = topicId,
                topicName = topic!!.name ?: "Unknown Topic",
                startTime = startTime,
                endTime = endTime,
                totalWords = words.size,
                knownWords = knownWords,
                learningWords = learningWords
            )
            studyResultRepository.addStudyResult(studyResult)
            
            // Chuyển đến màn hình kết quả
            navController.navigate("result/$topicId/$knownWords/$learningWords")
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
            // Header với nút back và progress
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

                // Chỉ báo tiến độ
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "${currentIndex + 1}/${words.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Chỗ trống để cân bằng bố cục
                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Huy hiệu thống kê
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Huy hiệu từ đã biết
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "$knownWords",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Huy hiệu từ đang học
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "$learningWords",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Thẻ flashcard với hiệu ứng lật
            if (currentWord != null) {
                AnimatedFlashcard(
                    word = currentWord.word ?: "",
                    meaning = currentWord.meaning ?: "",
                    isFlipped = isFlipped,
                    swipeDirection = swipeDirection,
                    onFlip = { isFlipped = !isFlipped },
                    onSwipeLeft = {
                        // Vuốt trái - Đang học
                        swipeDirection = SwipeDirection.LEFT
                        learningWords++
                        currentIndex++
                    },
                    onSwipeRight = {
                        // Vuốt phải - Đã nhớ
                        swipeDirection = SwipeDirection.RIGHT
                        knownWords++
                        currentIndex++
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Các nút điều hướng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút trước
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.return_),
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                            .clickable {
                                if (currentIndex > 0) {
                                    currentIndex--
                                }
                            }
                    )
                }

                // Văn bản hướng dẫn
                Text(
                    text = "Vuốt phải: đã nhớ\nVuốt trái: đang học\nNhấn thẻ: lật",
                    fontSize = 12.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                // Nút tiếp theo
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_triangle),
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                            .clickable {
                                if (currentIndex < words.size - 1) {
                                    currentIndex++
                                }
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AnimatedFlashcard(
    word: String,
    meaning: String,
    isFlipped: Boolean,
    swipeDirection: SwipeDirection?,
    onFlip: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    // Hiệu ứng cho chuyển động lật 3D
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardRotation"
    )
    
    // Xác định màu viền dựa trên hướng vuốt
    val borderColor = when (swipeDirection) {
        SwipeDirection.RIGHT -> Color(0xFF4CAF50) // Xanh lá cho "đã nhớ"
        SwipeDirection.LEFT -> Color(0xFFF44336)  // Đỏ cho "đang học"
        null -> Color.Transparent                 // Không có viền
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Xử lý cử chỉ vuốt
                    }
                ) { change, _ ->
                    // Phát hiện hướng vuốt
                    val swipeThreshold = 100f
                    if (change.position.x - change.previousPosition.x > swipeThreshold) {
                        // Vuốt phải - Đã nhớ
                        onSwipeRight()
                    } else if (change.position.x - change.previousPosition.x < -swipeThreshold) {
                        // Vuốt trái - Đang học
                        onSwipeLeft()
                    }
                }
            }
            .clickable { onFlip() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.background(
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    ).padding(4.dp)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Mặt trước (Tiếng Anh) - chỉ hiển thị khi không lật hoặc đang lật
            if (rotation <= 90f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (rotation > 90f) {
                                alpha = 0f
                            }
                        }
                ) {
                    Text(
                        text = word,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Từ vựng",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Mặt sau (Tiếng Việt) - chỉ hiển thị khi đã lật hoặc đang lật
            if (rotation >= 90f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                            if (rotation < 90f) {
                                alpha = 0f
                            }
                        }
                ) {
                    Text(
                        text = meaning,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nghĩa",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
