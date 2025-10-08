package com.example.rssclipping.ui.navigation

/**
 * Define las rutas de navegación de la aplicación de forma segura.
 * Usar un sealed class asegura que solo podamos navegar a destinos predefinidos.
 */
sealed class Screen(val route: String) {
    object Timeline : Screen("timeline")
    object Subscriptions : Screen("subscriptions")

    // Ruta para el detalle del artículo, que requiere un ID como argumento.
    object ArticleDetail : Screen("article_detail/{articleId}") {
        fun createRoute(articleId: Long) = "article_detail/$articleId"
    }
}
