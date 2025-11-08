package com.example.ezwordmaster.data.repository

import com.example.ezwordmaster.data.local.dao.TranslationHistoryDao
import com.example.ezwordmaster.data.local.entity.TranslationHistoryEntity
import com.example.ezwordmaster.data.remote.DictionaryApi
import com.example.ezwordmaster.domain.repository.ITranslationRepository
import com.example.ezwordmaster.model.DetailedTranslationResult
import com.example.ezwordmaster.model.DictionaryResponse
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class TranslationRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi, // CHÚNG TA VẪN GIỮ API NÀY
    private val translationHistoryDao: TranslationHistoryDao
) : ITranslationRepository {

    private var enViTranslator: Translator? = null
    private var viEnTranslator: Translator? = null

    // Hàm khởi tạo máy dịch (sẽ được gọi khi cần)
    private fun getTranslator(sourceLang: String, targetLang: String): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(if (sourceLang == "en") TranslateLanguage.ENGLISH else TranslateLanguage.VIETNAMESE)
            .setTargetLanguage(if (targetLang == "vi") TranslateLanguage.VIETNAMESE else TranslateLanguage.ENGLISH)
            .build()

        return if (sourceLang == "en") {
            if (enViTranslator == null) {
                enViTranslator = Translation.getClient(options)
            }
            enViTranslator!!
        } else {
            if (viEnTranslator == null) {
                viEnTranslator = Translation.getClient(options)
            }
            viEnTranslator!!
        }
    }

    override suspend fun translateText(
        text: String,
        sourceLang: String,
        targetLang: String
    ): DetailedTranslationResult {
        val cleanText = text.trim().lowercase()
        if (cleanText.isEmpty()) {
            return DetailedTranslationResult(
                translatedText = "Vui lòng nhập từ cần tra",
                sourceLanguage = sourceLang,
                targetLanguage = targetLang,
                error = "Từ không được để trống"
            )
        }

        println("🔄 Bắt đầu dịch (ML Kit): '$cleanText'")

        // BƯỚC 1: DỊCH BẰNG ML KIT (OFFLINE, 100% ỔN ĐỊNH)
        val mainTranslation: String
        try {
            val translator = getTranslator(sourceLang, targetLang)

            // Tải model nếu cần
            val conditions = DownloadConditions.Builder().requireWifi().build()
            suspendCancellableCoroutine { continuation ->
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        println("✅ Model dịch đã sẵn sàng.")
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        println("❌ Lỗi tải model dịch: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }

            // Dịch văn bản
            mainTranslation = suspendCancellableCoroutine { continuation ->
                translator.translate(cleanText)
                    .addOnSuccessListener { translatedText ->
                        println("✅ Dịch ML Kit '$cleanText' -> '$translatedText'")
                        continuation.resume(translatedText)
                    }
                    .addOnFailureListener { exception ->
                        println("❌ Lỗi dịch ML Kit: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }

        } catch (e: Exception) {
            println("❌ Lỗi nghiêm trọng với ML Kit: ${e.message}")
            return DetailedTranslationResult(
                translatedText = "Lỗi dịch (ML Kit)",
                sourceLanguage = sourceLang,
                targetLanguage = targetLang,
                error = "Lỗi dịch offline: ${e.message}"
            )
        }

        // ĐÃ DỊCH XONG. Bắt đầu tạo kết quả
        var result = DetailedTranslationResult(
            translatedText = mainTranslation,
            sourceLanguage = sourceLang,
            targetLanguage = targetLang
            // Các trường khác (phonetic, example...) lúc này đang rỗng
        )

        // BƯỚC 2: (TÙY CHỌN) LẤY THÊM CHI TIẾT BẰNG DICTIONARY API
        var dictResponse: DictionaryResponse? = null
        if (!cleanText.contains(" ") && sourceLang == "en") {
            try {
                // Thử gọi API online
                val responses = dictionaryApi.getWordDefinition(cleanText)
                if (responses.isNotEmpty()) {
                    dictResponse = responses.first()
                    println("✅ Tìm thấy chi tiết từ điển cho: ${dictResponse?.word}")
                } else {
                    println("ℹ️ Không tìm thấy chi tiết từ điển cho: '$cleanText'")
                }
            } catch (e: Exception) {
                // Lỗi API này không nghiêm trọng, app vẫn chạy tiếp
                println("⚠️ Lỗi Dictionary API (Không nghiêm trọng, vẫn có bản dịch): ${e.message}")
            }
        }

        // BƯỚC 3: CẬP NHẬT KẾT QUẢ VỚI DỮ LIỆU TÙY CHỌN (NẾU CÓ)
        if (dictResponse != null) {
            val firstMeaning = dictResponse.meanings?.firstOrNull()
            val firstDefinition = firstMeaning?.definitions?.firstOrNull()
            val phoneticText = dictResponse.phonetic ?: dictResponse.phonetics
                ?.firstOrNull { it.text != null && it.text.isNotBlank() }?.text ?: ""

            // Cập nhật đối tượng result
            result = result.copy(
                englishDefinition = firstDefinition?.definition ?: "",
                phonetic = phoneticText,
                partOfSpeech = firstMeaning?.partOfSpeech ?: "",
                example = firstDefinition?.example ?: "",
                synonyms = firstMeaning?.synonyms ?: emptyList(),
                antonyms = firstMeaning?.antonyms ?: emptyList()
            )
        }

        // BƯỚC 4: LƯU VÀO LỊCH SỬ
        saveTranslationHistory(cleanText, result)
        return result
    }

    private suspend fun saveTranslationHistory(
        originalText: String,
        result: DetailedTranslationResult
    ) {
        try {
            val translationHistory = TranslationHistoryEntity(
                id = UUID.randomUUID().toString(),
                originalText = originalText,
                translatedText = result.translatedText,
                sourceLanguage = result.sourceLanguage,
                targetLanguage = result.targetLanguage,
                phonetic = result.phonetic,
                partOfSpeech = result.partOfSpeech,
                example = result.example,
                synonyms = result.synonyms,
                antonyms = result.antonyms,
                timestamp = Date()
            )

            translationHistoryDao.insertTranslation(translationHistory)
            println("💾 Đã lưu vào lịch sử: $originalText")
        } catch (e: Exception) {
            println("❌ Lỗi lưu lịch sử: ${e.message}")
        }
    }

    // --- CÁC HÀM QUẢN LÝ LỊCH SỬ (GIỮ NGUYÊN) ---

    override fun getAllTranslationHistory(): Flow<List<TranslationHistoryEntity>> {
        return translationHistoryDao.getAllTranslationHistory()
    }

    override fun searchTranslationHistory(query: String): Flow<List<TranslationHistoryEntity>> {
        return translationHistoryDao.searchTranslationHistory("%$query%")
    }

    override suspend fun insertTranslation(translation: TranslationHistoryEntity) {
        translationHistoryDao.insertTranslation(translation)
    }

    override suspend fun deleteTranslation(translation: TranslationHistoryEntity) {
        translationHistoryDao.deleteTranslation(translation)
    }

    override suspend fun deleteTranslationById(id: String) {
        translationHistoryDao.deleteTranslationById(id)
    }

    override suspend fun deleteAllTranslationHistory() {
        translationHistoryDao.deleteAllTranslationHistory()
    }

    override suspend fun findTranslationByText(text: String): TranslationHistoryEntity? {
        return translationHistoryDao.findTranslationByText(text)
    }

    // Đảm bảo bạn dọn dẹp translator khi ViewModel bị hủy
    // Bạn có thể thêm một hàm trong ITranslationRepository để gọi từ ViewModel
    override fun cleanup() {
        enViTranslator?.close()
        viEnTranslator?.close()
        enViTranslator = null
        viEnTranslator = null
        println("🧼 Đã dọn dẹp Translators")
    }
}