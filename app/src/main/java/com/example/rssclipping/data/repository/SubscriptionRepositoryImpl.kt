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

    override fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAll()
    }

    override suspend fun addSubscription(url: String): Long {
        val feed = try {
            networkDataSource.fetchFeed(url)
        } catch (_: Exception) {
            null
        }

        val newSubscription = SubscriptionEntity(
            url = url,
            name = feed?.channelTitle?.ifBlank { url } ?: url,
            iconUrl = feed?.channelIconUrl ?: "",
            category = "General"
        )
        
        return subscriptionDao.insert(newSubscription)
    }
}
