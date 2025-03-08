package com.tahir.flickrimagesearcher.data.source.remote

import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import com.tahir.flickrimagesearcher.data.source.remote.api.FlickrApiService
import com.tahir.flickrimagesearcher.util.ResultWrapper
import okio.IOException
import org.koin.core.annotation.Single
import retrofit2.HttpException
import retrofit2.Response

@Single
class RemoteDataSource(private val apiService: FlickrApiService) {

    suspend fun searchImages(searchTerm: String,page:Int):  ResultWrapper<FlickrResponse>? {
        return try {
            val response: Response<FlickrResponse> = apiService.searchImages(searchTerm = searchTerm,
                page = page
            )
            when {
                response.isSuccessful -> {
                    response.body()?.let {
                        ResultWrapper.Success(it)
                    } ?: ResultWrapper.Error("Response body is null", response.code())
                }
                response.code() == 401 -> ResultWrapper.Error("Unauthorized request", 401)
                response.code() == 500 -> ResultWrapper.Error("Server error", 500)
                else -> ResultWrapper.Error("Unknown error", response.code())
            }
        } catch (e: IOException) {
            ResultWrapper.Error("Network error. Please check your connection.")
        } catch (e: HttpException) {
            ResultWrapper.Error("HTTP error: ${e.message}", e.code())
        } catch (e: Exception) {
            ResultWrapper.Error("Unexpected error: ${e.message}")
        }
    }
}