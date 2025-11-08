package com.example.ezwordmaster.data.di

import com.example.ezwordmaster.data.remote.DictionaryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Cung cấp "gián điệp" để log lỗi (bạn đã có thư viện này)
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Log cả nội dung
        return interceptor
    }

    // Cung cấp OkHttpClient (có gắn gián điệp)
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Dạy Hilt cách tạo DictionaryApi (CHÚNG TA GIỮ LẠI API NÀY)
    @Provides
    @Singleton
    fun provideDictionaryApi(okHttpClient: OkHttpClient): DictionaryApi {
        return Retrofit.Builder()
            .baseUrl(DictionaryApi.BASE_URL)
            .client(okHttpClient) // Dùng client có log
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryApi::class.java)
    }

    // Chúng ta đã XÓA hàm provideLibreTranslateApi()
}