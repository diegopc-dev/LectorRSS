package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Composable que representa un único elemento en la lista de suscripciones.
 * Muestra el icono, el nombre y los botones de acción para editar y eliminar.
 * @param subscription La entidad de la suscripción a mostrar.
 * @param onEdit La acción a ejecutar cuando se pulsa el botón de editar.
 * @param onDelete La acción a ejecutar cuando se pulsa el botón de eliminar.
 */
@Composable
fun SubscriptionItem(
    subscription: SubscriptionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = subscription.iconUrl,
                    contentDescription = "Icono de la suscripción",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar suscripción")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar suscripción")
            }
        }
    }
}
