package com.example.rssclipping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rssclipping.ui.articles.ArticleDetailScreen
import com.example.rssclipping.ui.timeline.TimelineScreen
import com.example.rssclipping.ui.subscriptions.SubscriptionsScreen

/**
 * Define el grafo de navegación principal de la aplicación.
 * Utiliza un NavHost para asociar cada ruta (definida en la clase Screen) con su Composable correspondiente.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Timeline.route) {
        // Ruta para la pantalla principal (Timeline)
        composable(Screen.Timeline.route) {
            TimelineScreen(navController = navController)
        }

        // Ruta para la pantalla de gestión de suscripciones
        composable(Screen.Subscriptions.route) {
            SubscriptionsScreen(navController = navController)
        }

        // Ruta para la pantalla de detalle del artículo
        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(navArgument("articleId") { type = NavType.LongType })
        ) {
            // No necesitamos pasar el ID al ViewModel, ya que Hilt lo gestiona automáticamente
            // a través del SavedStateHandle.
            ArticleDetailScreen(navController = navController)
        }
    }
}
