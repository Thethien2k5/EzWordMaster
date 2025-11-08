package com.example.ezwordmaster.ui

import android.content.Context
import com.example.ezwordmaster.data.cloud.CloudStudyResultRepository
import com.example.ezwordmaster.data.cloud.CloudTopicRepository
import com.example.ezwordmaster.data.local.database.EzWordMasterDatabase
import com.example.ezwordmaster.data.local.repository.SettingsRepositoryImpl
import com.example.ezwordmaster.data.local.repository.StudyResultRepositoryImpl
import com.example.ezwordmaster.data.local.repository.TopicRepositoryImpl
import com.example.ezwordmaster.data.remote.DictionaryApi
import com.example.ezwordmaster.data.repository.AuthRepositoryImpl
import com.example.ezwordmaster.data.repository.NotificationRepositoryImpl
import com.example.ezwordmaster.data.repository.TranslationRepositoryImpl
import com.example.ezwordmaster.data.repository.UserRepositoryImpl
import com.example.ezwordmaster.domain.repository.IAuthRepository
import com.example.ezwordmaster.domain.repository.ICloudStudyResultRepository
import com.example.ezwordmaster.domain.repository.ICloudTopicRepository
import com.example.ezwordmaster.domain.repository.INotificationRepository
import com.example.ezwordmaster.domain.repository.ISettingsRepository
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.domain.repository.ITranslationRepository
import com.example.ezwordmaster.domain.repository.IUserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Một "Thùng chứa Phụ thuộc" đơn giản để quản lý việc tạo và cung cấp các Repository.
 */
class AppContainer(private val context: Context) {

    /** Repository Room quản lý chủ đề. */
    val topicRepository: ITopicRepository by lazy {
        TopicRepositoryImpl(context)
    }

    /** Repository Room ghi nhận lịch sử ôn tập. */
    val studyResultRepository: IStudyResultRepository by lazy {
        StudyResultRepositoryImpl(context)
    }

    /** Repository DataStore cho phần cài đặt. */
    val settingsRepository: ISettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    /** Firebase Auth repository. */
    val authRepository: IAuthRepository by lazy {
        AuthRepositoryImpl()
    }

    /** Firestore repository lưu thông tin user. */
    val userRepository: IUserRepository by lazy {
        UserRepositoryImpl()
    }


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

    /**
     * Tạo một instance của CloudTopicRepository cho một user cụ thể
     */
    fun createCloudTopicRepository(userId: String): ICloudTopicRepository {
        return CloudTopicRepository(userId)
    }

    /**
     * Tạo một instance của CloudStudyResultRepository cho một user cụ thể
     */
    fun createCloudStudyResultRepository(userId: String): ICloudStudyResultRepository {
        return CloudStudyResultRepository(userId)
    }


}