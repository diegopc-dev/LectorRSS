package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Un "Composable" que representa la vista de un único elemento en la lista de suscripciones.
 * Recibe el dato y se encarga únicamente de su representación visual.
 *
 * @param subscription La entidad de suscripción a mostrar.
 */
@Composable
fun SubscriptionItem(subscription: SubscriptionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = subscription.name, style = MaterialTheme.typography.titleMedium)
            Text(text = subscription.category, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * La anotación @Preview permite a Android Studio renderizar este Composable en el panel de diseño,
 * facilitando el desarrollo visual rápido sin necesidad de ejecutar la app en un emulador.
 */
@Preview(showBackground = true)
@Composable
fun SubscriptionItemPreview() {
    SubscriptionItem(
        SubscriptionEntity(
            id = 1,
            name = "Mi Blog Favorito",
            url = "https://ejemplo.com",
            category = "Tecnología"
        )
    )
}
