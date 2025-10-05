package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rssclipping.navigation.Screen

/**
 * Composable que representa la pantalla principal de "Mis Suscripciones".
 * Muestra una lista de las suscripciones del usuario y un botón flotante para añadir nuevas.
 *
 * @param viewModel El [SubscriptionsViewModel] que proporciona el estado de la pantalla (la lista de suscripciones).
 * @param navController El controlador de navegación para manejar las acciones de la UI, como ir a la pantalla de añadir suscripción o a la de artículos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: SubscriptionsViewModel = hiltViewModel(),
    navController: NavController
) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Suscripciones") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddSubscription.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir suscripción")
            }
        }
    ) { innerPadding ->
        if (subscriptions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay suscripciones. Añade una con el botón +.")
            }
        } else {
            SubscriptionList(
                subscriptions = subscriptions,
                onItemClick = { subscription ->
                    // Navega a la pantalla de artículos para la suscripción seleccionada.
                    navController.navigate(Screen.Articles.createRoute(subscription.id))
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
