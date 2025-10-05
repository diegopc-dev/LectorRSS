package com.example.rssclipping.navigation

/**
 * Define las rutas de navegación de la aplicación de forma segura (type-safe).
 * Cada objeto representa una pantalla o destino de navegación.
 */
sealed class Screen(val route: String) {
    object Subscriptions : Screen("subscriptions_screen")
    object AddSubscription : Screen("add_subscription_screen")
}
