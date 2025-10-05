package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Un "Composable" que muestra una lista vertical de suscripciones.
 * Utiliza `LazyColumn` para un rendimiento eficiente, ya que solo compone y dibuja
 * los elementos que son visibles en la pantalla.
 *
 * @param subscriptions La lista de suscripciones a mostrar.
 * @param modifier Un [Modifier] opcional para personalizar el layout.
 */
@Composable
fun SubscriptionList(
    subscriptions: List<SubscriptionEntity>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        // `items` es una función de extensión de `LazyListScope` que permite
        // construir la lista de forma perezosa a partir de una lista de datos.
        items(subscriptions) {
            // Para cada entidad en la lista, se dibuja un `SubscriptionItem`.
            SubscriptionItem(subscription = it)
        }
    }
}
