package com.example.ezwordmaster.data.cloud

import android.util.Log
import com.example.ezwordmaster.domain.repository.ICloudStudyResultRepository
import com.example.ezwordmaster.model.StudyResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repository để sync StudyResults với Firestore.
 * Chỉ xử lý các operations với Firestore, không ảnh hưởng đến local database.
 */
class CloudStudyResultRepository(
    private val userId: String
) : ICloudStudyResultRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val studyResultsCollection = firestore.collection("users")
        .document(userId)
        .collection("studyResults")

    companion object {
        private const val TAG = "CloudStudyResultRepo"
    }

    /**
     * Lưu một study result lên Firestore.
     */
    suspend fun saveStudyResult(result: StudyResult): Result<Unit> {
        return try {
            val resultData = hashMapOf(
                "id" to result.id,
                "topicId" to result.topicId,
                "topicName" to result.topicName,
                "studyMode" to result.studyMode,
                "day" to result.day,
                "duration" to result.duration,
                "totalWords" to result.totalWords,
                "knownWords" to result.knownWords,
                "learningWords" to result.learningWords,
                "accuracy" to result.accuracy,
                "totalPairs" to result.totalPairs,
                "matchedPairs" to result.matchedPairs,
                "completionRate" to result.completionRate,
                "playTime" to result.playTime,
                "lastModified" to Date()
            )

            studyResultsCollection.document(result.id)
                .set(resultData, SetOptions.merge())
                .await()

            Log.d(TAG, "✅ Đã sync study result ${result.id} lên Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi sync study result lên Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Lưu nhiều study results lên Firestore.
     */
    override suspend fun saveStudyResults(results: List<StudyResult>): Result<Unit> {
        return try {
            val batch = firestore.batch()

            results.forEach { result ->
                val resultData = hashMapOf(
                    "id" to result.id,
                    "topicId" to result.topicId,
                    "topicName" to result.topicName,
                    "studyMode" to result.studyMode,
                    "day" to result.day,
                    "duration" to result.duration,
                    "totalWords" to result.totalWords,
                    "knownWords" to result.knownWords,
                    "learningWords" to result.learningWords,
                    "accuracy" to result.accuracy,
                    "totalPairs" to result.totalPairs,
                    "matchedPairs" to result.matchedPairs,
                    "completionRate" to result.completionRate,
                    "playTime" to result.playTime,
                    "lastModified" to Date()
                )

                val resultRef = studyResultsCollection.document(result.id)
                batch.set(resultRef, resultData, SetOptions.merge())
            }

            batch.commit().await()
            Log.d(TAG, "✅ Đã sync ${results.size} study results lên Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi sync study results lên Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Lấy tất cả study results từ Firestore.
     */
    override suspend fun loadStudyResults(): Result<List<StudyResult>> {
        return try {
            val snapshot = studyResultsCollection.get().await()
            val results = snapshot.documents.mapNotNull { doc ->
                try {
                    StudyResult(
                        id = doc.getString("id") ?: doc.id,
                        topicId = doc.getString("topicId") ?: "",
                        topicName = doc.getString("topicName") ?: "",
                        studyMode = doc.getString("studyMode") ?: "",
                        day = doc.getString("day") ?: "",
                        duration = (doc.get("duration") as? Number)?.toLong() ?: 0L,
                        totalWords = (doc.get("totalWords") as? Number)?.toInt(),
                        knownWords = (doc.get("knownWords") as? Number)?.toInt(),
                        learningWords = (doc.get("learningWords") as? Number)?.toInt(),
                        accuracy = (doc.get("accuracy") as? Number)?.toFloat(),
                        totalPairs = (doc.get("totalPairs") as? Number)?.toInt(),
                        matchedPairs = (doc.get("matchedPairs") as? Number)?.toInt(),
                        completionRate = (doc.get("completionRate") as? Number)?.toFloat(),
                        playTime = (doc.get("playTime") as? Number)?.toLong()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Lỗi khi parse study result từ Firestore: ${e.message}", e)
                    null
                }
            }

            Log.d(TAG, "✅ Đã load ${results.size} study results từ Firestore")
            Result.success(results)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi load study results từ Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Xóa tất cả study results khỏi Firestore.
     */
    suspend fun deleteAllStudyResults(): Result<Unit> {
        return try {
            val snapshot = studyResultsCollection.get().await()

            if (snapshot.documents.isEmpty()) {
                Log.d(TAG, "ℹ️ Không có study results để xóa")
                return Result.success(Unit)
            }

            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
            Log.d(TAG, "🗑️ Đã xóa ${snapshot.documents.size} study results khỏi Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi xóa study results khỏi Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Kiểm tra xem có dữ liệu trên Firestore không.
     */
    override suspend fun hasData(): Boolean {
        return try {
            val snapshot = studyResultsCollection.limit(1).get().await()
            snapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi kiểm tra dữ liệu trên Firestore: ${e.message}", e)
            false
        }
    }
}