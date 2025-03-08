package com.tahir.flickrimagesearcher.data.repository

import com.tahir.flickrimagesearcher.data.source.local.LocalDataSource
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import timber.log.Timber

@Single
class ImageRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    fun searchImages(searchTerm: String): Flow<Result<List<String>>> = flow {
        val response = remoteDataSource.searchImages(searchTerm)
        val imageUrls = response?.photos?.photo?.map { it.getImageUrl() } ?: mutableListOf()
        emit(Result.success(imageUrls))
    }.catch { e ->
        emit(Result.failure(e))
    }
}