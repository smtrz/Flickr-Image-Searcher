package com.tahir.flickrimagesearcher.di

import android.content.Context
import androidx.room.Room
import com.tahir.flickrimagesearcher.data.source.local.AppDatabase
import com.tahir.flickrimagesearcher.data.source.local.dao.SearchHistoryDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabaseModule {

    @Single
    fun provideDatabase(app: Context) = Room.databaseBuilder(app, AppDatabase::class.java, "flickrImageSearcher").build()

    @Single
    fun provideDao(appDatabase: AppDatabase): SearchHistoryDao = appDatabase.searchHistoryDao()

}