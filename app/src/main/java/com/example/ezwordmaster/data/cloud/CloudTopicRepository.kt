package com.example.ezwordmaster.data.cloud

import android.util.Log
import com.example.ezwordmaster.domain.repository.ICloudTopicRepository
import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repository để sync Topics và Words với Firestore.
 * Chỉ xử lý các operations với Firestore, không ảnh hưởng đến local database.
 */
class CloudTopicRepository(
    private val userId: String
) : ICloudTopicRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val topicsCollection =
        firestore.collection("users").document(userId).collection("topics")

    companion object {
        private const val TAG = "CloudTopicRepository"
    }

    /**
     * Lưu một topic lên Firestore (tạo mới hoặc cập nhật).
     */
    suspend fun saveTopic(topic: Topic): Result<Unit> {
        return try {
            if (topic.id == null) {
                return Result.failure(IllegalArgumentException("Topic ID cannot be null"))
            }

            val topicData = hashMapOf(
                "id" to topic.id,
                "name" to (topic.name ?: ""),
                "lastModified" to Date(),
                "words" to topic.words.map { word ->
                    hashMapOf(
                        "word" to (word.word ?: ""),
                        "meaning" to (word.meaning ?: ""),
                        "example" to (word.example ?: "")
                    )
                }
            )

            topicsCollection.document(topic.id)
                .set(topicData, SetOptions.merge())
                .await()

            Log.d(TAG, "Đã sync topic '${topic.name}' lên Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi sync topic lên Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Lưu nhiều topics lên Firestore.
     */
    override suspend fun saveTopics(topics: List<Topic>): Result<Unit> {
        return try {
            val batch = firestore.batch()

            topics.forEach { topic ->
                if (topic.id != null) {
                    val topicData = hashMapOf(
                        "id" to topic.id,
                        "name" to (topic.name ?: ""),
                        "lastModified" to Date(),
                        "words" to topic.words.map { word ->
                            hashMapOf(
                                "word" to (word.word ?: ""),
                                "meaning" to (word.meaning ?: ""),
                                "example" to (word.example ?: "")
                            )
                        }
                    )

                    val topicRef = topicsCollection.document(topic.id)
                    batch.set(topicRef, topicData, SetOptions.merge())
                }
            }

            batch.commit().await()
            Log.d(TAG, "Đã sync ${topics.size} topics lên Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi sync topics lên Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Lấy tất cả topics từ Firestore.
     */
    override suspend fun loadTopics(): Result<List<Topic>> {
        return try {
            val snapshot = topicsCollection.get().await()
            val topics = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.getString("id") ?: doc.id
                    val name = doc.getString("name") ?: ""
                    val wordsData = doc.get("words") as? List<Map<String, Any>> ?: emptyList()

                    val words = wordsData.mapNotNull { wordMap ->
                        Word(
                            word = wordMap["word"] as? String,
                            meaning = wordMap["meaning"] as? String,
                            example = wordMap["example"] as? String
                        )
                    }

                    Topic(id = id, name = name, words = words)
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi khi parse topic từ Firestore: ${e.message}", e)
                    null
                }
            }

            Log.d(TAG, "Đã load ${topics.size} topics từ Firestore")
            Result.success(topics)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi load topics từ Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Xóa một topic khỏi Firestore.
     */
    suspend fun deleteTopic(topicId: String): Result<Unit> {
        return try {
            topicsCollection.document(topicId).delete().await()
            Log.d(TAG, "Đã xóa topic $topicId khỏi Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi xóa topic khỏi Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Kiểm tra xem có dữ liệu trên Firestore không.
     */
    override suspend fun hasData(): Boolean {
        return try {
            val snapshot = topicsCollection.limit(1).get().await()
            snapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi kiểm tra dữ liệu trên Firestore: ${e.message}", e)
            false
        }
    }
}