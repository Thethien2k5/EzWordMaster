package com.example.ezwordmaster.model

data class MeaningDto(
    val definitions: List<DefinitionDto>,
    val partOfSpeech: String
)