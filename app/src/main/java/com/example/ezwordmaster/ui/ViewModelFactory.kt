package com.example.ezwordmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ezwordmaster.ui.screens.history.HistoryViewModel
import com.example.ezwordmaster.ui.screens.practice.FlashcardViewModel
import com.example.ezwordmaster.ui.screens.practice.FlipCardViewModel
import com.example.ezwordmaster.ui.screens.practice.PracticeViewModel
import com.example.ezwordmaster.ui.screens.practice.ResultViewModel
import com.example.ezwordmaster.ui.screens.settings.SettingsViewModel
import com.example.ezwordmaster.ui.screens.topic_managment.TopicViewModel

/**
 * Factory này nhận vào cả AppContainer.
 * Dựa vào ViewModel được yêu cầu, nó sẽ lấy Repository tương ứng từ container để tạo ViewModel.
 */
class ViewModelFactory(
    private val CONTAINER: AppContainer
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Nếu được yêu cầu tạo TopicViewModel...
            modelClass.isAssignableFrom(TopicViewModel::class.java) -> {
                // ...nó sẽ lấy topicRepository từ container
                TopicViewModel(CONTAINER.TOPICREPOSITORY) as T
            }

            modelClass.isAssignableFrom(PracticeViewModel::class.java) -> {
                PracticeViewModel(CONTAINER.TOPICREPOSITORY) as T
            }

            modelClass.isAssignableFrom(FlashcardViewModel::class.java) -> {
                FlashcardViewModel(CONTAINER.TOPICREPOSITORY, CONTAINER.STUDYRESULTREPOSITORY) as T
            }

            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(CONTAINER.TOPICREPOSITORY, CONTAINER.STUDYRESULTREPOSITORY) as T
            }

            modelClass.isAssignableFrom(FlipCardViewModel::class.java) -> {
                FlipCardViewModel(CONTAINER.TOPICREPOSITORY, CONTAINER.STUDYRESULTREPOSITORY) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(CONTAINER.STUDYRESULTREPOSITORY) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(CONTAINER.settingsRepository) as T
            }
            // Thêm các ViewModel khác ở đây...
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}