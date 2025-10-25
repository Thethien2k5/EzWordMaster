package com.example.ezwordmaster.model

// Các class này khớp với cấu trúc JSON mà API trả về
data class WordInfo(
    val meanings: List<MeaningDto>,
    val phonetic: String?,
    val word: String
)



