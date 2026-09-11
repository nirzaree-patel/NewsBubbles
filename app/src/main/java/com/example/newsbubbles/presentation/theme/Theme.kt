package com.example.newsbubbles.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D1B2A),
    background = Color(0xFF0D1B2A),
    onBackground = Color(0xFFE3F2FD),
    surface = Color(0xFF1A2744),
    onSurface = Color(0xFFE3F2FD)
)

@Composable
fun NewsBubblesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
