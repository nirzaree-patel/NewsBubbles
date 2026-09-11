package com.example.newsbubbles.domain.model

/**
 * Domain entity representing a single news article.
 * Pure Kotlin — contains only what the app actually needs; no serialization annotations.
 */
data class Article(
    val title: String,
    val description: String?,
    val sourceName: String?,
    val imageUrl: String?,
    val articleUrl: String?
)
