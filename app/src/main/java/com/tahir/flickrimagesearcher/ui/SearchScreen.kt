package com.tahir.flickrimagesearcher.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.util.ResultWrapper
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun SearchScreen(viewModel: ImageViewModel = koinViewModel()) {
    var searchTerm by remember { mutableStateOf("") }
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val searchHistory by viewModel.searchHistory.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        SearchBox(
            query = searchTerm,
            onQueryChanged = { query ->
                searchTerm = query
            },
            onSearchClicked = {
                viewModel.onSearchClicked(searchTerm)
            },
            searchHistory = searchHistory,
            onSearchHistoryItemClicked = { historyItem ->
                searchTerm = historyItem // Populate search box with history item
                viewModel.onSearchClicked(historyItem) // Trigger search
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Handle Paging Load States (Show Loader or Error)
        when (searchResults.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is LoadState.Error -> {
                val errorMessage =
                    (searchResults.loadState.refresh as LoadState.Error).error.message
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            else -> {
                if (searchResults.itemCount > 0) {
                    ImageList(searchResults)
                }
            }
        }
    }
}

@Composable
fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    searchHistory: List<String>,
    onSearchHistoryItemClicked: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = { newQuery ->
                onQueryChanged(newQuery)
                isDropdownExpanded = newQuery.isNotEmpty() && searchHistory.isNotEmpty()
            },
            label = { Text("Search for images") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isDropdownExpanded = focusState.isFocused && searchHistory.isNotEmpty()
                },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchClicked()
                isDropdownExpanded = false
            })
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSearchClicked()
                isDropdownExpanded = false
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search history dropdown with heading
        if (isDropdownExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Past Searches",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    items(searchHistory.size) { index ->
                        Text(
                            text = searchHistory[index],
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    onSearchHistoryItemClicked(searchHistory[index])
                                    isDropdownExpanded = false
                                },
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageItem(image: ImageData) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column {
            AsyncImage(
                model = image.imageUrl,
                contentDescription = image.title,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = image.title, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ImageList(images: LazyPagingItems<ImageData>) {
    LazyColumn {
        items(images.itemCount) { index ->
            images[index]?.let {
                ImageItem(it)
            }
        }
    }
}


