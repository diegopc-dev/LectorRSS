package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.clickable
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

@Composable
fun SubscriptionItem(
    subscription: SubscriptionEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = subscription.name, style = MaterialTheme.typography.titleMedium)
            Text(text = subscription.category, style = MaterialTheme.typography.bodySmall)
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
            category = "Tecnología"
        ),
        onClick = {} 
    )
}
