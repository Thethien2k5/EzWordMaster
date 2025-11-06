package com.example.ezwordmaster.data.local.repository

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
class TopicRepositoryImpl(private val context: Context) : ITopicRepository {

    // Tên file JSON cũ để kiểm tra và migrate
    private val oldJsonFileName = "topics.json"
    private val json = Json { prettyPrint = true }

    private val database = EzWordMasterDatabase.getDatabase(context)
    private val topicDao: TopicDao = database.topicDao()
    private val wordDao: WordDao = database.wordDao()

    // Luôn sử dụng Dispatchers.IO cho các tác vụ I/O của repository
    private val ioDispatcher = Dispatchers.IO

    // Đường dẫn tới file topics.json trong thư mục riêng của app
    private fun getTopicsFile(): File = File(context.filesDir, oldJsonFileName)

    override suspend fun isTopicsFileExists(): Boolean = withContext(ioDispatcher) {
        val file = File(context.filesDir, oldJsonFileName)
        return@withContext file.exists()
    }

    override suspend fun loadTopics(): List<Topic> = withContext(ioDispatcher) {
        createTopicsFileIfMissing()
        val topicEntities = topicDao.getAllTopicsSync()
        val topics = mutableListOf<Topic>()

        for (topicEntity in topicEntities) {
            val words = wordDao.getWordsByTopicIdSync(topicEntity.id)
            val topic = TopicMapper.toDomain(topicEntity, words)
            topics.add(topic)
        }

        return@withContext topics
    }

    override suspend fun createTopicsFileIfMissing(): Unit = withContext(ioDispatcher) {
        val topicCount = topicDao.getTopicCount()

        if (topicCount == 0) {
            // Kiểm tra xem có file JSON cũ không, nếu có thì migrate
            val jsonFile = File(context.filesDir, oldJsonFileName)
            if (jsonFile.exists()) {
                Log.d("TopicRepo", "Database rỗng, tìm thấy file JSON cũ. Bắt đầu migration...")
                migrateFromJson(jsonFile)
            } else {
                Log.d(
                    "TopicRepo",
                    "Database rỗng và không tìm thấy file JSON cũ. Tạo dữ liệu mặc định..."
                )
                createDefaultTopics()
            }
        } else {
            Log.d("TopicRepo", "Database đã có dữ liệu (${topicCount} topics). Không cần migrate.")
        }
    }

    /**
     * Helper: Di chuyển dữ liệu từ file JSON cũ sang Room DB.
     * Chỉ chạy một lần khi CSDL rỗng và file cũ tồn tại.
     */
    private suspend fun migrateFromJson(jsonFile: File) {
        try {
            val jsonString = jsonFile.readText()
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

        } catch (e: Exception) {
            Log.e("TopicRepo", "Lỗi nghiêm trọng khi migration từ JSON: ${e.message}", e)
            // Nếu lỗi, tạo dữ liệu mặc định
            createDefaultTopics()
        }
    }

    /**
     * Helper: Tạo dữ liệu mặc định
     */
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

    override suspend fun generateNewTopicId(): String = withContext(ioDispatcher) {
        topicDao.getMaxTopicId() ?: 0
        val allTopics = loadTopics()
        val existingIds = allTopics.mapNotNull { it.id?.toIntOrNull() }.sorted()

        var newId = 1
        for (id in existingIds) {
            if (id != newId) break
            newId++
        }
        return@withContext newId.toString()
    }

    override suspend fun addOrUpdateTopic(newTopic: Topic): Unit = withContext(ioDispatcher) {
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
                return@withContext
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

    override suspend fun addWordToTopic(topicId: String, word: Word): Unit =
        withContext(ioDispatcher) {
            if (wordExistsInTopic(topicId, word)) {
                Log.d(
                    "TopicRepo",
                    "Từ '${word.word}' đã tồn tại trong chủ đề. Thao tác thêm mới bị hủy."
                )
                return@withContext
            }

            val topicEntity = topicDao.getTopicById(topicId)
            if (topicEntity != null) {
                val wordEntity = TopicMapper.wordToEntity(word, topicId)
                wordDao.insertWord(wordEntity)
                Log.d("TopicRepo", "➕ Đã thêm từ '${word.word}' vào chủ đề")
            }
        }

    override suspend fun addNameTopic(newName: String): Unit = withContext(ioDispatcher) {
        if (topicNameExists(newName)) {
            Log.d("TopicRepo", "Tên chủ đề '$newName' đã tồn tại. Thao tác thêm mới bị hủy.")
            return@withContext
        }

        val newId = generateNewTopicId()
        val topicEntity = TopicEntity(id = newId, name = newName)
        topicDao.insertTopic(topicEntity)

        Log.d("TopicRepo", "🆕 Đã thêm chủ đề mới: id=$newId, name=$newName")
    }

    override suspend fun deleteTopicById(id: String): Unit = withContext(ioDispatcher) {
        // Room sẽ tự động xóa words nhờ CASCADE
        topicDao.deleteTopicById(id)
        Log.d("TopicRepo", "🗑 Đã xóa chủ đề có id=$id")
    }

    override suspend fun deleteWordFromTopic(topicId: String, word: Word): Unit =
        withContext(ioDispatcher) {
            wordDao.deleteWordFromTopic(topicId, word.word, word.meaning)
            Log.d("TopicRepo", "🗑️ Đã xóa từ '${word.word}' khỏi chủ đề")
        }

    override suspend fun updateTopicName(id: String, newName: String): Unit =
        withContext(ioDispatcher) {
            topicDao.updateTopicName(id, newName)
            Log.d("TopicRepo", "✏️ Đã cập nhật tên chủ đề: $newName")
        }

    override suspend fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word): Unit =
        withContext(ioDispatcher) {
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

    override suspend fun getTopicById(id: String): Topic? = withContext(ioDispatcher) {
        val topicEntity = topicDao.getTopicById(id) ?: return@withContext null
        val words = wordDao.getWordsByTopicIdSync(id)
        return@withContext TopicMapper.toDomain(topicEntity, words)
    }

    override suspend fun topicNameExists(name: String): Boolean = withContext(ioDispatcher) {
        val existing = topicDao.getTopicByName(name)
        return@withContext existing != null
    }

    override suspend fun wordExistsInTopic(topicId: String, word: Word): Boolean =
        withContext(ioDispatcher) {
            val existing = wordDao.getWordByTopicAndContent(
                topicId,
                word.word,
                word.meaning
            )
            return@withContext existing != null
        }
}