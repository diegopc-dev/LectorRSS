package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [SubscriptionRepository].
 * Orquesta las fuentes de datos para las suscripciones, obteniendo los metadatos del feed
 * desde la red para crear la entidad en la base de datos.
 *
 * @param subscriptionDao El DAO para acceder a los datos de suscripciones en la base de datos local.
 * @param networkDataSource La fuente de datos de red para obtener los feeds RSS.
 */
class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao,
    private val networkDataSource: RssNetworkDataSource
) : SubscriptionRepository {

    override fun getAll(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAll()
    }

    override suspend fun addSubscription(url: String): Long {
        // Intenta obtener los metadatos del feed desde la red.
        val feed = try {
            networkDataSource.fetchFeed(url)
        } catch (_: Exception) {
            // Si falla la petición (ej. URL incorrecta, sin conexión), se devuelve null.
            null
        }

        // Crea la nueva entidad. Si el título del feed está vacío, usa la URL como nombre.
        val newSubscription = SubscriptionEntity(
            url = url,
            name = feed?.channelTitle?.ifBlank { url } ?: url,
            iconUrl = feed?.channelIconUrl ?: "",
            category = "General"
        )
        
        // Inserta la nueva suscripción en la base de datos y devuelve su ID.
        return subscriptionDao.insert(newSubscription)
    }

    override suspend fun updateSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.update(subscription)
    }

    override suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.delete(subscription)
    }
}
