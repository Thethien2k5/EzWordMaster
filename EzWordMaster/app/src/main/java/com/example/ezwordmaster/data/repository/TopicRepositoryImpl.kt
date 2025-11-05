package com.example.ezwordmaster.data.repository

import android.content.Context
import android.util.Log
import com.example.ezwordmaster.data.local.dao.TopicDao
import com.example.ezwordmaster.data.local.dao.WordDao
import com.example.ezwordmaster.data.local.database.EzWordMasterDatabase
import com.example.ezwordmaster.data.local.entity.TopicEntity
import com.example.ezwordmaster.data.local.mapper.TopicMapper
import com.example.ezwordmaster.domain.repository.ITopicRepository
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word
import java.io.File

class TopicRepositoryImpl(private val context: Context) : ITopicRepository {

    private val database = EzWordMasterDatabase.getDatabase(context)
    private val topicDao: TopicDao = database.topicDao()
    private val wordDao: WordDao = database.wordDao()

    private val FILE_NAME = "topics.json" // Để check file cũ nếu có

    // Kiểm tra file JSON cũ có tồn tại không (để migration)
    override suspend fun isTopicsFileExists(): Boolean {
        val file = File(context.filesDir, FILE_NAME)
        return file.exists()
    }

    // Đọc dữ liệu từ Room Database
    override suspend fun loadTopics(): List<Topic> {
        createTopicsFileIfMissing()

        val topicEntities = topicDao.getAllTopicsSync()
        val topics = mutableListOf<Topic>()

        for (topicEntity in topicEntities) {
            val words = wordDao.getWordsByTopicIdSync(topicEntity.id)
            val topic = TopicMapper.toDomain(topicEntity, words)
            topics.add(topic)
        }

        return topics
    }

    // Tạo dữ liệu mặc định nếu chưa có
    override suspend fun createTopicsFileIfMissing() {
        val topicCount = topicDao.getTopicCount()

        if (topicCount == 0) {
            // Kiểm tra xem có file JSON cũ không, nếu có thì migrate
            val jsonFile = File(context.filesDir, FILE_NAME)
            if (jsonFile.exists()) {
                migrateFromJson(jsonFile)
            } else {
                // Tạo dữ liệu mặc định
                createDefaultTopics()
            }
        }
    }

    // Migrate từ JSON cũ sang Room
    private suspend fun migrateFromJson(jsonFile: File) {
        try {
            Log.d("TopicRepo", "Bắt đầu migration từ JSON sang Room")
            val jsonString = jsonFile.readText()
            val json = kotlinx.serialization.json.Json { prettyPrint = true }
            val topics: List<Topic> = json.decodeFromString(jsonString)

            for (topic in topics) {
                val topicEntity = TopicMapper.toEntity(topic)
                topicDao.insertTopic(topicEntity)

                if (topic.words.isNotEmpty()) {
                    val wordEntities = topic.words.map { word ->
                        TopicMapper.wordToEntity(word, topic.id ?: "")
                    }
                    wordDao.insertWords(wordEntities)
                }
            }

            Log.d("TopicRepo", "Đã migrate ${topics.size} topics từ JSON sang Room")

            // Optionally: Backup file cũ hoặc xóa
            // jsonFile.delete()
        } catch (e: Exception) {
            Log.e("TopicRepo", "Lỗi migration từ JSON: ${e.message}")
            // Nếu lỗi, tạo dữ liệu mặc định
            createDefaultTopics()
        }
    }

