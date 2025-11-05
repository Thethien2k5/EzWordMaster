package com.example.ezwordmaster.ui.screens.regime.practice.flash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.SwipeDirection
import kotlinx.coroutines.delay

@Composable
fun FlashcardScreen(
    navController: NavHostController,
    topicId: String?,
    viewModel: FlashcardViewModel
) {
    val UISTATE by viewModel.uiState.collectAsState()

    var swipeDirection by remember { mutableStateOf<SwipeDirection?>(null) }
    var meaningVisible by remember { mutableStateOf(false) }

    // Tải chủ đề theo ID
    LaunchedEffect(topicId) {
        viewModel.loadTopic(topicId ?: "Lỗi không thấy id")
    }

    val WORDS = UISTATE.words
    val CURRENTINDEX = UISTATE.currentIndex
    val CURRENTWORD =
        if (WORDS.isNotEmpty() && CURRENTINDEX < WORDS.size) WORDS[CURRENTINDEX] else null

    // Đặt lại trạng thái lật và hướng vuốt khi chuyển sang từ tiếp theo
    LaunchedEffect(CURRENTINDEX) {
        meaningVisible = false
        swipeDirection = null
    }

    // Kiểm soát việc hiển thị nghĩa
    LaunchedEffect(UISTATE.isFlipped) {
        if (UISTATE.isFlipped) {
            // Khi lật để xem nghĩa, đợi nửa animation (300ms) rồi mới cho hiện
            delay(300)
            meaningVisible = true
        } else {
            // Khi lật lại, ẩn nghĩa ngay lập tức
            meaningVisible = false
        }
    }

    // Chuyển đến màn hình kết quả khi hoàn thành và lưu kết quả
    LaunchedEffect(UISTATE.isCompleted) {
        if (UISTATE.isCompleted) {
            val finalTopicId = UISTATE.topic?.id ?: topicId ?: "unknown"
            val topicName = UISTATE.topic?.name ?: "Unknown"
            val totalWords = UISTATE.words.size

            navController.navigate("result/${finalTopicId}/${topicName}/${UISTATE.knownWords}/${UISTATE.learningWords}/${totalWords}") {
                popUpTo("practice") { inclusive = false }
            }
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
                        .clickable { navController.navigate("wordpractice/$topicId") }
                )

                // Chỉ báo tiến độ
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "${CURRENTINDEX + 1}/${WORDS.size}",
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

                // Chưa nhớ
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "${UISTATE.learningWords}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                // Đã nhớ
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "${UISTATE.knownWords}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }


            }

            Spacer(modifier = Modifier.weight(1f))

            // Thẻ flashcard với hiệu ứng lật
            if (CURRENTWORD != null) {
                AnimatedFlashcard(
                    word = CURRENTWORD.word ?: "",
                    meaning = CURRENTWORD.meaning ?: "",
                    isFlipped = UISTATE.isFlipped,
                    meaningVisible = meaningVisible,
                    swipeDirection = swipeDirection,
                    isEnabled = !UISTATE.isProcessing,
                    onFlip = { viewModel.flipCard() },
                    onSwipeLeft = {
                        // Vuốt trái - Đang học
                        swipeDirection = SwipeDirection.LEFT
                        viewModel.onSwipe(isKnown = false)
                    },
                    onSwipeRight = {
                        // Vuốt phải - Đã nhớ
                        swipeDirection = SwipeDirection.RIGHT
                        viewModel.onSwipe(isKnown = true)
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
                        painter = painterResource(id = R.drawable.ic_triangle),
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                            .rotate(180f)
                            .clickable {
                                viewModel.goToPreviousWord()
                            }
                    )
                }

                // Văn bản hướng dẫn
                Text(
                    text = "Vuốt sang phải: đã nhớ\nVuốt sang trái: chưa nhớ\nNhấn để lật",
                    fontSize = 17.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-24).dp)
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
                                viewModel.goToNextWord()
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
    meaningVisible: Boolean,
    swipeDirection: SwipeDirection?,
    isEnabled: Boolean,
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
        SwipeDirection.RIGHT -> Color(0xFF4CAF50)
        SwipeDirection.LEFT -> Color(0xFFF44336)
        null -> Color.Transparent                 // Không có viền
    }
    // Biến để theo dõi tổng quãng đường vuốt theo chiều ngang
    var offsetX by remember { mutableStateOf(0f) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .pointerInput(Unit) {
                if (!isEnabled) return@pointerInput
                detectDragGestures(
                    onDragStart = {
                        // Reset lại quãng đường khi bắt đầu vuốt
                        offsetX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Cộng dồn quãng đường vuốt ngang
                        offsetX += dragAmount.x
                    },
                    onDragEnd = {
                        val swipeThreshold = 200f // Có thể tăng ngưỡng vuốt
                        // Xử lý khi kết thúc vuốt
                        if (offsetX > swipeThreshold) {
                            // Vuốt đủ xa sang phải
                            onSwipeRight()
                        } else if (offsetX < -swipeThreshold) {
                            // Vuốt đủ xa sang trái
                            onSwipeLeft()
                        }
                        // Reset lại sau khi xử lý
                        offsetX = 0f
                    }
                )
            }
            .clickable { onFlip() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier
                        .background(
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp)
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
                        .alpha(if (meaningVisible) 1f else 0f)
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