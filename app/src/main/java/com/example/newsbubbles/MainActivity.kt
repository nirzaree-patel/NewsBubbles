package com.example.newsbubbles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.newsbubbles.presentation.navigation.NewsNavGraph
import com.example.newsbubbles.presentation.theme.NewsBubblesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsBubblesTheme {
                NewsNavGraph()
            }
        }
    }
}
