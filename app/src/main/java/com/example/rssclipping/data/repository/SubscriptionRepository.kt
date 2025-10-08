package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona los datos de las suscripciones, actuando como única fuente de verdad
 * para los datos de suscripción de la aplicación. Abstrae el origen de los datos (en este caso, Room).
 */
interface SubscriptionRepository {
    /**
     * Obtiene un Flow con todas las suscripciones guardadas en la base de datos.
     * El uso de Flow permite que las capas superiores (ViewModels) observen los cambios
     * en las suscripciones de forma reactiva y actualicen la UI automáticamente.
     *
     * @return Un Flow que emite la lista actualizada de todas las suscripciones cada vez que cambian.
     */
    fun getAll(): Flow<List<SubscriptionEntity>>

    /**
     * Añade una nueva suscripción a la base de datos a partir de una URL.
     *
     * @param url La URL del feed RSS a añadir.
     * @return El ID de la suscripción recién creada.
     */
    suspend fun addSubscription(url: String): Long

    /**
     * Actualiza una suscripción existente en la base de datos.
     * @param subscription La suscripción con los datos modificados.
     */
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    /**
     * Elimina una suscripción de la base de datos.
     * @param subscription La suscripción a eliminar.
     */
    suspend fun deleteSubscription(subscription: SubscriptionEntity)
}
