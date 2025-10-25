package com.example.ezwordmaster.model

data class CardItem(
    val id: String,
    val text: String,
    val isWord: Boolean, // true = từ, false = nghĩa
    val pairId: String, // ID để xác định cặp từ-nghĩa
    val isMatched: Boolean = false,
    val isFlipped: Boolean = false,
    val isWrong: Boolean = false // Thêm trạng thái để hiển thị viền đỏ khi sai
)