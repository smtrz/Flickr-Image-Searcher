package com.tahir.flickrimagesearcher

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tahir.flickrimagesearcher.data.source.local.AppDatabase
import com.tahir.flickrimagesearcher.data.source.local.dao.SearchHistoryDao
import com.tahir.flickrimagesearcher.data.source.local.entity.SearchHistory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchHistoryDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: SearchHistoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        dao = database.searchHistoryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertSearchQuery_retrievesSuccessfully() = runBlocking {
        val searchQuery = SearchHistory(query = "karachi")
        dao.insertSearchQuery(searchQuery)

        val history = dao.getSearchHistory().first()

        assertEquals(1, history.size)
        assertEquals("karachi", history[0].query)
    }


    @Test
    fun insertDuplicateQuery_doesNotDuplicate() = runBlocking {
        val searchQuery = SearchHistory(query = "tahir")
        dao.insertSearchQuery(searchQuery)
        dao.insertSearchQuery(searchQuery)

        // search history
        val history = dao.getSearchHistory().first()

        //  Only one entry should be stored
        assertEquals(1, history.size)
    }

    @Test
    fun insertMultipleQueries_retrievesInDescendingOrder() = runBlocking {
        val queries = listOf(
            SearchHistory(query = "query1"),
            SearchHistory(query = "query2"),
            SearchHistory(query = "query is 3"),
            SearchHistory(query = "query4")
        )

        queries.forEach { dao.insertSearchQuery(it) }

        //  Retrieve search history
        val history = dao.getSearchHistory().first()

        //  History should be in descending order (latest first)
        assertEquals(4, history.size)
        assertEquals("query4", history[0].query) // Last inserted should be first
        assertEquals("query is 3", history[1].query)
        assertEquals("query2", history[2].query)
        assertEquals("query1", history[3].query)
    }
}