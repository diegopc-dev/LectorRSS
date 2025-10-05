package com.example.rssclipping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rssclipping.navigation.Screen
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
                        AddSubscriptionScreen()
                    }
                }
            }
        }
    }
}
