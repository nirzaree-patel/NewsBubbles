package com.example.newsbubbles.data.remote.mapper

import com.example.newsbubbles.data.remote.dto.ArticleDto
import com.example.newsbubbles.domain.model.Article

/**
 * Maps a raw [ArticleDto] from the network into a clean [Article] domain entity.
 *
 * Centralizing mapping here means:
 *  - DTOs and domain models can evolve independently.
 *  - The domain layer never sees Gson annotations or nullable API fields.
 */
fun ArticleDto.toDomain(): Article = Article(
    title = title?.trim()?.takeIf { it.isNotEmpty() } ?: "Untitled",
    description = description?.trim()?.takeIf { it.isNotEmpty() },
    sourceName = source?.name?.trim()?.takeIf { it.isNotEmpty() },
    imageUrl = urlToImage?.trim()?.takeIf { it.startsWith("http") },
    articleUrl = url?.trim()?.takeIf { it.isNotEmpty() }
)
