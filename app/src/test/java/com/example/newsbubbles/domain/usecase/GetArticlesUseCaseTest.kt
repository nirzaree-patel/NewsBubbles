package com.example.newsbubbles.domain.usecase

import com.example.newsbubbles.domain.model.Article
import com.example.newsbubbles.domain.model.NewsCategory
import com.example.newsbubbles.domain.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetArticlesUseCaseTest {

    private val repository = mockk<NewsRepository>()
    private lateinit var useCase: GetArticlesUseCase

    @Before
    fun setUp() {
        useCase = GetArticlesUseCase(repository)
    }

    @Test
    fun `delegates to repository with the requested category`() = runTest {
        coEvery { repository.getArticles(NewsCategory.SPORTS) } returns Result.success(emptyList())

        useCase(NewsCategory.SPORTS)

        coVerify(exactly = 1) { repository.getArticles(NewsCategory.SPORTS) }
    }

    @Test
    fun `returns repository success result unchanged`() = runTest {
        val articles = listOf(
            Article(title = "A", description = null, sourceName = null, imageUrl = null, articleUrl = null)
        )
        coEvery { repository.getArticles(any()) } returns Result.success(articles)

        val result = useCase(NewsCategory.TECHNOLOGY)

        assertTrue(result.isSuccess)
        assertEquals(articles, result.getOrNull())
    }

    @Test
    fun `returns repository failure result unchanged`() = runTest {
        val error = Exception("network down")
        coEvery { repository.getArticles(any()) } returns Result.failure(error)

        val result = useCase(NewsCategory.HEALTH)

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
