package com.example.ezwordmaster.ui.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezwordmaster.domain.repository.IStudyResultRepository
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.CardItem
import com.example.ezwordmaster.model.StudyResult
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Lớp dữ liệu (data class) chứa toàn bộ trạng thái cần thiết để hiển thị màn hình Lật thẻ.
 */
data class FlipCardUiState(
    val TOPIC: Topic? = null,
    val CARDS: List<CardItem> = emptyList(),
    val FLIPPED_CARDS: List<CardItem> = emptyList(), // Tối đa 2 thẻ đang được lật
    val MATCHED_PAIRS: Int = 0,
    val IS_COMPLETED: Boolean = false,
    val WRONG_CARD_IDS: Set<String> = emptySet(), // Lưu ID của các thẻ lật sai để hiển thị hiệu ứng
    val CORRECT_CARD_IDS: Set<String> = emptySet(), // Lưu ID của các thẻ lật đúng để hiển thị hiệu ứng
    val IS_PROCESSING: Boolean = false, // Cờ chống nhấn nhanh khi đang xử lý animation
    val START_TIME: Long = 0L
)

/**
 * ViewModel quản lý toàn bộ logic và trạng thái cho trò chơi Lật thẻ (FlipCard).
 */
class FlipCardViewModel(
    private val TOPIC_REPOSITORY: ITopicRepository,
    private val STUDY_RESULT_REPOSITORY: IStudyResultRepository
) : ViewModel() {

    private val _UI_STATE = MutableStateFlow(FlipCardUiState())
    val UI_STATE: StateFlow<FlipCardUiState> = _UI_STATE.asStateFlow()

    /**
     * Thiết lập và bắt đầu trò chơi từ topicId và chuỗi wordsJson.
     */
    fun setupGame(topicId: String, wordsJson: String) {
        viewModelScope.launch {
            val TOPIC = TOPIC_REPOSITORY.getTopicById(topicId)

            val WORDS = wordsJson.split(",").mapNotNull { pair ->
                val PARTS = pair.split(":")
                if (PARTS.size == 2) Word(word = PARTS[0], meaning = PARTS[1]) else null
            }

            val CARD_ITEMS = mutableListOf<CardItem>()
            WORDS.forEachIndexed { index, word ->
                val PAIR_ID = "pair_$index"
                CARD_ITEMS.add(
                    CardItem(
                        id = "word_$index",
                        text = word.word ?: "",
                        isWord = true,
                        pairId = PAIR_ID
                    )
                )
                CARD_ITEMS.add(
                    CardItem(
                        id = "meaning_$index",
                        text = word.meaning ?: "",
                        isWord = false,
                        pairId = PAIR_ID
                    )
                )
            }

            _UI_STATE.value = FlipCardUiState(
                TOPIC = TOPIC,
                CARDS = CARD_ITEMS.shuffled(),
                START_TIME = System.currentTimeMillis()
            )
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn vào một thẻ.
     */
    fun onCardClicked(card: CardItem) {
        viewModelScope.launch {
            val CURRENT_STATE = _UI_STATE.value
            if (card.isMatched || CURRENT_STATE.FLIPPED_CARDS.any { it.id == card.id } || CURRENT_STATE.FLIPPED_CARDS.size >= 2 || CURRENT_STATE.IS_PROCESSING) {
                return@launch
            }

            val NEW_FLIPPED_CARDS = CURRENT_STATE.FLIPPED_CARDS + card
            _UI_STATE.value = CURRENT_STATE.copy(FLIPPED_CARDS = NEW_FLIPPED_CARDS)

            if (NEW_FLIPPED_CARDS.size == 2) {
                _UI_STATE.value = _UI_STATE.value.copy(IS_PROCESSING = true)
                checkForMatch(NEW_FLIPPED_CARDS)
            }
        }
    }

    /**
     * Helper function để tạo hiệu ứng nhấp nháy cho các thẻ.
     */
    private suspend fun flashEffect(cardIds: Set<String>, isCorrect: Boolean) {
        repeat(2) { // Lặp lại 2 lần để tạo hiệu ứng nháy
            if (isCorrect) {
                _UI_STATE.value = _UI_STATE.value.copy(CORRECT_CARD_IDS = cardIds)
            } else {
                _UI_STATE.value = _UI_STATE.value.copy(WRONG_CARD_IDS = cardIds)
            }
            delay(250)
            if (isCorrect) {
                _UI_STATE.value = _UI_STATE.value.copy(CORRECT_CARD_IDS = emptySet())
            } else {
                _UI_STATE.value = _UI_STATE.value.copy(WRONG_CARD_IDS = emptySet())
            }
            delay(250)
        }
    }

    private fun checkForMatch(flipped: List<CardItem>) {
        viewModelScope.launch {
            try {
                val CARD_1 = flipped[0]
                val CARD_2 = flipped[1]
                val IS_MATCH = (CARD_1.isWord != CARD_2.isWord) && (CARD_1.pairId == CARD_2.pairId)
                val CARD_IDS = setOf(CARD_1.id, CARD_2.id)

                // Chạy hiệu ứng nhấp nháy
                flashEffect(CARD_IDS, IS_MATCH)

                if (IS_MATCH) {
                    val NEW_MATCHED_PAIRS = _UI_STATE.value.MATCHED_PAIRS + 1

                    // Cập nhật thẻ thành đã khớp (để chúng biến mất)
                    val UPDATED_CARDS = _UI_STATE.value.CARDS.map {
                        if (it.pairId == CARD_1.pairId) it.copy(isMatched = true) else it
                    }
                    _UI_STATE.value = _UI_STATE.value.copy(
                        CARDS = UPDATED_CARDS,
                        FLIPPED_CARDS = emptyList(),
                        MATCHED_PAIRS = NEW_MATCHED_PAIRS
                    )

                    if (NEW_MATCHED_PAIRS >= _UI_STATE.value.CARDS.size / 2) {
                        completeGame()
                    }
                } else {
                    // Nếu sai, chỉ cần lật úp thẻ lại sau một khoảng trễ
                    delay(200)
                    _UI_STATE.value = _UI_STATE.value.copy(FLIPPED_CARDS = emptyList())
                }
            } finally {
                // Mở khóa tương tác sau khi mọi thứ hoàn tất
                _UI_STATE.value = _UI_STATE.value.copy(IS_PROCESSING = false)
            }
        }
    }

    private fun completeGame() {
        val STATE = _UI_STATE.value
        if (STATE.IS_COMPLETED) return // Tránh gọi nhiều lần

        val TOPIC = STATE.TOPIC ?: return
        val DURATION = System.currentTimeMillis() - STATE.START_TIME
        val PLAY_TIME = DURATION / 1000

        val STUDY_RESULT = StudyResult.createFlipCardResult(
            id = UUID.randomUUID().toString(),
            topicId = TOPIC.id ?: "unknown_id",
            topicName = TOPIC.name ?: "Chủ đề không tên",
            duration = DURATION,
            totalPairs = STATE.CARDS.size / 2,
            matchedPairs = STATE.MATCHED_PAIRS,
            playTime = PLAY_TIME
        )
        STUDY_RESULT_REPOSITORY.addStudyResult(STUDY_RESULT)
        _UI_STATE.value = _UI_STATE.value.copy(IS_COMPLETED = true)
    }
}