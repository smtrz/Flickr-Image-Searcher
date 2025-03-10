package com.tahir.flickrimagesearcher.data.source.local

import com.tahir.flickrimagesearcher.data.source.local.dao.SearchHistoryDao
import com.tahir.flickrimagesearcher.data.source.local.entity.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class LocalDataSource(private val searchHistoryDao: SearchHistoryDao) {

    suspend fun saveSearchQuery(query: String) {
        withContext(Dispatchers.IO) {
            searchHistoryDao.insertSearchQuery(SearchHistory(query = query))
        }
    }

    fun getSearchHistory() = searchHistoryDao.getSearchHistory()
}