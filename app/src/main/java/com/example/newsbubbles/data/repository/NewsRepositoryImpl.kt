package com.example.newsbubbles.data.repository

import com.example.newsbubbles.BuildConfig
import com.example.newsbubbles.data.remote.api.NewsApiService
import com.example.newsbubbles.data.remote.mapper.toDomain
import com.example.newsbubbles.domain.model.Article
import com.example.newsbubbles.domain.model.NewsCategory
import com.example.newsbubbles.domain.repository.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [NewsRepository] that lives in the data layer.
 *
 * Dependency direction:
 *   data → domain (implements domain interface)
 *   domain has zero knowledge of this class.
 *
 * Responsibilities:
 *  - Calls [NewsApiService] with the correct API key.
 *  - Converts [ArticleDto] → [Article] via the mapper.
 *  - Wraps errors in [Result.failure] so callers never need try/catch.
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService
) : NewsRepository {

    override suspend fun getArticles(category: NewsCategory): Result<List<Article>> {
        return try {
            val response = api.getTopHeadlines(
                category = category.apiValue,
                apiKey = BuildConfig.NEWS_API_KEY
            )
            if (response.status == "ok") {
                val articles = response.articles?.map { it.toDomain() } ?: emptyList()
                Result.success(articles)
            } else {
                Result.failure(Exception(response.message ?: "API error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
