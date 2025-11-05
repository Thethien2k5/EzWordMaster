package com.example.ezwordmaster.ui.screens.topic_managment

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.Word
import com.example.ezwordmaster.ui.common.AppBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopicScreen(
    navController: NavHostController,
    topicId: String,
    viewModel: TopicViewModel
) {
    val TOPIC by viewModel.selectedTopic.collectAsState()

    val TOASTMESSAGE by viewModel.toastMessage.collectAsState()
    val SNACKBARHOSTSTATE = remember { SnackbarHostState() }
    val COROUTINESCOPE = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showDeleteTopicDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAddWordDialog by remember { mutableStateOf(false) }
    var showEditWordDialog by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf<Word?>(null) }

    LaunchedEffect(TOASTMESSAGE) {
        TOASTMESSAGE?.let { message ->
            COROUTINESCOPE.launch {
                SNACKBARHOSTSTATE.showSnackbar(message)
            }
            viewModel.clearToastMessage() // Rất quan trọng: Xóa lỗi sau khi hiển thị
        }
    }

    // Hàm để tải lại dữ liệu chủ đề sau khi có thay đổi
    LaunchedEffect(topicId) {
        viewModel.loadTopicById(topicId)
    }

    AppBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = SNACKBARHOSTSTATE) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút quay lại
                    Image(
                        painter = painterResource(id = R.drawable.return_),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navController.navigate("home/MANAGEMENT") }
                    )

                    // Tên chủ đề + Icon sửa
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = TOPIC?.name ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit name",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { showEditNameDialog = true },
                            tint = Color(0xFF00BCD4)
                        )
                    }

                    // Icon xóa chủ đề
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete topic",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showDeleteTopicDialog = true },
                        tint = Color.Red
                    )
                }

                // Thanh tìm kiếm
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm từ vựng", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.magnifying_glass),
                            contentDescription = "Search",
                            tint = Color(0xFF00BCD4)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nút thêm từ vựng
                Button(
                    onClick = { showAddWordDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BCD4)
                    )
                ) {
                    Text(
                        "Thêm từ vựng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Danh sách từ vựng
                TOPIC?.let { currentTopic ->
                    val filteredWords = currentTopic.words.filter {
                        it.word?.contains(searchQuery.text, ignoreCase = true) ?: false ||
                                it.meaning?.contains(searchQuery.text, ignoreCase = true) ?: false
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(filteredWords) { word ->
                            WordItem(
                                word = word,
                                onClick = {
                                    selectedWord = word
                                    showEditWordDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Dialog xác nhận xóa chủ đề
        if (showDeleteTopicDialog) {
            ConfirmDeleteDialog(
                title = "Xác nhận xóa chủ đề",
                message = "Bạn có chắc chắn muốn xóa chủ đề này không?",
                onDismiss = { showDeleteTopicDialog = false },
                onConfirm = {
                    viewModel.deleteTopicById(topicId)
                    showDeleteTopicDialog = false
                    navController.popBackStack()
                }
            )
        }

        // Dialog sửa tên chủ đề
        if (showEditNameDialog) {
            EditTopicNameDialog(
                currentName = TOPIC?.name ?: "",
                onDismiss = { showEditNameDialog = false },
                onConfirm = { newName ->
                    viewModel.updateTopicName(topicId, newName)
                    showEditNameDialog = false
                }
            )
        }

        // Dialog thêm từ vựng
        if (showAddWordDialog) {
            AddEditWordDialog(
                title = "Thêm từ vựng",
                word = null,
                onDismiss = { showAddWordDialog = false },
                onConfirm = { newWord ->
                    viewModel.addWordToTopic(topicId, newWord)
                    showAddWordDialog = false
                }
            )
        }

        // Dialog sửa từ vựng
        if (showEditWordDialog && selectedWord != null) {
            AddEditWordDialog(
                title = "Chỉnh sửa từ vựng",
                word = selectedWord,
                onDismiss = {
                    showEditWordDialog = false
                    selectedWord = null
                },
                onConfirm = { newWord ->
                    viewModel.updateWordInTopic(topicId, selectedWord!!, newWord)
                    showEditWordDialog = false
                    selectedWord = null
                },
                onDelete = {
                    viewModel.deleteWordFromTopic(topicId, selectedWord!!)
                    showEditWordDialog = false
                    selectedWord = null
                }
            )
        }
    }
}

@Composable
fun WordItem(
    word: Word,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.word ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = word.meaning ?: "",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Edit",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF00BCD4)
            )
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFC2DDEF),
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Text(
                message,
                color = Color.Black
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy bỏ", color = Color.Gray)
            }
        }
    )
}

@Composable
fun EditTopicNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var topicName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFC2DDEF),
        title = {
            Text(
                "Chỉnh sửa tên chủ đề",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            OutlinedTextField(
                value = topicName,
                onValueChange = { topicName = it },
                placeholder = { Text("Tên chủ đề", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (topicName.isNotBlank()) {
                        onConfirm(topicName.trim())
                    }
                }
            ) {
                Text("OK", color = Color(0xFF00BCD4), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy bỏ", color = Color.Gray)
            }
        }
    )
}

@Composable
fun AddEditWordDialog(
    title: String,
    word: Word?,
    onDismiss: () -> Unit,
    onConfirm: (Word) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var wordText by remember { mutableStateOf(word?.word ?: "") }
    var meaningText by remember { mutableStateOf(word?.meaning ?: "") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFC2DDEF),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (onDelete != null) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete word",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showDeleteConfirm = true },
                        tint = Color.Red
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    "Văn bản gốc",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = wordText,
                    onValueChange = { wordText = it },
                    placeholder = { Text("Nhập từ vựng", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Văn bản dịch",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = meaningText,
                    onValueChange = { meaningText = it },
                    placeholder = { Text("Nhập nghĩa", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (wordText.isNotBlank() && meaningText.isNotBlank()) {
                        onConfirm(Word(wordText.trim(), meaningText.trim()))
                    }
                }
            ) {
                Text("OK", color = Color(0xFF00BCD4), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy bỏ", color = Color.Gray)
            }
        }
    )

    // Dialog xác nhận xóa từ vựng
    if (showDeleteConfirm && onDelete != null) {
        ConfirmDeleteDialog(
            title = "Xác nhận xóa từ vựng",
            message = "Bạn có chắc chắn muốn xóa từ này không?",
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                onDelete()
                showDeleteConfirm = false
            }
        )
    }
}