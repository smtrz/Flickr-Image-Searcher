package com.tahir.flickrimagesearcher

import com.tahir.flickrimagesearcher.data.model.FlickrResponse
import com.tahir.flickrimagesearcher.data.model.Photo
import com.tahir.flickrimagesearcher.data.model.PhotoResponse
import com.tahir.flickrimagesearcher.data.source.remote.RemoteDataSource
import com.tahir.flickrimagesearcher.data.source.remote.api.FlickrApiService
import com.tahir.flickrimagesearcher.di.AppModules
import com.tahir.flickrimagesearcher.util.ResultWrapper
import io.mockk.coEvery
import io.mockk.mockkClass
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
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.test.assertEquals

class RemoteDataSourceTest : KoinTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var apiService: FlickrApiService

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
        apiService = declareMock()
        remoteDataSource = RemoteDataSource(apiService)
    }

    @Test
    fun `searchImages should return success when API call is successful`() = runTest {
        val searchTerm = "cats"
        val page = 1
        val fakeResponse = FlickrResponse(
            photos = PhotoResponse(
                photo = listOf(
                    Photo("1", "secret1", "server1", 1, "Cat1"),
                    Photo("2", "secret2", "server2", 1, "Cat2")
                )
            )
        )
        coEvery {
            apiService.searchImages(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Response.success(fakeResponse)
        val result = remoteDataSource.searchImages(searchTerm, page)
        assertEquals(ResultWrapper.Success(fakeResponse), result)

    }
    @Test
    fun `searchImages should return error when API call fails`() = runTest {
        val searchTerm = "cats"
        val page = 1
        val errorResponse = Response.error<FlickrResponse>(500, ResponseBody.create(null, "Server error"))

        coEvery { apiService.searchImages(any(), any(), any(), any(), any(), any(), any()) } returns errorResponse

        val result = remoteDataSource.searchImages(searchTerm, page)

        assertEquals(ResultWrapper.Error("Server error", 500), result)
    }}