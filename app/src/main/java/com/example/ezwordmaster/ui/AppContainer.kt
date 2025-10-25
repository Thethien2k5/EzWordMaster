package com.example.ezwordmaster.ui

import android.content.Context
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.data.repository.TopicRepositoryImpl
import  com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.data.repository.StudyResultRepositoryImpl

/**
 * Một "Thùng chứa Phụ thuộc" đơn giản để quản lý việc tạo và cung cấp các Repository.
 */
class AppContainer(private val context: Context) {

    // Tạo TopicRepository
    val TOPICREPOSITORY: ITopicRepository by lazy {
        TopicRepositoryImpl(context)
    }

    // Tạo StudyResultRepository
    val STUDYRESULTREPOSITORY: IStudyResultRepository by lazy {
        StudyResultRepositoryImpl(context)
    }

}