package com.example.newsbubbles.presentation.articles

import app.cash.turbine.test
import com.example.newsbubbles.MainDispatcherRule
import com.example.newsbubbles.domain.model.Article
import com.example.newsbubbles.domain.model.NewsCategory
import com.example.newsbubbles.domain.usecase.GetArticlesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArticleListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getArticlesUseCase = mockk<GetArticlesUseCase>()

    private fun viewModel() = ArticleListViewModel(getArticlesUseCase)

    @Test
    fun `starts in Loading state`() = runTest {
        assertEquals(ArticleListUiState.Loading, viewModel().uiState.value)
    }

    @Test
    fun `emits Success when the use case returns articles`() = runTest {
        val articles = listOf(
            Article(title = "A", description = null, sourceName = null, imageUrl = null, articleUrl = null)
        )
        coEvery { getArticlesUseCase(NewsCategory.BUSINESS) } returns Result.success(articles)
        val vm = viewModel()

        vm.uiState.test {
            assertEquals(ArticleListUiState.Loading, awaitItem())
            vm.loadArticles(NewsCategory.BUSINESS)
            assertEquals(ArticleListUiState.Success(articles), awaitItem())
        }
    }

    @Test
    fun `emits Error when the use case returns an empty list`() = runTest {
        coEvery { getArticlesUseCase(NewsCategory.SPORTS) } returns Result.success(emptyList())
        val vm = viewModel()

        vm.uiState.test {
            awaitItem() // Loading
            vm.loadArticles(NewsCategory.SPORTS)
            val state = awaitItem()
            assertTrue(state is ArticleListUiState.Error)
            assertTrue((state as ArticleListUiState.Error).message.contains(NewsCategory.SPORTS.displayName))
        }
    }

    @Test
    fun `emits Error with the exception message when the use case fails`() = runTest {
        coEvery { getArticlesUseCase(NewsCategory.HEALTH) } returns Result.failure(Exception("network down"))
        val vm = viewModel()

        vm.uiState.test {
            awaitItem() // Loading
            vm.loadArticles(NewsCategory.HEALTH)
            assertEquals(ArticleListUiState.Error("network down"), awaitItem())
        }
    }

    @Test
    fun `emits a generic Error when the failure has no message`() = runTest {
        coEvery { getArticlesUseCase(NewsCategory.SCIENCE) } returns Result.failure(Exception())
        val vm = viewModel()

        vm.uiState.test {
            awaitItem() // Loading
            vm.loadArticles(NewsCategory.SCIENCE)
            assertEquals(
                ArticleListUiState.Error("Something went wrong. Please try again."),
                awaitItem()
            )
        }
    }
}
