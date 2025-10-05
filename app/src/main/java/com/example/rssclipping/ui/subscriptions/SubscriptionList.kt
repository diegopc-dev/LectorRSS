package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Un "Composable" que muestra una lista vertical de suscripciones.
 * Utiliza `LazyColumn` para un rendimiento eficiente.
 *
 * @param subscriptions La lista de suscripciones a mostrar.
 * @param onItemClick La acción a ejecutar cuando se pulsa sobre una suscripción.
 * @param modifier Un [Modifier] opcional para personalizar el layout.
 */
@Composable
fun SubscriptionList(
    subscriptions: List<SubscriptionEntity>,
    onItemClick: (SubscriptionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(subscriptions) { subscription ->
            SubscriptionItem(
                subscription = subscription,
                onClick = { onItemClick(subscription) } // Pasa la entidad completa hacia arriba.
            )
        }
    }
}
