package com.example.ezwordmaster.ui.screens.regime.entertainment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.CardItem
import com.example.ezwordmaster.model.FlipCardUiState
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * ViewModel quản lý toàn bộ logic và trạng thái cho trò chơi Lật thẻ (FlipCard).
 */
class FlipCardViewModel(
    private val topicRepository: ITopicRepository,
    private val studyResultRepository: IStudyResultRepository
) : ViewModel() {

    private val _UISTATE = MutableStateFlow(FlipCardUiState())
    val uiState: StateFlow<FlipCardUiState> = _UISTATE.asStateFlow()

    /**
     * Thiết lập và bắt đầu trò chơi từ topicId và chuỗi wordsJson.
     */
    fun setupGame(topicId: String, wordsJson: String) {
        viewModelScope.launch {
            val topic = topicRepository.getTopicById(topicId)

            val words = wordsJson.split(",").mapNotNull { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) Word(word = parts[0], meaning = parts[1]) else null
            }

            val cardItems = mutableListOf<CardItem>()
            words.forEachIndexed { index, word ->
                val pairId = "pair_$index"
                cardItems.add(
                    CardItem(
                        id = "word_$index",
                        text = word.word ?: "",
                        isWord = true,
                        pairId = pairId
                    )
                )
                cardItems.add(
                    CardItem(
                        id = "meaning_$index",
                        text = word.meaning ?: "",
                        isWord = false,
                        pairId = pairId
                    )
                )
            }

            _UISTATE.value = FlipCardUiState(
                topic = topic,
                cards = cardItems.shuffled(),
                startTime = System.currentTimeMillis()
            )
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn vào một thẻ.
     */
    fun onCardClicked(card: CardItem) {
        viewModelScope.launch {
            val currentState = _UISTATE.value
            if (card.isMatched || currentState.flippedCards.any { it.id == card.id } || currentState.flippedCards.size >= 2 || currentState.isProcessing) {
                return@launch
            }

            val newFlippedCards = currentState.flippedCards + card
            _UISTATE.value = currentState.copy(flippedCards = newFlippedCards)

            if (newFlippedCards.size == 2) {
                _UISTATE.value = _UISTATE.value.copy(isProcessing = true)
                checkForMatch(newFlippedCards)
            }
        }
    }

    /**
     * Helper function để tạo hiệu ứng nhấp nháy cho các thẻ.
     */
    private suspend fun flashEffect(cardIds: Set<String>, isCorrect: Boolean) {
        repeat(2) { // Lặp lại 2 lần để tạo hiệu ứng nháy
            if (isCorrect) {
                _UISTATE.value = _UISTATE.value.copy(correctCardIds = cardIds)
            } else {
                _UISTATE.value = _UISTATE.value.copy(wrongCardIds = cardIds)
            }
            delay(250)
            if (isCorrect) {
                _UISTATE.value = _UISTATE.value.copy(correctCardIds = emptySet())
            } else {
                _UISTATE.value = _UISTATE.value.copy(wrongCardIds = emptySet())
            }
            delay(250)
        }
    }

    private fun checkForMatch(flipped: List<CardItem>) {
        viewModelScope.launch {
            try {
                val card1 = flipped[0]
                val card2 = flipped[1]
                val isMatch = (card1.isWord != card2.isWord) && (card1.pairId == card2.pairId)
                val cardIds = setOf(card1.id, card2.id)

                // Chạy hiệu ứng nhấp nháy
                flashEffect(cardIds, isMatch)

                if (isMatch) {
                    val newMatchedpairs = _UISTATE.value.matchedPairs + 1

                    // Cập nhật thẻ thành đã khớp (để chúng biến mất)
                    val updatedCards = _UISTATE.value.cards.map {
                        if (it.pairId == card1.pairId) it.copy(isMatched = true) else it
                    }
                    _UISTATE.value = _UISTATE.value.copy(
                        cards = updatedCards,
                        flippedCards = emptyList(),
                        matchedPairs = newMatchedpairs
                    )

                    if (newMatchedpairs >= _UISTATE.value.cards.size / 2) {
                        completeGame()
                    }
                } else {
                    // Nếu sai, chỉ cần lật úp thẻ lại sau một khoảng trễ
                    delay(200)
                    _UISTATE.value = _UISTATE.value.copy(flippedCards = emptyList())
                }
            } finally {
                // Mở khóa tương tác sau khi mọi thứ hoàn tất
                _UISTATE.value = _UISTATE.value.copy(isProcessing = false)
            }
        }
    }

    private fun completeGame() {
        val state = _UISTATE.value
        if (state.isCompleted) return // Tránh gọi nhiều lần

        val topic = state.topic ?: return
        val duration = System.currentTimeMillis() - state.startTime
        val playTime = duration / 1000

        val study_result = StudyResult.createFlipCardResult(
            id = UUID.randomUUID().toString(),
            topicId = topic.id ?: "unknown_id",
            topicName = topic.name ?: "Chủ đề không tên",
            duration = duration,
            totalPairs = state.cards.size / 2,
            matchedPairs = state.matchedPairs,
            playTime = playTime
        )
        studyResultRepository.addStudyResult(study_result)
        _UISTATE.value = _UISTATE.value.copy(isCompleted = true)
    }
}