    // Tạo dữ liệu mặc định
    private suspend fun createDefaultTopics() {
        val defaultTopic = Topic(
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

        val topicEntity = TopicMapper.toEntity(defaultTopic)
        topicDao.insertTopic(topicEntity)

        val wordEntities = defaultTopic.words.map { word ->
            TopicMapper.wordToEntity(word, defaultTopic.id ?: "")
        }
        wordDao.insertWords(wordEntities)

        Log.d("TopicRepo", "Đã tạo dữ liệu mặc định trong Room")
    }

    // Tạo ID mới cho topic
    override suspend fun generateNewTopicId(): String {
        topicDao.getMaxTopicId() ?: 0
        val allTopics = loadTopics()
        val existingIds = allTopics.mapNotNull { it.id?.toIntOrNull() }.sorted()

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

    // Thêm hoặc cập nhật một topic
    override suspend fun addOrUpdateTopic(newTopic: Topic) {
        val existingTopic = if (newTopic.id != null) {
            topicDao.getTopicById(newTopic.id)
        } else {
            topicDao.getTopicByName(newTopic.name ?: "")
        }

        if (existingTopic == null) {
            // Thêm mới
            val topicEntity = TopicMapper.toEntity(newTopic)
            topicDao.insertTopic(topicEntity)

            // Xóa words cũ nếu có, rồi thêm words mới
            wordDao.deleteWordsByTopicId(topicEntity.id)
            if (newTopic.words.isNotEmpty()) {
                val wordEntities = newTopic.words.map { word ->
                    TopicMapper.wordToEntity(word, topicEntity.id)
                }
                wordDao.insertWords(wordEntities)
            }

            Log.d("TopicRepo", "Đã thêm chủ đề mới: ${newTopic.name}")
        } else {
            // Kiểm tra có giống hệt không
            val existingWords = wordDao.getWordsByTopicIdSync(existingTopic.id)
            val existingDomain = TopicMapper.toDomain(existingTopic, existingWords)

            val sameWords = existingDomain.words.size == newTopic.words.size &&
                    existingDomain.words.containsAll(newTopic.words)

            if (sameWords) {
                Log.d("TopicRepo", "Chủ đề '${newTopic.name}' đã tồn tại và giống hệt, bỏ qua.")
                return
            } else {
                // Cập nhật
                val topicEntity = TopicMapper.toEntity(newTopic.copy(id = existingTopic.id))
                topicDao.updateTopic(topicEntity)

                // Xóa words cũ và thêm words mới
                wordDao.deleteWordsByTopicId(topicEntity.id)
                if (newTopic.words.isNotEmpty()) {
                    val wordEntities = newTopic.words.map { word ->
                        TopicMapper.wordToEntity(word, topicEntity.id)
                    }
                    wordDao.insertWords(wordEntities)
                }

                Log.d("TopicRepo", "Đã cập nhật chủ đề '${newTopic.name}'")
            }
        }
    }

    // Thêm từ vào chủ đề
    override suspend fun addWordToTopic(topicId: String, word: Word) {
        if (wordExistsInTopic(topicId, word)) {
            Log.d(
                "TopicRepo",
                "Từ '${word.word}' đã tồn tại trong chủ đề. Thao tác thêm mới bị hủy."
            )
            return
        }

        val topicEntity = topicDao.getTopicById(topicId)
        if (topicEntity != null) {
            val wordEntity = TopicMapper.wordToEntity(word, topicId)
            wordDao.insertWord(wordEntity)
            Log.d("TopicRepo", "➕ Đã thêm từ '${word.word}' vào chủ đề")
        }
    }

    // Thêm tên chủ đề mới
    override suspend fun addNameTopic(newName: String) {
        if (topicNameExists(newName)) {
            Log.d("TopicRepo", "Tên chủ đề '$newName' đã tồn tại. Thao tác thêm mới bị hủy.")
            return
        }

        val newId = generateNewTopicId()
        val topicEntity = TopicEntity(id = newId, name = newName)
        topicDao.insertTopic(topicEntity)

        Log.d("TopicRepo", "🆕 Đã thêm chủ đề mới: id=$newId, name=$newName")
    }

    // Xóa một topic theo id
    override suspend fun deleteTopicById(id: String) {
        // Room sẽ tự động xóa words nhờ CASCADE
        topicDao.deleteTopicById(id)
        Log.d("TopicRepo", "🗑 Đã xóa chủ đề có id=$id")
    }

    // Xóa từ khỏi chủ đề
    override suspend fun deleteWordFromTopic(topicId: String, word: Word) {
        wordDao.deleteWordFromTopic(topicId, word.word, word.meaning)
        Log.d("TopicRepo", "🗑️ Đã xóa từ '${word.word}' khỏi chủ đề")
    }

    // Cập nhật tên chủ đề
    override suspend fun updateTopicName(id: String, newName: String) {
        topicDao.updateTopicName(id, newName)
        Log.d("TopicRepo", "✏️ Đã cập nhật tên chủ đề: $newName")
    }

    // Cập nhật từ trong chủ đề
    override suspend fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word) {
        val existingWordEntity = wordDao.getWordByTopicAndContent(
            topicId,
            oldWord.word,
            oldWord.meaning
        )

        if (existingWordEntity != null) {
            val updatedWordEntity = existingWordEntity.copy(
                word = newWord.word,
                meaning = newWord.meaning,
                example = newWord.example
            )
            wordDao.updateWord(updatedWordEntity)
            Log.d("TopicRepo", "✏️ Đã cập nhật từ '${newWord.word}'")
        }
    }

    // Lấy một topic theo ID
    override suspend fun getTopicById(id: String): Topic? {
        val topicEntity = topicDao.getTopicById(id) ?: return null
        val words = wordDao.getWordsByTopicIdSync(id)
        return TopicMapper.toDomain(topicEntity, words)
    }

    // Kiểm tra tên chủ đề đã tồn tại chưa
    override suspend fun topicNameExists(name: String): Boolean {
        val existing = topicDao.getTopicByName(name)
        return existing != null
    }

    // Kiểm tra từ đã tồn tại trong chủ đề chưa
    override suspend fun wordExistsInTopic(topicId: String, word: Word): Boolean {
        val existing = wordDao.getWordByTopicAndContent(
            topicId,
            word.word,
            word.meaning
        )
        return existing != null
    }
}
