package com.tahir.flickrimagesearcher.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tahir.flickrimagesearcher.data.source.local.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // to avoid duplication
    suspend fun insertSearchQuery(query: SearchHistory)

    @Query("SELECT * FROM search_history ORDER BY `query` DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<SearchHistory>>
}