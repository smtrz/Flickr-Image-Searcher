package com.tahir.flickrimagesearcher

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.tahir.flickrimagesearcher.data.dto.ImageData
import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import com.tahir.flickrimagesearcher.data.model.Photo
import com.tahir.flickrimagesearcher.data.model.PhotoResponse
import com.tahir.flickrimagesearcher.data.repository.ImageRepository
import com.tahir.flickrimagesearcher.data.source.local.LocalDataSource
import com.tahir.flickrimagesearcher.data.source.local.entity.SearchHistory
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import com.tahir.flickrimagesearcher.di.AppModules
import com.tahir.flickrimagesearcher.util.ResultWrapper
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
import kotlin.test.assertNotNull


class ImageRepositoryTest : KoinTest {

    private lateinit var imageRepository: ImageRepository
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource

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

    @After
    fun teardown() {
        stopKoin()
    }

    @Before
    fun setup() {
        remoteDataSource = declareMock()
        localDataSource = declareMock()
        imageRepository = ImageRepository(remoteDataSource, localDataSource)
    }

    @Test
    fun `storeSearchTerm should call localDataSource saveSearchQuery`() = runTest {
        val searchTerm = "tahir"
        coEvery { localDataSource.saveSearchQuery(searchTerm) } just Runs
        imageRepository.storeSearchTerm(searchTerm)
        coVerify(exactly = 1) { localDataSource.saveSearchQuery(searchTerm) }
    }


    @Test
    fun `getSearchHistory should return list of search terms`() = runTest {
        val fakeHistory =
            listOf(SearchHistory(query = "tahir"), SearchHistory(query = "munich"), SearchHistory(query = "germany"))
        every { localDataSource.getSearchHistory() } returns flowOf(fakeHistory)
        val result = imageRepository.getSearchHistory().first()
        assertEquals(fakeHistory.map { it.query }, result)
    }

    @Test
    fun `searchImages should return paging data`() = runTest {
        //  Given: Fake API response
        val searchTerm = "cats"
        val fakeFlickrResponse = FlickrResponse(
            photos = PhotoResponse(
                photo = listOf(
                    Photo(
                        id = "1",
                        secret = "secret1",
                        server = "server1",
                        farm = 1,
                        title = "Cat1"
                    ),
                    Photo(
                        id = "2",
                        secret = "secret2",
                        server = "server2",
                        farm = 1,
                        title = "Cat2"
                    )
                )
            )
        )

        val fakeImageList = fakeFlickrResponse.photos.photo.map {
            ImageData(it.title, it.getImageUrl())
        }

        coEvery { remoteDataSource.searchImages(searchTerm, any()) } returns ResultWrapper.Success(
            fakeFlickrResponse
        )

        mockkConstructor(Pager::class)
        every { anyConstructed<Pager<Int, ImageData>>().flow } returns flowOf(
            PagingData.from(fakeImageList)
        )
        val pagingDataFlow = imageRepository.searchImages(searchTerm)
        val pagingData = pagingDataFlow.asSnapshot()
        assertNotNull(pagingDataFlow)
        assertEquals(fakeImageList, pagingData)
    }
}
