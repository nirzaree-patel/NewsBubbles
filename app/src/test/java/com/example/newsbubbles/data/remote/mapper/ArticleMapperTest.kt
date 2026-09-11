package com.example.newsbubbles.data.remote.mapper

import com.example.newsbubbles.data.remote.dto.ArticleDto
import com.example.newsbubbles.data.remote.dto.SourceDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleMapperTest {

    private fun dto(
        source: SourceDto? = SourceDto(id = "bbc-news", name = "BBC News"),
        title: String? = "Some headline",
        description: String? = "Some description",
        url: String? = "https://example.com/article",
        urlToImage: String? = "https://example.com/image.jpg",
    ) = ArticleDto(
        source = source,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = "2024-01-01T00:00:00Z"
    )

    @Test
    fun `maps all fields when present`() {
        val article = dto().toDomain()

        assertEquals("Some headline", article.title)
        assertEquals("Some description", article.description)
        assertEquals("BBC News", article.sourceName)
        assertEquals("https://example.com/image.jpg", article.imageUrl)
        assertEquals("https://example.com/article", article.articleUrl)
    }

    @Test
    fun `blank or whitespace fields are trimmed`() {
        val article = dto(title = "  Padded title  ", description = "  padded desc  ").toDomain()

        assertEquals("Padded title", article.title)
        assertEquals("padded desc", article.description)
    }

    @Test
    fun `null title falls back to Untitled`() {
        assertEquals("Untitled", dto(title = null).toDomain().title)
    }

    @Test
    fun `blank title falls back to Untitled`() {
        assertEquals("Untitled", dto(title = "   ").toDomain().title)
    }

    @Test
    fun `null description maps to null`() {
        assertNull(dto(description = null).toDomain().description)
    }

    @Test
    fun `blank description maps to null`() {
        assertNull(dto(description = "   ").toDomain().description)
    }

    @Test
    fun `null source maps to null sourceName`() {
        assertNull(dto(source = null).toDomain().sourceName)
    }

    @Test
    fun `blank source name maps to null`() {
        assertNull(dto(source = SourceDto(id = "x", name = "  ")).toDomain().sourceName)
    }

    @Test
    fun `imageUrl not starting with http is dropped`() {
        assertNull(dto(urlToImage = "ftp://example.com/image.jpg").toDomain().imageUrl)
        assertNull(dto(urlToImage = "example.com/image.jpg").toDomain().imageUrl)
    }

    @Test
    fun `null imageUrl maps to null`() {
        assertNull(dto(urlToImage = null).toDomain().imageUrl)
    }

    @Test
    fun `https imageUrl is kept`() {
        assertEquals(
            "https://example.com/image.jpg",
            dto(urlToImage = "https://example.com/image.jpg").toDomain().imageUrl
        )
    }

    @Test
    fun `blank articleUrl maps to null`() {
        assertNull(dto(url = "   ").toDomain().articleUrl)
    }
}
