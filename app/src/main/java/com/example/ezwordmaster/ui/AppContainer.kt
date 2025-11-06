package com.example.ezwordmaster.ui

import android.content.Context
import com.example.ezwordmaster.data.local.database.EzWordMasterDatabase
import com.example.ezwordmaster.data.local.repository.SettingsRepositoryImpl
import com.example.ezwordmaster.data.local.repository.StudyResultRepositoryImpl
import com.example.ezwordmaster.data.local.repository.TopicRepositoryImpl
import com.example.ezwordmaster.data.repository.NotificationRepositoryImpl
import com.example.ezwordmaster.data.repository.TranslationRepositoryImpl
import com.example.ezwordmaster.data.remote.DictionaryApi
import com.example.ezwordmaster.domain.repository.INotificationRepository
import com.example.ezwordmaster.domain.repository.ISettingsRepository
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.domain.repository.ITranslationRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {

    // Database
    private val database: EzWordMasterDatabase by lazy {
        EzWordMasterDatabase.getDatabase(context)
    }

    // --- Client để log lỗi (Giữ lại) ---
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // --- Dictionary API (VẪN GIỮ LẠI) ---
    private val dictionaryRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DictionaryApi.BASE_URL) // Dùng BASE_URL từ class
            .client(okHttpClient) // Dùng client có log
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val dictionaryApi: DictionaryApi by lazy {
        dictionaryRetrofit.create(DictionaryApi::class.java)
    }

    // --- LibreTranslate API (ĐÃ BỊ XÓA) ---
    // (Toàn bộ code cho libreTranslateRetrofit và libreTranslateApi đã bị xóa)

    // Tạo TopicRepository
    val topicRepository: ITopicRepository by lazy {
        TopicRepositoryImpl(context)
    }

    // Tạo StudyResultRepository
    val studyResultRepository: IStudyResultRepository by lazy {
        StudyResultRepositoryImpl(context)
    }

    val settingsRepository: ISettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    // --- Translation Repository (SỬA LẠI) ---
    // Chỉ còn dùng DictionaryApi, không dùng LibreTranslateApi nữa
    val translationRepository: ITranslationRepository by lazy {
        TranslationRepositoryImpl(
            dictionaryApi = dictionaryApi,
            translationHistoryDao = database.translationHistoryDao()
        )
    }

    // Notification Repository
    val notificationRepository: INotificationRepository by lazy {
        NotificationRepositoryImpl(
            notificationDao = database.notificationDao()
        )
    }
}