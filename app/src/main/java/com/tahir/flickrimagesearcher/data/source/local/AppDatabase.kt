package com.tahir.flickrimagesearcher.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tahir.flickrimagesearcher.data.source.local.dao.SearchHistoryDao
import com.tahir.flickrimagesearcher.data.source.local.entity.SearchHistory

/**
 * abstract class to setup app database and contains the abstract method for db operations - Data access object.
 */
@Database(entities = [SearchHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}