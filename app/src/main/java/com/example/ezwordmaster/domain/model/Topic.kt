package com.example.ezwordmaster.domain.model
import kotlinx.serialization.Serializable

@Serializable // cho phép chuyển đổi giữa kotlin và json
data class Topic(
    val id: String? = null,
    val name: String? = null,
    val words: List<Word>,
    val owner: String? = null,           // Tên chủ sở hữu
    val createdDate: Long? = null,        // Thời gian tạo (timestamp)
    val lastModified: Long? = null,      // Thời gian sửa cuối (timestamp)
    val category: String? = null,         // Danh mục
    val description: String? = null        // Mô tả
)