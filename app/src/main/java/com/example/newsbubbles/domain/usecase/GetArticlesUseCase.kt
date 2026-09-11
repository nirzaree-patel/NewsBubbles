package com.example.newsbubbles.domain.usecase

import com.example.newsbubbles.domain.model.Article
import com.example.newsbubbles.domain.model.NewsCategory
import com.example.newsbubbles.domain.repository.NewsRepository
import javax.inject.Inject

/**
 * Use case: fetch and return the article list for a given [NewsCategory].
 *
 * Responsibility: Delegates to [NewsRepository] (defined in domain; implemented in data layer).
 *
 * Kept as a plain `operator fun invoke` so the ViewModel calls it like a function:
 *   `getArticlesUseCase(category)`
 */
class GetArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(category: NewsCategory): Result<List<Article>> =
        repository.getArticles(category)
}
