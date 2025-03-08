package com.tahir.flickrimagesearcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.paging.FlickrPagingSource
import com.tahir.flickrimagesearcher.data.repository.ImageRepository
import com.tahir.flickrimagesearcher.util.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber

@KoinViewModel
class ImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    //    private val _images = MutableStateFlow<ResultWrapper<List<ImageData>>>(
//        ResultWrapper.Success(
//            emptyList()
//        )
//    )
    //val imagesData: StateFlow<ResultWrapper<List<ImageData>>> = _images
    //  private val _searchResults = MutableStateFlow<PagingData<ImageData>>(PagingData.empty())
    //   val searchResults = _searchResults.asStateFlow()
    private val _searchHistory =
        MutableStateFlow<List<String>>(emptyList()) // ✅ Hold search history
    val searchHistory: StateFlow<List<String>> = _searchHistory
    // private val _searchQuery = MutableStateFlow("")
    // val searchQuery = _searchQuery.asStateFlow()


    // private val _searchQuery = MutableStateFlow("")
    // val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<PagingData<ImageData>>(PagingData.empty())
    val searchResults = _searchResults.asStateFlow()

//    private val _isSearchClicked = MutableStateFlow(false)
//    val isSearchClicked = _isSearchClicked.asStateFlow()

//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//    }

    fun onSearchClicked(term: String) {
        viewModelScope.launch {
            imageRepository.storeSearchTerm(term)
            imageRepository.searchImages(term)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _searchResults.value = pagingData
                }

        }
    }


    init {
        Timber.d("INIT of viewmodel called")
        getSearchHistory()
    }


    private fun getSearchHistory() {
        viewModelScope.launch {
            imageRepository.getSearchHistory().collect { history ->
                _searchHistory.value = history // ✅ Update search history
            }
        }
    }
}