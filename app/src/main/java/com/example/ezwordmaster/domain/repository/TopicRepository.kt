package com.example.ezwordmaster.domain.repository

import android.content.Context
import com.example.ezwordmaster.domain.model.Topic
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import android.util.Log
import java.io.File
import com.example.ezwordmaster.domain.model.Word

class TopicRepository(private val context: Context) {
    // "context.filesDi" thư mục riêng tư của app Androi
    //Cho androi studio ( phải máy ảo trước )
    // 4 Gạch góc trên -> View -> Tool Windows -> Device Explorer ( dưới máy ảo sẽ xuất hiện Device Explorer )
    // data/data/com.example.ezwordmaster/files/topocs.json
    private val FILE_NAME = "topics.json"
    // tạo file topics.json nếu chưa có
    fun createTopicsFileIfMissing() {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) {
                val defaultTopics = listOf(
                    Topic(
                        id = "0",
                        name = "Welcome to Ez Word Master",
                        words = listOf(
                            Word("Hello", "Xin chào"),
                            Word("Hi", "Xin chào"),
                            Word("Welcome", "Chào mừng")
                        )
                    )
                )

                val jsonString = Json.encodeToString(defaultTopics)
                file.writeText(jsonString)
                Log.d("TopicInit", "Đã tạo file topics.json với dữ liệu mặc định")
            } else {
                Log.d("TopicInit", "File topics.json đã tồn tại, không cần tạo lại")
            }
        } catch (e: Exception) {
            Log.e("TopicInit", "Lỗi khi tạo file: ${e.message}", e)
        }
    }

    fun loadTopics(): List<Topic> {
        val topicRepo = TopicRepository(context)
        topicRepo.createTopicsFileIfMissing()

        // tạo đt file trỏ đến FILE_NAME
        val file = File(context.filesDir, FILE_NAME)
        try {
            // đọc file và trả về
            val jsonString = file.readText()
            return Json.decodeFromString(jsonString) //giải mã json
        }catch(e: Exception)
        {
            return emptyList() // trả về list rỗng khi lỗi (k tìm thấy file/ k đọc được file )
        }
    }

    fun saveTopics(topics: List<Topic>) {
        // mã hóa list thành json và lưu vào file
        val jsonString = Json.encodeToString(topics)
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(jsonString)
    }
}
