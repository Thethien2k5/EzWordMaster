package com.example.ezwordmaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ezwordmaster.data.local.converters.DateConverter
import com.example.ezwordmaster.data.local.converters.StringListConverter
import java.util.Date

@Entity(tableName = "translation_history")
@TypeConverters(DateConverter::class, StringListConverter::class)
data class TranslationHistoryEntity(
    @PrimaryKey
    val id: String,
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val phonetic: String = "",
    val partOfSpeech: String = "",
    val example: String = "",
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val timestamp: Date = Date()
)