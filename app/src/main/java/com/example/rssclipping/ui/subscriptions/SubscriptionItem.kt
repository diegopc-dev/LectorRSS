package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Composable que representa un único elemento en la lista de suscripciones.
 * Muestra el icono del feed, su nombre y su categoría.
 *
 * @param subscription La entidad [SubscriptionEntity] que contiene los datos a mostrar.
 * @param onClick La acción a ejecutar cuando el usuario pulsa sobre la tarjeta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionItem(
    subscription: SubscriptionEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val placeholder = rememberVectorPainter(image = Icons.Default.RssFeed)
            AsyncImage(
                model = subscription.iconUrl,
                contentDescription = "Icono del feed",
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier.size(40.dp)
            )
            Column {
                Text(text = subscription.name, style = MaterialTheme.typography.titleMedium)
                Text(text = subscription.category, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionItemPreview() {
    SubscriptionItem(
        subscription = SubscriptionEntity(
            id = 1,
            name = "Mi Blog Favorito",
            url = "https://ejemplo.com",
            iconUrl = "", // En la preview, no hay icono
            category = "Tecnología"
        ),
        onClick = {} 
    )
}
