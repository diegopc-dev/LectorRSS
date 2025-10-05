package com.example.rssclipping.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Define la estructura de la tabla 'subscriptions' en la base de datos Room.
 * Cada instancia de esta clase representa una suscripción a un feed RSS.
 */
@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    /**
     * Clave primaria autoincremental para identificar de forma única cada suscripción.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * La URL del feed RSS a la que el usuario está suscrito.
     */
    val url: String,

    /**
     * Un nombre personalizado o título para la suscripción, facilitando su identificación.
     */
    val name: String,

    /**
     * La URL del icono o favicon del feed.
     */
    val iconUrl: String,

    /**
     * Categoría asignada por el usuario para organizar las suscripciones.
     */
    val category: String
)
