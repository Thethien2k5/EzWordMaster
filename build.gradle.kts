// Top-level build file
plugins {
    // Nâng cấp AGP lên phiên bản mới nhất để đọc SDK v4
    id("com.android.application") version "8.13.0" apply false

    // Giữ nguyên phiên bản Kotlin ổn định
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false

    // Thêm plugin cho Kotlinx Serialization để sửa lỗi 'Unresolved reference: serialization'
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false

}