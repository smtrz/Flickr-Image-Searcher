package com.tahir.flickrimagesearcher

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import com.tahir.flickrimagesearcher.data.model.Photo
import com.tahir.flickrimagesearcher.data.model.PhotoResponse
import com.tahir.flickrimagesearcher.data.repository.ImageRepository
import com.tahir.flickrimagesearcher.data.source.local.LocalDataSource
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import com.tahir.flickrimagesearcher.di.AppModules
import com.tahir.flickrimagesearcher.ui.ImageViewModel
import com.tahir.flickrimagesearcher.util.ResultWrapper
import io.mockk.InternalPlatformDsl.toArray
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.internal.wait
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import kotlin.test.assertNotEquals

class ImageViewModelTest : KoinTest {

    private lateinit var viewModel: ImageViewModel
    private lateinit var imageRepository: ImageRepository
    private val fakeHistory = listOf("munich", "bayern", "ingolstadt")

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(
            module {
                AppModules().module
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz.java.kotlin)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        imageRepository = declareMock()
        coEvery { imageRepository.getSearchHistory() } returns flowOf(fakeHistory)
        viewModel = ImageViewModel(imageRepository)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSearchClicked should update searchResults`() = runTest {
        val searchTerm = "cats"
        val fakeImages = listOf(
            ImageData("Cat1", "url1"),
            ImageData("Cat2", "url2")
        )
        val fakePagingData = PagingData.from(fakeImages)

        coEvery { imageRepository.storeSearchTerm(any()) } just runs
        coEvery { imageRepository.searchImages(any()) } returns flowOf(fakePagingData)
        viewModel.onSearchClicked(searchTerm)
        advanceUntilIdle()
        val collectedPagingData = viewModel.searchResults.value
        // sort of workaround to convert the collected paging data to flow
        val snapshot = flowOf(collectedPagingData).asSnapshot()

        assertEquals(fakeImages, snapshot)
        coVerify { imageRepository.storeSearchTerm(any()) }
        verify { imageRepository.searchImages(any()) }
    }

    @Test
    fun `getSearchHistory should update searchHistory state`() = runTest {
        val result = viewModel.searchHistory.value
        assertEquals(fakeHistory, result)
        verify { imageRepository.getSearchHistory() }
    }

    @After
    fun teardown() {
        stopKoin()
    }

}
