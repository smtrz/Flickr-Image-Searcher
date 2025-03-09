package com.tahir.flickrimagesearcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber

@KoinViewModel
class ImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    private val _searchHistory =
        MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _searchResults = MutableStateFlow<PagingData<ImageData>>(PagingData.empty())
    val searchResults = _searchResults.asStateFlow()

    fun onSearchClicked(term: String) {
        Timber.d("OnSearch() called with searchTerm : $term")
        viewModelScope.launch {
            imageRepository.storeSearchTerm(term)
            imageRepository.searchImages(term)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    println("this is important place. ${pagingData}")
                    _searchResults.value = pagingData
                }

        }
    }

    init {
        Timber.d("Initializing the ImageViewModel")
        getSearchHistory()
    }

    private fun getSearchHistory() {
        Timber.d("getSearchHistory()")
        viewModelScope.launch {
            imageRepository.getSearchHistory().collect { history ->
                _searchHistory.value = history
            }
        }
    }
}