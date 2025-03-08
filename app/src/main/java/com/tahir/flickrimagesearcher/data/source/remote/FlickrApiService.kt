package com.tahir.flickrimagesearcher.data.source.remote

import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import com.tahir.flickrimagesearcher.util.API_KEY
import com.tahir.flickrimagesearcher.util.FORMAT
import com.tahir.flickrimagesearcher.util.METHOD
import com.tahir.flickrimagesearcher.util.NO_JSON_CALLBACK
import com.tahir.flickrimagesearcher.util.SAFE_SEARCH
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApiService {

    @GET("services/rest/")
    suspend fun searchImages(
        @Query("method") method: String = METHOD,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("format") format: String = FORMAT,
        @Query("nojsoncallback") noJsonCallback: Int = NO_JSON_CALLBACK,
        @Query("safe_search") safeSearch: Int = SAFE_SEARCH,
        @Query("text") searchTerm: String
    ): Response<FlickrResponse>

}