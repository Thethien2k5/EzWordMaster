package com.example.ezwordmaster.model

data class DefinitionDto(
    val definition: String,
    val example: String?
)

data class MeaningDto(
    val definitions: List<DefinitionDto>,
    val partOfSpeech: String
)