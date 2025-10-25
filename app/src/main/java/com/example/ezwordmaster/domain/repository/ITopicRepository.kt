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
    fun isTopicsFileExists(): Boolean
    //Tải tất cả các chủ đề từ nguồn dữ liệu.
    fun loadTopics(): List<Topic>
     //Tạo file topics mặc định với dữ liệu chào mừng nếu nó chưa tồn tại.
    fun createTopicsFileIfMissing()

    // ====== Chức năng Tạo và Thêm mới =====
    //Tạo một ID mới cho chủ đề. Logic sẽ ưu tiên lấp đầy các khoảng trống ID đã bị xóa.
    fun generateNewTopicId(): String
     //Thêm một chủ đề mới hoặc cập nhật một chủ đề đã có (dựa trên ID hoặc tên).
    fun addOrUpdateTopic(newTopic: Topic)
    //Thêm một từ mới vào một chủ đề cụ thể.
    fun addWordToTopic(topicId: String, word: Word)
     //Tạo một chủ đề mới chỉ với tên (danh sách từ rỗng).
    fun addNameTopic(newName: String)


    // ======= Chức năng Xóa =========
    // Xóa một chủ đề dựa trên ID của nó.
    fun deleteTopicById(id: String)
     //Xóa một từ khỏi một chủ đề cụ thể.
    fun deleteWordFromTopic(topicId: String, word: Word)

    // ===== Chức năng Cập nhật ====
     //Cập nhật tên của một chủ đề đã tồn tại.
    fun updateTopicName(id: String, newName: String)
    //Cập nhật thông tin của một từ trong một chủ đề.
    fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word)
    // Lấy thông tin chi tiết của một chủ đề dựa trên ID.
    fun getTopicById(id: String): Topic?

    // ===== THÊM CÁC HÀM KIỂM TRA MỚI =====
     //Kiểm tra xem một tên chủ đề đã tồn tại hay chưa (không phân biệt hoa thường).
    fun topicNameExists(name: String): Boolean
     //Kiểm tra xem một từ (word và meaning) đã tồn tại trong một chủ đề cụ thể hay chưa.
    fun wordExistsInTopic(topicId: String, word: Word): Boolean
}