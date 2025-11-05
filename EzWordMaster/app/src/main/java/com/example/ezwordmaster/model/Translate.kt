package com.example.ezwordmaster.model

data class translateWords(
    val meanings: List<MeaningDto>,
    val phonetic: String?,
    val word: String
)