package com.example.ezwordmaster.data.remote.model

// Models cho LibreTranslate API
data class LibreTranslateRequest(
    val q: String,
    val source: String = "en",
    val target: String = "vi"
)

data class LibreTranslateResponse(
    val translatedText: String
)

// Model cho kết quả dịch chi tiết (GIỮ NGUYÊN)
data class DetailedTranslationResult(
    val translatedText: String,
    val englishDefinition: String = "",
    val sourceLanguage: String = "en",
    val targetLanguage: String = "vi",
    val phonetic: String = "",
    val partOfSpeech: String = "",
    val example: String = "",
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val error: String? = null
)

// Models cho Dictionary API (GIỮ NGUYÊN)
data class DictionaryResponse(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<Phonetic> = emptyList(),
    val meanings: List<Meaning> = emptyList()
)

data class Phonetic(
    val text: String? = null,
    val audio: String? = null
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

data class Definition(
    val definition: String,
    val example: String? = null,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)