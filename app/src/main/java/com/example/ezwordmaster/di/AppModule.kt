// DÁN VÀ THAY THẾ TOÀN BỘ NỘI DUNG FILE NÀY
package com.example.ezwordmaster.di

import android.content.Context
import androidx.room.Room
import com.example.ezwordmaster.data.datastore.SettingsDataStore // Đảm bảo import đúng
import com.example.ezwordmaster.data.local.AppDatabase
import com.example.ezwordmaster.data.local.NotificationDao
import com.example.ezwordmaster.data.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // THÊM PHẦN NÀY VÀO ĐỂ SỬA LỖI
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }
    // KẾT THÚC PHẦN THÊM VÀO

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ezwordmaster_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepository(notificationDao)
    }
}