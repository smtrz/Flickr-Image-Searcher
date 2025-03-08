package com.tahir.flickrimagesearcher.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import com.tahir.flickrimagesearcher.util.ResultWrapper
import timber.log.Timber


class FlickrPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val searchTerm: String
) : PagingSource<Int, ImageData>() {
    init {
        Timber.d("⚡ FlickrPagingSource Created for searchTerm: $searchTerm")

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageData> {
        return try {
            // start with page 1 if no data has been provided.
            val page = params.key ?: 1
            // Fetch data from the API
            when (val response = remoteDataSource.searchImages(searchTerm, page)) {
                is ResultWrapper.Success -> {
                    val imageData = response.data.photos.photo.map {
                        ImageData(it.title, it.getImageUrl())
                    }

                    // Calculate next and previous keys
                    val nextKey = if (imageData.isEmpty()) null else page + 1
                    val prevKey = if (page == 1) null else page - 1

                    LoadResult.Page(
                        data = imageData,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                is ResultWrapper.Error -> {
                    LoadResult.Error(Throwable(response.message))
                }

                is ResultWrapper.Loading -> {
                    // No need to handle loading explicitly inside PagingSource
                    // The UI will show loading via LoadState in PagingData
                    LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
                }

                else -> {
                    LoadResult.Error(Throwable("Unexpected response"))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}