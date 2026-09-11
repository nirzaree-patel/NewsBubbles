package com.example.newsbubbles.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newsbubbles.presentation.articles.ArticleListScreen
import com.example.newsbubbles.presentation.bubbles.BubblesScreen

private object Routes {
    const val BUBBLES = "bubbles"
    const val ARTICLES = "articles/{category}"
    fun articles(categoryApiValue: String) = "articles/$categoryApiValue"
}

@Composable
fun NewsNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.BUBBLES) {

        composable(Routes.BUBBLES) {
            BubblesScreen(
                onCategoryTapped = { category ->
                    navController.navigate(Routes.articles(category.apiValue))
                }
            )
        }

        composable(
            route = Routes.ARTICLES,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryValue = backStackEntry.arguments?.getString("category") ?: return@composable
            ArticleListScreen(
                categoryApiValue = categoryValue,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
