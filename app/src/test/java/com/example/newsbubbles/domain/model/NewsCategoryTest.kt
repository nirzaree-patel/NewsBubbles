package com.example.newsbubbles.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class NewsCategoryTest {

    @Test
    fun `has exactly seven categories`() {
        assertEquals(7, NewsCategory.entries.size)
    }

    @Test
    fun `fromApiValue resolves every known category`() {
        NewsCategory.entries.forEach { category ->
            assertEquals(category, NewsCategory.fromApiValue(category.apiValue))
        }
    }

    @Test
    fun `fromApiValue falls back to GENERAL for unknown values`() {
        assertEquals(NewsCategory.GENERAL, NewsCategory.fromApiValue("unknown-category"))
        assertEquals(NewsCategory.GENERAL, NewsCategory.fromApiValue(""))
    }
}
