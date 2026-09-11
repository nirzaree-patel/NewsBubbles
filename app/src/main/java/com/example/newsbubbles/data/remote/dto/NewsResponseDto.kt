package com.example.newsbubbles.data.remote.dto

/**
 * Raw JSON shapes returned by the News API.
 * Only used in the data layer — never leaked into domain or presentation.
 */
data class NewsResponseDto(
    val status: String,
    val totalResults: Int?,
    val articles: List<ArticleDto>?,
    val message: String?
)

data class ArticleDto(
    val source: SourceDto?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?
)

data class SourceDto(
    val id: String?,
    val name: String?
)
