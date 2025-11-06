package com.example.ezwordmaster.data.remote

import com.example.ezwordmaster.data.remote.model.DictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordDefinition(@Path("word") word: String): List<DictionaryResponse>

    companion object {
        const val BASE_URL = "https://api.dictionaryapi.dev/"
    }
}