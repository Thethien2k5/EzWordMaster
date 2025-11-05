package com.example.ezwordmaster.ui

import android.content.Context
import com.example.ezwordmaster.data.repository.SettingsRepositoryImpl
import com.example.ezwordmaster.data.repository.StudyResultRepositoryImpl
import com.example.ezwordmaster.data.repository.TopicRepositoryImpl
import com.example.ezwordmaster.domain.repository.ISettingsRepository
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository

/**
 * Một "Thùng chứa Phụ thuộc" đơn giản để quản lý việc tạo và cung cấp các Repository.
 */
class AppContainer(private val context: Context) {

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
}