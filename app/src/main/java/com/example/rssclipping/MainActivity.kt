package com.example.rssclipping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rssclipping.navigation.KEY_ARTICLE_ID
import com.example.rssclipping.navigation.KEY_SUBSCRIPTION_ID
import com.example.rssclipping.navigation.Screen
import com.example.rssclipping.ui.articles.ArticleDetailScreen
import com.example.rssclipping.ui.articles.ArticlesScreen
import com.example.rssclipping.ui.subscriptions.AddSubscriptionScreen
import com.example.rssclipping.ui.subscriptions.SubscriptionsScreen
import com.example.rssclipping.ui.theme.RSSClippingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RSSClippingTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Subscriptions.route
                ) {
                    composable(Screen.Subscriptions.route) {
                        SubscriptionsScreen(navController = navController)
                    }
                    composable(Screen.AddSubscription.route) {
                        AddSubscriptionScreen(navController = navController)
                    }
                    composable(
                        route = Screen.Articles.route,
                        arguments = listOf(navArgument(KEY_SUBSCRIPTION_ID) { type = NavType.LongType })
                    ) {
                        ArticlesScreen(navController = navController)
                    }
                    composable(
                        route = Screen.ArticleDetail.route,
                        arguments = listOf(navArgument(KEY_ARTICLE_ID) { type = NavType.LongType })
                    ) {
                        ArticleDetailScreen(navController = navController)
                    }
                }
            }
        }
    }
}
