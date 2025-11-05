package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.model.Topic
import com.example.ezwordmaster.model.Word

/**
 * Interface (bản hợp đồng) cho TopicRepository.
 * Định nghĩa tất cả các chức năng cần có để quản lý dữ liệu về các chủ đề và từ vựng,
 */
interface ITopicRepository {

    // ===== Chức năng về File và Dữ liệu cơ bản =====

    //Kiểm tra xem file dữ liệu topics có tồn tại không.
    suspend fun isTopicsFileExists(): Boolean

    //Tải tất cả các chủ đề từ nguồn dữ liệu.
    suspend fun loadTopics(): List<Topic>

    //Tạo file topics mặc định với dữ liệu chào mừng nếu nó chưa tồn tại.
    suspend fun createTopicsFileIfMissing()

    // ====== Chức năng Tạo và Thêm mới =====
    //Tạo một ID mới cho chủ đề. Logic sẽ ưu tiên lấp đầy các khoảng trống ID đã bị xóa.
    suspend fun generateNewTopicId(): String

    //Thêm một chủ đề mới hoặc cập nhật một chủ đề đã có (dựa trên ID hoặc tên).
    suspend fun addOrUpdateTopic(newTopic: Topic)

    //Thêm một từ mới vào một chủ đề cụ thể.
    suspend fun addWordToTopic(topicId: String, word: Word)

    //Tạo một chủ đề mới chỉ với tên (danh sách từ rỗng).
    suspend fun addNameTopic(newName: String)


    // ======= Chức năng Xóa =========
    // Xóa một chủ đề dựa trên ID của nó.
    suspend fun deleteTopicById(id: String)

    //Xóa một từ khỏi một chủ đề cụ thể.
    suspend fun deleteWordFromTopic(topicId: String, word: Word)

    // ===== Chức năng Cập nhật ====
    //Cập nhật tên của một chủ đề đã tồn tại.
    suspend fun updateTopicName(id: String, newName: String)

    //Cập nhật thông tin của một từ trong một chủ đề.
    suspend fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word)

    // Lấy thông tin chi tiết của một chủ đề dựa trên ID.
    suspend fun getTopicById(id: String): Topic?

    // ===== THÊM CÁC HÀM KIỂM TRA MỚI =====
    //Kiểm tra xem một tên chủ đề đã tồn tại hay chưa (không phân biệt hoa thường).
    suspend fun topicNameExists(name: String): Boolean

    //Kiểm tra xem một từ (word và meaning) đã tồn tại trong một chủ đề cụ thể hay chưa.
    suspend fun wordExistsInTopic(topicId: String, word: Word): Boolean

//    fun isTopicDuplicate(topic: Topic): Boolean
}