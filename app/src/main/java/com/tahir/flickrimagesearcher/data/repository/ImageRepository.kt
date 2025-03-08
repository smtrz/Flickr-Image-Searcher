package com.tahir.flickrimagesearcher.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import com.tahir.flickrimagesearcher.data.paging.FlickrPagingSource
import com.tahir.flickrimagesearcher.data.source.local.LocalDataSource
import com.tahir.flickrimagesearcher.data.source.local.entity.SearchHistory
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import com.tahir.flickrimagesearcher.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import timber.log.Timber

@Single
class ImageRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    fun searchImages(searchTerm: String): Flow<PagingData<ImageData>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { FlickrPagingSource(remoteDataSource, searchTerm) }
        ).flow

        //  return  remoteDataSource.searchImages(searchTerm,page)

    }

    //    fun searchImages(searchTerm: String,page:Int=1): Flow<ResultWrapper<List<ImageData>>> = flow {
//        //store the data in local db
//        localDataSource.saveSearchQuery(searchTerm)
//        // emit and transform the flow
//        emit(remoteDataSource.searchImages(searchTerm))
//    }.map { result ->
//        when (result) {
//            is ResultWrapper.Success -> {
//                val imageData = result.data.photos.photo.map {
//                    ImageData(it.title, it.getImageUrl())
//
//                }
//                ResultWrapper.Success(imageData)
//            }
//
//            is ResultWrapper.Error -> result // Forward Error as is
//            is ResultWrapper.Loading -> ResultWrapper.Loading // Forward Loading as is
//            null -> ResultWrapper.Error("Unexpected null response")
//        }
//    }
//        .catch { e ->
//            println("Error in ImageRepository: ${e.message}") // Log the error
//            emit(ResultWrapper.Error(e.message ?: "Unexpected Error"))
//        }
    suspend fun storeSearchTerm(searchTerm: String) {

        //store the data in local db
        localDataSource.saveSearchQuery(searchTerm)
    }

    fun getSearchHistory(): Flow<List<String>> =
        localDataSource.getSearchHistory()
            .map { historyList -> historyList.map { it.query } } // ✅ Convert List<SearchHistory> to List<String>
            .flowOn(Dispatchers.IO)
}
