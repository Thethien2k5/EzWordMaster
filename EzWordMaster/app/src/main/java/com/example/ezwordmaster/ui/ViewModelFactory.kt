package com.example.ezwordmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ezwordmaster.ui.screens.history.HistoryViewModel
import com.example.ezwordmaster.ui.screens.regime.PracticeViewModel
import com.example.ezwordmaster.ui.screens.regime.ResultViewModel
import com.example.ezwordmaster.ui.screens.regime.entertainment.FlipCardViewModel
import com.example.ezwordmaster.ui.screens.regime.practice.flash.FlashcardViewModel
import com.example.ezwordmaster.ui.screens.regime.practice.quiz.QuizViewModel
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
                TopicViewModel(CONTAINER.topicRepository) as T
            }

            modelClass.isAssignableFrom(PracticeViewModel::class.java) -> {
                PracticeViewModel(CONTAINER.topicRepository) as T
            }

            modelClass.isAssignableFrom(FlashcardViewModel::class.java) -> {
                FlashcardViewModel(CONTAINER.topicRepository, CONTAINER.studyResultRepository) as T
            }

            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(CONTAINER.topicRepository, CONTAINER.studyResultRepository) as T
            }

            modelClass.isAssignableFrom(FlipCardViewModel::class.java) -> {
                FlipCardViewModel(CONTAINER.topicRepository, CONTAINER.studyResultRepository) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(CONTAINER.studyResultRepository) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(CONTAINER.settingsRepository) as T
            }

            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(CONTAINER.topicRepository, CONTAINER.studyResultRepository) as T
            }
            // Thêm các ViewModel khác ở đây...
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}