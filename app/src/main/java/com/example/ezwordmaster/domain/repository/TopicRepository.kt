package com.example.ezwordmaster.domain.repository

import android.content.Context
import android.util.Log
import com.example.ezwordmaster.domain.model.Topic
import com.example.ezwordmaster.domain.model.Word
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class TopicRepository(private val context: Context) {

    private val FILE_NAME = "topics.json"
    private val json = Json { prettyPrint = true }

    // Đường dẫn tới file topics.json trong thư mục riêng của app
    private fun getTopicsFile(): File = File(context.filesDir, FILE_NAME)

    // Kiểm tra file có tồn tại không
    fun isTopicsFileExists(): Boolean {
        val exists = getTopicsFile().exists()
        Log.d("TopicRepo", "File tồn tại: $exists")
        return exists
    }
    // Đọc dữ liệu từ file
    fun loadTopics(): List<Topic> {
        createTopicsFileIfMissing()
        val file = getTopicsFile()

        return try {
            val jsonString = file.readText()
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            Log.e("TopicRepo", "Lỗi đọc file: ${e.message}")
            emptyList()
        }
    }
    // Ghi đè toàn bộ danh sách (chỉ dùng nội bộ)
    private fun saveTopics(topics: List<Topic>) {
        try {
            val jsonString = json.encodeToString(topics)
            getTopicsFile().writeText(jsonString)
            Log.d("TopicRepo", "Đã lưu ${topics.size} topics vào file.")
        } catch (e: Exception) {
            Log.e("TopicRepo", " Lỗi khi ghi file: ${e.message}")
        }
    }
    //***** ====== TẠO ============ ********
    //  Tạo file mặc định nếu chưa có
    fun createTopicsFileIfMissing() {
        val file = getTopicsFile()
        if (!file.exists()) {
            val defaultTopics = listOf(
                Topic(
                    id = "14",
                    name = "Chào mừng đến với EzWordMaster",
                    words = listOf(
                        Word("Welcome", "Chào mừng"),
                        Word("Friend", "Bạn bè"),
                        Word("Happy", "Hạnh phúc"),
                        Word("Smile", "Nụ cười"),
                        Word("Hello", "Xin chào"),
                        Word("Greeting", "Lời chào"),
                        Word("Warm", "Ấm áp"),
                        Word("Joy", "Niềm vui"),
                        Word("Peace", "Bình yên"),
                        Word("Love", "Yêu thương"),
                        Word("Kind", "Tử tế"),
                        Word("Share", "Chia sẻ"),
                        Word("Together", "Cùng nhau"),
                        Word("Success", "Thành công")
                    )
                )
            )
            saveTopics(defaultTopics)
            Log.d("TopicRepo", "Đã tạo file topics.json mặc định")
        }
    }

    // Tạo ID mới cho topic, tạo id nhỏ ch tồn tại ( lấy đầy khoảng trống id )
    fun generateNewTopicId(): String {
        val topics = loadTopics()
        val existingIds = topics.mapNotNull { it.id?.toIntOrNull() }.sorted()

        var newId = 1
        for (id in existingIds) {
            if (id == newId) {
                newId++
            } else if (id > newId) {
                break
            }
        }
        return newId.toString()
    }

    //******* ========== THÊM =================== **************
    //  Thêm hoặc cập nhật một topic (thông minh)
    fun addOrUpdateTopic(newTopic: Topic) {
        val currentTopics = loadTopics().toMutableList()
        val existing = currentTopics.find {
            it.id == newTopic.id || it.name.equals(newTopic.name, ignoreCase = true)
        }

        if (existing == null) {
            //  Nếu chưa tồn tại → thêm mới
            currentTopics.add(newTopic)
            Log.d("TopicRepo", "Đã thêm chủ đề mới: ${newTopic.name}")
        } else {
            // Kiểm tra danh sách từ có giống hệt không
            val sameWords = existing.words.size == newTopic.words.size &&
                    existing.words.containsAll(newTopic.words)

            if (sameWords) {
                Log.d("TopicRepo", "Chủ đề '${newTopic.name}' đã tồn tại và giống hệt, bỏ qua.")
                return
            } else {
                // Cập nhật chủ đề (thay thế danh sách từ)
                val index = currentTopics.indexOf(existing)
                currentTopics[index] = newTopic
                Log.d("TopicRepo", " Cập nhật chủ đề '${newTopic.name}' với danh sách từ mới.")
            }
        }
        saveTopics(currentTopics)
    }
    // Thêm từ vào chủ đề
    fun addWordToTopic(topicId: String, word: Word) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == topicId }

        if (index != -1) {
            val updatedWords = topics[index].words.toMutableList()
            updatedWords.add(word)
            topics[index] = topics[index].copy(words = updatedWords)
            saveTopics(topics)
            Log.d("TopicRepo", "➕ Đã thêm từ '${word.word}' vào chủ đề")
        }
    }
    //Thêm tên chủ đề mới
    fun addNameTopic(newName: String) {
        val topics = loadTopics().toMutableList()
        val newId = generateNewTopicId()

        val newTopic = Topic(
            id = newId,
            name = newName,
            words = emptyList()
        )

        topics.add(newTopic)
        saveTopics(topics)

        Log.d("TopicRepo", "🆕 Đã thêm chủ đề mới: id=$newId, name=$newName")
    }

    //*** ================= XÓA ===============================
    //  Xóa một topic theo id
    fun deleteTopicById(id: String) {
        val currentTopics = loadTopics().filterNot { it.id == id }
        saveTopics(currentTopics)
        Log.d("TopicRepo", "🗑 Đã xóa chủ đề có id=$id")
    }
    // Xóa từ khỏi chủ đề
    fun deleteWordFromTopic(topicId: String, word: Word) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == topicId }

        if (index != -1) {
            val updatedWords = topics[index].words.toMutableList()
            updatedWords.removeAll { it.word == word.word && it.meaning == word.meaning }
            topics[index] = topics[index].copy(words = updatedWords)
            saveTopics(topics)
            Log.d("TopicRepo", "🗑️ Đã xóa từ '${word.word}' khỏi chủ đề")
        }
    }


    // *** =============== CẬP NHẬT  =========================
    // Cập nhật tên chủ đề
    fun updateTopicName(id: String, newName: String) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == id }

        if (index != -1) {
            topics[index] = topics[index].copy(name = newName)
            saveTopics(topics)
            Log.d("TopicRepo", "✏️ Đã cập nhật tên chủ đề: $newName")
        }
    }

    // Cập nhật từ trong chủ đề
    fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == topicId }

        if (index != -1) {
            val updatedWords = topics[index].words.toMutableList()
            val wordIndex = updatedWords.indexOfFirst {
                it.word == oldWord.word && it.meaning == oldWord.meaning
            }

            if (wordIndex != -1) {
                updatedWords[wordIndex] = newWord
                topics[index] = topics[index].copy(words = updatedWords)
                saveTopics(topics)
                Log.d("TopicRepo", "✏️ Đã cập nhật từ '${newWord.word}'")
            }
        }
    }

    // Lấy một topic theo ID
    fun getTopicById(id: String): Topic? {
        return loadTopics().find { it.id == id }
    }

    // Kiểm tra trùng ID hoặc tên (public dùng cho form thêm chủ đề)
    fun isTopicDuplicate(topic: Topic): Boolean {
        val topics = loadTopics()
        return topics.any {
            it.id == topic.id || it.name.equals(topic.name, ignoreCase = true)
        }
    }
}
