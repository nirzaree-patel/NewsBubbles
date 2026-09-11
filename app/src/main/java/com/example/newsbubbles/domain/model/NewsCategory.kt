package com.example.newsbubbles.domain.model

/**
 * Domain model for the seven fixed News API categories.
 * Pure Kotlin — no Android or Compose imports.
 */
enum class NewsCategory(
    val displayName: String,
    val apiValue: String
) {
    BUSINESS(displayName = "Business", apiValue = "business"),
    ENTERTAINMENT(displayName = "Entertainment", apiValue = "entertainment"),
    GENERAL(displayName = "General", apiValue = "general"),
    HEALTH(displayName = "Health", apiValue = "health"),
    SCIENCE(displayName = "Science", apiValue = "science"),
    SPORTS(displayName = "Sports", apiValue = "sports"),
    TECHNOLOGY(displayName = "Technology", apiValue = "technology");

    companion object {
        fun fromApiValue(value: String): NewsCategory =
            entries.find { it.apiValue == value } ?: GENERAL
    }
}
