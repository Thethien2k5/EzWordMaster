package com.example.ezwordmaster.domain.model

enum class FilterSortType {
    ALL,                    // Tất cả
    BY_TOPIC,              // Theo chủ đề (có category)
    BY_OWNER,              // Theo người dùng (có owner)
    NEWEST,                // Mới nhất
    A_TO_Z,                // A - Z
    WORD_COUNT             // Số lượng từ vựng
}
