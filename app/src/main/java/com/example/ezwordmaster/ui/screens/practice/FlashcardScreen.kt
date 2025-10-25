package com.example.ezwordmaster.ui.screens.practice

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
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.StudyResult
import kotlinx.coroutines.delay
import java.util.UUID
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.alpha

enum class SwipeDirection {
    LEFT, RIGHT
}

@Composable
fun FlashcardScreen(navController: NavHostController, topicId: String?, viewModel: FlashcardViewModel) {
    val uiState by viewModel.UISTATE.collectAsState()

    var topic by remember { mutableStateOf<Topic?>(null) }
    var currentIndex by remember { mutableStateOf(0) }
    var knownWords by remember { mutableStateOf(0) }
    var learningWords by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    var isFlipped by remember { mutableStateOf(false) } // Trạng thái lật thẻ
    var swipeDirection by remember { mutableStateOf<SwipeDirection?>(null) } // Hướng vuốt hiện tại
    var startTime by remember { mutableStateOf(0L) } // Thời gian bắt đầu học
    var meaningVisible by remember { mutableStateOf(false) } //Đảm bảo nghĩa luôn bị ẩn khi sang từ mới

    // Tải chủ đề theo ID và khởi tạo thời gian bắt đầu
    LaunchedEffect(topicId) {
        viewModel.loadTopic(topicId?:"Lỗi không thấy id")
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
        meaningVisible = false
        swipeDirection = null
    }

    //kiểm soát việc hiển thị nghĩa
    LaunchedEffect(isFlipped) {
        if (isFlipped) {
            // Khi lật để xem nghĩa, đợi nửa animation (300ms) rồi mới cho hiện
            delay(300)
            meaningVisible = true
        } else {
            // Khi lật lại, ẩn nghĩa ngay lập tức
            meaningVisible = false
        }
    }

    // Chuyển đến màn hình kết quả khi hoàn thành và lưu kết quả
    LaunchedEffect(uiState.ISCOMPLETED) {
        if (uiState.ISCOMPLETED) {
            val topicName = uiState.TOPIC?.name ?: "Unknown"
            // Điều hướng đến màn hình kết quả và xóa các màn hình ôn tập trước đó khỏi stack
            navController.navigate("result/${topicId}/${topicName}/${uiState.KNOWNWORDS}/${uiState.LEARNINGWORDS}") {
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
                        .clickable { navController.navigate("practice")}
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

                // Chư nhớ
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
                // Đã nhớ
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


            }

            Spacer(modifier = Modifier.weight(1f))

            // Thẻ flashcard với hiệu ứng lật
            if (currentWord != null) {
                AnimatedFlashcard(
                    word = currentWord.word ?: "",
                    meaning = currentWord.meaning ?: "",
                    isFlipped = isFlipped,
                    meaningVisible = meaningVisible,
                    swipeDirection = swipeDirection,
                    onFlip = { isFlipped = !isFlipped },
                    onSwipeLeft = {
                        // Vuốt trái - Đang học
                        swipeDirection = SwipeDirection.LEFT
                        learningWords++
                        // Reset trạng thái NGAY LẬP TỨC trước khi chuyển từ
                        isFlipped = false
                        meaningVisible = false
                        currentIndex++
                    },
                    onSwipeRight = {
                        // Vuốt phải - Đã nhớ
                        swipeDirection = SwipeDirection.RIGHT
                        knownWords++
                        // Reset trạng thái NGAY LẬP TỨC trước khi chuyển từ
                        isFlipped = false
                        meaningVisible = false
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
                        painter = painterResource(id = R.drawable.ic_triangle),
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                            .rotate(180f)
                            .clickable {
                                if (currentIndex > 0) {
                                    currentIndex--
                                }
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
    meaningVisible: Boolean,
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
