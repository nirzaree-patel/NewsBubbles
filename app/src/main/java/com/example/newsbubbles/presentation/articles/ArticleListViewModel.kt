package com.example.newsbubbles.presentation.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbubbles.domain.model.NewsCategory
import com.example.newsbubbles.domain.usecase.GetArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Article List screen.
 *
 * Depends on [GetArticlesUseCase] — it has NO direct knowledge of the repository
 * or network layer. This is the Clean Architecture dependency rule in action:
 *   Presentation → Domain ← Data
 */
@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleListUiState>(ArticleListUiState.Loading)
    val uiState: StateFlow<ArticleListUiState> = _uiState.asStateFlow()

    fun loadArticles(category: NewsCategory) {
        viewModelScope.launch {
            _uiState.value = ArticleListUiState.Loading
            getArticlesUseCase(category).fold(
                onSuccess = { articles ->
                    _uiState.value = if (articles.isEmpty()) {
                        ArticleListUiState.Error("No articles found for ${category.displayName}.")
                    } else {
                        ArticleListUiState.Success(articles)
                    }
                },
                onFailure = { error ->
                    _uiState.value = ArticleListUiState.Error(
                        error.message ?: "Something went wrong. Please try again."
                    )
                }
            )
        }
    }
}
