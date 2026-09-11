package com.example.newsbubbles.data.repository

import com.example.newsbubbles.data.remote.api.NewsApiService
import com.example.newsbubbles.data.remote.dto.ArticleDto
import com.example.newsbubbles.data.remote.dto.NewsResponseDto
import com.example.newsbubbles.data.remote.dto.SourceDto
import com.example.newsbubbles.domain.model.NewsCategory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NewsRepositoryImplTest {

    private val api = mockk<NewsApiService>()
    private lateinit var repository: NewsRepositoryImpl

    @Before
    fun setUp() {
        repository = NewsRepositoryImpl(api)
    }

    @Test
    fun `ok status maps articles to domain models`() = runTest {
        coEvery { api.getTopHeadlines(category = any(), country = any(), pageSize = any(), apiKey = any()) } returns
            NewsResponseDto(
                status = "ok",
                totalResults = 1,
                articles = listOf(
                    ArticleDto(
                        source = SourceDto(id = "bbc-news", name = "BBC News"),
                        title = "Headline",
                        description = "Description",
                        url = "https://example.com",
                        urlToImage = "https://example.com/img.jpg",
                        publishedAt = "2024-01-01T00:00:00Z"
                    )
                ),
                message = null
            )

        val result = repository.getArticles(NewsCategory.GENERAL)

        assertTrue(result.isSuccess)
        val articles = result.getOrNull().orEmpty()
        assertEquals(1, articles.size)
        assertEquals("Headline", articles.first().title)
        assertEquals("BBC News", articles.first().sourceName)
    }

    @Test
    fun `ok status with null articles maps to empty list`() = runTest {
        coEvery { api.getTopHeadlines(category = any(), country = any(), pageSize = any(), apiKey = any()) } returns
            NewsResponseDto(status = "ok", totalResults = 0, articles = null, message = null)

        val result = repository.getArticles(NewsCategory.GENERAL)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Any>(), result.getOrNull())
    }

    @Test
    fun `non-ok status with message returns failure with that message`() = runTest {
        coEvery { api.getTopHeadlines(category = any(), country = any(), pageSize = any(), apiKey = any()) } returns
            NewsResponseDto(status = "error", totalResults = null, articles = null, message = "apiKey invalid")

        val result = repository.getArticles(NewsCategory.GENERAL)

        assertTrue(result.isFailure)
        assertEquals("apiKey invalid", result.exceptionOrNull()?.message)
    }

    @Test
    fun `non-ok status without message falls back to a generic error`() = runTest {
        coEvery { api.getTopHeadlines(category = any(), country = any(), pageSize = any(), apiKey = any()) } returns
            NewsResponseDto(status = "error", totalResults = null, articles = null, message = null)

        val result = repository.getArticles(NewsCategory.GENERAL)

        assertTrue(result.isFailure)
        assertEquals("API error: error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `exception from the api is wrapped in a failure result`() = runTest {
        val networkError = java.io.IOException("no connection")
        coEvery { api.getTopHeadlines(category = any(), country = any(), pageSize = any(), apiKey = any()) } throws
            networkError

        val result = repository.getArticles(NewsCategory.GENERAL)

        assertTrue(result.isFailure)
        assertEquals(networkError, result.exceptionOrNull())
    }

    @Test
    fun `requests the category's apiValue`() = runTest {
        coEvery { api.getTopHeadlines(category = "science", country = any(), pageSize = any(), apiKey = any()) } returns
            NewsResponseDto(status = "ok", totalResults = 0, articles = null, message = null)

        val result = repository.getArticles(NewsCategory.SCIENCE)

        assertTrue(result.isSuccess)
    }
}
