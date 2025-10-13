package com.example.ezwordmaster.data.remote.dto

// Các class này khớp với cấu trúc JSON mà API trả về
data class WordInfoDto(
    val meanings: List<MeaningDto>,
    val phonetic: String?,
    val word: String
)

data class MeaningDto(
    val definitions: List<DefinitionDto>,
    val partOfSpeech: String
)

data class DefinitionDto(
    val definition: String,
    val example: String?
)