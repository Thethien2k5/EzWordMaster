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

    //  Tạo file mặc định nếu chưa có
    fun createTopicsFileIfMissing() {
        val file = getTopicsFile()
        if (!file.exists()) {
            val defaultTopics = listOf(
                Topic(
                    id = "1",
                    name = "Learning environment",
                    words = listOf(
                        Word("Student", "Học sinh"),
                        Word("Teacher", "Giáo viên"),
                        Word("Classroom", "Lớp học"),
                        Word("School", "Trường học"),
                        Word("Homework", "Bài tập về nhà"),
                        Word("Exam", "Kỳ thi"),
                        Word("Test", "Bài kiểm tra"),
                        Word("Grade", "Điểm số"),
                        Word("Subject", "Môn học"),
                        Word("Lesson", "Bài học"),
                        Word("Book", "Sách"),
                        Word("Notebook", "Vở ghi"),
                        Word("Pen", "Bút mực"),
                        Word("Pencil", "Bút chì"),
                        Word("Eraser", "Cục tẩy"),
                        Word("Ruler", "Thước kẻ"),
                        Word("Bag", "Cặp sách"),
                        Word("Uniform", "Đồng phục"),
                        Word("Break", "Giờ giải lao"),
                        Word("Lunch", "Bữa trưa"),
                        Word("Library", "Thư viện"),
                        Word("Laboratory", "Phòng thí nghiệm"),
                        Word("Playground", "Sân chơi"),
                        Word("Friend", "Bạn bè"),
                        Word("Classmate", "Bạn cùng lớp"),
                        Word("Principal", "Hiệu trưởng"),
                        Word("Study", "Học tập"),
                        Word("Learn", "Học hỏi"),
                        Word("Teach", "Dạy"),
                        Word("Read", "Đọc"),
                        Word("Write", "Viết"),
                        Word("Calculate", "Tính toán"),
                        Word("Remember", "Ghi nhớ"),
                        Word("Understand", "Hiểu"),
                        Word("Practice", "Luyện tập"),
                        Word("Project", "Dự án"),
                        Word("Presentation", "Bài thuyết trình"),
                        Word("Group", "Nhóm"),
                        Word("Teamwork", "Làm việc nhóm"),
                        Word("Knowledge", "Kiến thức")
                    )
                )
            )
            saveTopics(defaultTopics)
            Log.d("TopicRepo", "Đã tạo file topics.json mặc định")
        }
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

    // Kiểm tra trùng ID hoặc tên (public dùng cho form thêm chủ đề)
    fun isTopicDuplicate(topic: Topic): Boolean {
        val topics = loadTopics()
        return topics.any {
            it.id == topic.id || it.name.equals(topic.name, ignoreCase = true)
        }
    }

    //  Xóa một topic theo id
    fun deleteTopicById(id: String) {
        val currentTopics = loadTopics().filterNot { it.id == id }
        saveTopics(currentTopics)
        Log.d("TopicRepo", "🗑 Đã xóa chủ đề có id=$id")
    }
}
