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

/**
 * El "Composable" principal que representa la pantalla de gestión de suscripciones.
 * Utiliza un Scaffold para proporcionar una estructura de Material Design (TopAppBar, FAB).
 *
 * @param viewModel El [SubscriptionsViewModel] que proporciona el estado de la pantalla. Hilt se encarga
 * de inyectarlo automáticamente gracias a `hiltViewModel()`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Suscripciones") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navegar a la pantalla de añadir suscripción */ }) {
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
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
