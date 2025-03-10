package com.tahir.flickrimagesearcher.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.paging.FlickrPagingSource
import com.tahir.flickrimagesearcher.data.source.local.LocalDataSource
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class ImageRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    fun searchImages(searchTerm: String): Flow<PagingData<ImageData>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { FlickrPagingSource(remoteDataSource, searchTerm) }
        ).flow.flowOn(Dispatchers.IO)

    }

    suspend fun storeSearchTerm(searchTerm: String) {
        localDataSource.saveSearchQuery(searchTerm)
    }

    fun getSearchHistory(): Flow<List<String>> =
        localDataSource.getSearchHistory()
            .map { historyList -> historyList.map { it.query } }
}
