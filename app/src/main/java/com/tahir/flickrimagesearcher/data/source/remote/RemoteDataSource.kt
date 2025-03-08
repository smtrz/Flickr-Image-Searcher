package com.tahir.flickrimagesearcher.data.source.remote

import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import org.koin.core.annotation.Single
import timber.log.Timber

@Single
class RemoteDataSource(private val apiService: FlickrApiService) {

    suspend fun searchImages(searchTerm: String): FlickrResponse? {
        Timber.d("inside search images.")
        val response = apiService.searchImages(searchTerm = searchTerm)
        Timber.d("this is the response $response")
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}