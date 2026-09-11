package com.example.newsbubbles.presentation.articles

import com.example.newsbubbles.domain.model.Article

/**
 * Sealed UI state for the Article List screen.
 * The ViewModel emits one of these; the screen renders accordingly.
 */
sealed interface ArticleListUiState {
    data object Loading : ArticleListUiState
    data class Success(val articles: List<Article>) : ArticleListUiState
    data class Error(val message: String) : ArticleListUiState
}
