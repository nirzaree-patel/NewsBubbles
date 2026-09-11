package com.example.newsbubbles.domain.repository

import com.example.newsbubbles.domain.model.Article
import com.example.newsbubbles.domain.model.NewsCategory

/**
 * Repository contract defined in the domain layer.
 * The domain layer depends on this interface; the data layer implements it.
 * This inverts the dependency so the domain has zero knowledge of Retrofit or Room.
 */
interface NewsRepository {
    suspend fun getArticles(category: NewsCategory): Result<List<Article>>
}
