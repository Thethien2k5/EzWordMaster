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
import com.example.ezwordmaster.model.FlipCardUiState

/**
 * Lớp dữ liệu chứa toàn bộ trạng thái cần thiết để hiển thị màn hình Lật thẻ.
 */


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

            // Phân tích từ từ chuỗi JSON
            val WORDS = wordsJson.split(",").mapNotNull { pair ->
                val PARTS = pair.split(":")
                if (PARTS.size == 2) Word(word = PARTS[0], meaning = PARTS[1]) else null
            }

            // Tạo các cặp thẻ
            val CARD_ITEMS = mutableListOf<CardItem>()
            WORDS.forEachIndexed { index, word ->
                val PAIR_ID = "pair_$index"
                CARD_ITEMS.add(CardItem(id = "word_$index", text = word.word?:"Lỗi không word FlipCardModel", isWord = true, pairId = PAIR_ID))
                CARD_ITEMS.add(CardItem(id = "meaning_$index", text = word.meaning?:"Lỗi không nghĩa FlipCardModel", isWord = false, pairId = PAIR_ID))
            }

            // Cập nhật trạng thái ban đầu
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
            if (card.isMatched || CURRENT_STATE.FLIPPED_CARDS.any { it.id == card.id } || CURRENT_STATE.FLIPPED_CARDS.size == 2) {
                return@launch
            }

            val NEW_FLIPPED_CARDS = CURRENT_STATE.FLIPPED_CARDS + card
            _UI_STATE.value = CURRENT_STATE.copy(FLIPPED_CARDS = NEW_FLIPPED_CARDS)

            if (NEW_FLIPPED_CARDS.size == 2) {
                checkForMatch(NEW_FLIPPED_CARDS)
            }
        }
    }

    private fun checkForMatch(flipped: List<CardItem>) {
        viewModelScope.launch {
            val CARD_1 = flipped[0]
            val CARD_2 = flipped[1]
            val IS_MATCH = (CARD_1.isWord != CARD_2.isWord) && (CARD_1.pairId == CARD_2.pairId)

            if (IS_MATCH) {
                val NEW_MATCHED_PAIRS = _UI_STATE.value.MATCHED_PAIRS + 1
                _UI_STATE.value = _UI_STATE.value.copy(CORRECT_PAIR_IDS = setOf(CARD_1.pairId))
                delay(1000)

                val UPDATED_CARDS = _UI_STATE.value.CARDS.map {
                    if (it.pairId == CARD_1.pairId) it.copy(isMatched = true) else it
                }

                _UI_STATE.value = _UI_STATE.value.copy(
                    CARDS = UPDATED_CARDS,
                    FLIPPED_CARDS = emptyList(),
                    MATCHED_PAIRS = NEW_MATCHED_PAIRS,
                    CORRECT_PAIR_IDS = emptySet()
                )

                if (NEW_MATCHED_PAIRS >= _UI_STATE.value.CARDS.size / 2) {
                    completeGame()
                }
            } else {
                _UI_STATE.value = _UI_STATE.value.copy(WRONG_PAIR_IDS = setOf(CARD_1.pairId, CARD_2.pairId))
                delay(1500)
                _UI_STATE.value = _UI_STATE.value.copy(FLIPPED_CARDS = emptyList(), WRONG_PAIR_IDS = emptySet())
            }
        }
    }

    private fun completeGame() {
        val STATE = _UI_STATE.value
        val TOPIC = STATE.TOPIC ?: return

        val STUDY_RESULT = StudyResult.createFlipCardResult(
            id = UUID.randomUUID().toString(),
            topicId = TOPIC.id ?: "unknown_id",
            topicName = TOPIC.name ?: "Chủ đề không tên",
            startTime = STATE.START_TIME,
            endTime = System.currentTimeMillis(),
            totalPairs = STATE.CARDS.size / 2,
            matchedPairs = STATE.MATCHED_PAIRS,
            playTime = (System.currentTimeMillis() - STATE.START_TIME) / 1000
        )
        STUDY_RESULT_REPOSITORY.addStudyResult(STUDY_RESULT)
        _UI_STATE.value = _UI_STATE.value.copy(IS_COMPLETED = true)
    }
}