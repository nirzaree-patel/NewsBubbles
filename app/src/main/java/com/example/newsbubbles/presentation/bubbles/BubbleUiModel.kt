package com.example.newsbubbles.presentation.bubbles

import androidx.compose.ui.graphics.Color
import com.example.newsbubbles.domain.model.NewsCategory

/**
 * Presentation-level metadata for a category bubble.
 * Keeps UI concerns (Color, emoji icon) out of the domain layer.
 */
data class BubbleUiModel(
    val category: NewsCategory,
    val color: Color,
    val icon: String
) {
    companion object {
        fun from(category: NewsCategory): BubbleUiModel = BubbleUiModel(
            category = category,
            color = when (category) {
                NewsCategory.BUSINESS     -> Color(0xFF1565C0)
                NewsCategory.ENTERTAINMENT-> Color(0xFF6A1B9A)
                NewsCategory.GENERAL      -> Color(0xFF00695C)
                NewsCategory.HEALTH       -> Color(0xFFC62828)
                NewsCategory.SCIENCE      -> Color(0xFF2E7D32)
                NewsCategory.SPORTS       -> Color(0xFFE65100)
                NewsCategory.TECHNOLOGY   -> Color(0xFF00838F)
            },
            icon = when (category) {
                NewsCategory.BUSINESS     -> "💼"
                NewsCategory.ENTERTAINMENT-> "🎬"
                NewsCategory.GENERAL      -> "📰"
                NewsCategory.HEALTH       -> "❤️"
                NewsCategory.SCIENCE      -> "🔬"
                NewsCategory.SPORTS       -> "🏆"
                NewsCategory.TECHNOLOGY   -> "💻"
            }
        )

        val all: List<BubbleUiModel> =
            NewsCategory.entries.map { from(it) }
    }
}
