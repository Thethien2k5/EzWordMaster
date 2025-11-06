package com.example.ezwordmaster.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ezwordmaster.data.local.converters.DateConverter
import com.example.ezwordmaster.data.local.converters.StringListConverter
import com.example.ezwordmaster.data.local.dao.NotificationDao
import com.example.ezwordmaster.data.local.dao.StudyResultDao
import com.example.ezwordmaster.data.local.dao.TopicDao
import com.example.ezwordmaster.data.local.dao.TranslationHistoryDao
import com.example.ezwordmaster.data.local.dao.WordDao
import com.example.ezwordmaster.data.local.entity.NotificationEntity
import com.example.ezwordmaster.data.local.entity.StudyResultEntity
import com.example.ezwordmaster.data.local.entity.TopicEntity
import com.example.ezwordmaster.data.local.entity.TranslationHistoryEntity
import com.example.ezwordmaster.data.local.entity.WordEntity

@Database(
    entities = [
        TopicEntity::class,
        WordEntity::class,
        StudyResultEntity::class,
        NotificationEntity::class,
        TranslationHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class, StringListConverter::class)
abstract class EzWordMasterDatabase : RoomDatabase() {

    abstract fun topicDao(): TopicDao
    abstract fun wordDao(): WordDao
    abstract fun studyResultDao(): StudyResultDao
    abstract fun notificationDao(): NotificationDao
    abstract fun translationHistoryDao(): TranslationHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: EzWordMasterDatabase? = null

        fun getDatabase(context: Context): EzWordMasterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EzWordMasterDatabase::class.java,
                    "ezwordmaster_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}