package com.example.rssclipping.data.local.database.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Clase de datos que representa la relación entre un Artículo y su Suscripción.
 * Room utilizará esta clase para unir las dos entidades en una sola consulta.
 */
data class ArticleWithSubscription(
    // Anotación @Embedded para indicar que los campos de ArticleEntity se incluirán
    // directamente en este objeto.
    @Embedded
    val article: ArticleEntity,

    // Anotación @Relation para definir la relación.
    @Relation(
        parentColumn = "subscriptionId", // Columna en la tabla padre (ArticleEntity)
        entityColumn = "id"             // Columna en la tabla hija (SubscriptionEntity)
    )
    val subscription: SubscriptionEntity
)
