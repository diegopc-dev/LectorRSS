package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import kotlinx.coroutines.flow.Flow

class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao,
    private val networkDataSource: RssNetworkDataSource
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAll()
    }

    override suspend fun addSubscription(url: String): Long {
        val name = try {
            // Obtenemos el feed completo para usar su título
            val feed = networkDataSource.fetchFeed(url)
            feed.channelTitle.ifBlank { url } // Si el título del canal está vacío, usamos la URL
        } catch (e: Exception) {
            url // Si la red falla, usamos la URL como nombre
        }

        val newSubscription = SubscriptionEntity(
            url = url,
            name = name,
            category = "General"
        )
        
        return subscriptionDao.insert(newSubscription)
    }
}
