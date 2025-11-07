package com.example.ezwordmaster.ui

import android.content.Context
import com.example.ezwordmaster.data.local.repository.SettingsRepositoryImpl
import com.example.ezwordmaster.data.local.repository.StudyResultRepositoryImpl
import com.example.ezwordmaster.data.local.repository.TopicRepositoryImpl
import com.example.ezwordmaster.data.repository.AuthRepositoryImpl
import com.example.ezwordmaster.data.repository.UserRepositoryImpl
import com.example.ezwordmaster.domain.repository.IAuthRepository
import com.example.ezwordmaster.domain.repository.ISettingsRepository
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.domain.repository.IUserRepository

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
}