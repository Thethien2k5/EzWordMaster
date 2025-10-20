package com.example.ezwordmaster.di

import android.content.Context
import com.example.ezwordmaster.data.datastore.SettingsDataStore
import com.example.ezwordmaster.data.repository.NotificationHistoryManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHistoryManager(@ApplicationContext context: Context): NotificationHistoryManager {
        return NotificationHistoryManager(context)
    }
}