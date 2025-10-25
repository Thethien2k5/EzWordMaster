package com.example.ezwordmaster.data.remote

import com.example.ezwordmaster.model.WordInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    // Định nghĩa endpoint của API
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordInfo(
        @Path("word") word: String
    ): List<WordInfo> // API trả về một danh sách kết quả

    companion object {
        const val BASE_URL = "https://api.dictionaryapi.dev/"
    }
}