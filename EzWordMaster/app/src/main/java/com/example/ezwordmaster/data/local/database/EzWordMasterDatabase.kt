package com.example.ezwordmaster.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.ezwordmaster.data.local.dao.StudyResultDao
import com.example.ezwordmaster.data.local.dao.TopicDao
import com.example.ezwordmaster.data.local.dao.WordDao
import com.example.ezwordmaster.data.local.entity.StudyResultEntity
import com.example.ezwordmaster.data.local.entity.TopicEntity
import com.example.ezwordmaster.data.local.entity.WordEntity

@Database(
    entities = [TopicEntity::class, WordEntity::class, StudyResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EzWordMasterDatabase : RoomDatabase() {
    
    abstract fun topicDao(): TopicDao
    abstract fun wordDao(): WordDao
    abstract fun studyResultDao(): StudyResultDao
    
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
                    .fallbackToDestructiveMigration() // Tạm thời dùng để test, có thể thêm migration sau
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


