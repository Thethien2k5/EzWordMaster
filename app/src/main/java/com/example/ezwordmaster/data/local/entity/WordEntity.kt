package com.example.ezwordmaster.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["topicId"])]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val wordId: Long = 0,
    val topicId: String,
    val word: String?,
    val meaning: String?,
    val example: String?
)
