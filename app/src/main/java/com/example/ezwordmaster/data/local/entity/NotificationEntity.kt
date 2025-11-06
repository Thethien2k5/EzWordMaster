package com.example.ezwordmaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ezwordmaster.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "notifications")
@TypeConverters(DateConverter::class)
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int = 0,
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val type: String = "system"
)