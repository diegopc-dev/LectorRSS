package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona los datos de las suscripciones, actuando como única fuente de verdad
 * para la información de las suscripciones del usuario. Abstrae el origen de los datos
 * (red, base de datos) de las capas superiores de la aplicación (ViewModels).
 */
interface SubscriptionRepository {
    /**
     * Obtiene un [Flow] con la lista de todas las suscripciones guardadas en la base de datos local.
     * El uso de Flow permite que las capas superiores (ViewModels) observen los cambios
     * en las suscripciones de forma reactiva y actualicen la UI automáticamente.
     *
     * @return Un [Flow] que emite la lista actualizada de [SubscriptionEntity] cada vez que los datos cambian.
     */
    fun getAll(): Flow<List<SubscriptionEntity>>

    /**
     * Añade una nueva suscripción a la base de datos a partir de una URL.
     * Esta operación realiza una llamada de red para obtener los metadatos del feed (título, icono)
     * antes de crear la entidad y guardarla en la base de datos.
     *
     * @param url La URL del feed RSS a añadir.
     * @return El ID de la suscripción recién creada.
     */
    suspend fun addSubscription(url: String): Long

    /**
     * Actualiza una suscripción existente en la base de datos.
     * @param subscription La entidad [SubscriptionEntity] con los datos modificados.
     */
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    /**
     * Elimina una suscripción de la base de datos.
     * @param subscription La entidad [SubscriptionEntity] a eliminar.
     */
    suspend fun deleteSubscription(subscription: SubscriptionEntity)
}
