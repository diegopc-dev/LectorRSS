package com.example.rssclipping.navigation

const val KEY_SUBSCRIPTION_ID = "subscriptionId"

/**
 * Define las rutas de navegación de la aplicación de forma segura (type-safe).
 * Cada objeto representa una pantalla o destino de navegación.
 */
sealed class Screen(val route: String) {
    object Subscriptions : Screen("subscriptions_screen")
    object AddSubscription : Screen("add_subscription_screen")
    object Articles : Screen("articles_screen/{$KEY_SUBSCRIPTION_ID}") {
        fun createRoute(subscriptionId: Long) = "articles_screen/$subscriptionId"
    }
}
