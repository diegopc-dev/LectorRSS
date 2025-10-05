package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * El "Composable" principal que representa la pantalla de gestión de suscripciones.
 * Es el punto de entrada de la UI para esta feature.
 *
 * @param viewModel El [SubscriptionsViewModel] que proporciona el estado de la pantalla. Hilt se encarga
 * de inyectarlo automáticamente gracias a `hiltViewModel()`.
 */
@Composable
fun SubscriptionsScreen(
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    // Observa el StateFlow del ViewModel. `collectAsStateWithLifecycle` se encarga de
    // recoger el flujo de forma segura, respetando el ciclo de vida del Composable.
    // Cada vez que el Flow emita un nuevo valor, este Composable se recompondrá.
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()

    // Lógica de UI: decide qué mostrar basándose en el estado actual.
    if (subscriptions.isEmpty()) {
        // Si la lista está vacía, muestra un mensaje centrado.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay suscripciones.")
        }
    } else {
        // Si hay datos, se los pasa al Composable que se encarga de dibujar la lista.
        SubscriptionList(subscriptions = subscriptions)
    }
}
