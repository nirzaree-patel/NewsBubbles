package com.example.newsbubbles.presentation.bubbles

import com.example.newsbubbles.domain.model.NewsCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BubbleUiModelTest {

    @Test
    fun `all contains one entry per category, in category order`() {
        assertEquals(NewsCategory.entries.map { it }, BubbleUiModel.all.map { it.category })
    }

    @Test
    fun `every category maps to a non-blank icon`() {
        NewsCategory.entries.forEach { category ->
            val uiModel = BubbleUiModel.from(category)
            assertNotNull(uiModel.icon)
            assert(uiModel.icon.isNotBlank()) { "icon for $category should not be blank" }
        }
    }

    @Test
    fun `each category has a distinct color`() {
        val colors = NewsCategory.entries.map { BubbleUiModel.from(it).color }
        assertEquals(colors.size, colors.distinct().size)
    }

    @Test
    fun `from is stable for the same category`() {
        assertEquals(BubbleUiModel.from(NewsCategory.SPORTS), BubbleUiModel.from(NewsCategory.SPORTS))
    }
}
