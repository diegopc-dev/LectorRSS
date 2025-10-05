package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [SubscriptionRepository].
 * Orquesta las operaciones entre la red y la base de datos local para las suscripciones.
 *
 * @param subscriptionDao El DAO para acceder a los datos de las suscripciones, inyectado por Hilt.
 * @param networkDataSource La fuente de datos de red, para obtener información de los feeds.
 */
class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao,
    private val networkDataSource: RssNetworkDataSource
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAll()
    }

    override suspend fun addSubscription(url: String) {
        // Por ahora, como no tenemos una forma de obtener solo el título del feed,
        // y para mantenerlo simple, usaremos la URL como nombre.
        // En el futuro, podríamos mejorar esto para obtener el título real del feed.
        val name = try {
            // Intenta obtener los artículos solo para extraer el título del feed.
            // Esta no es la forma más eficiente, pero funciona para nuestro MVP.
            val articles = networkDataSource.fetchArticles(url)
            // Asumimos que el título del feed es el título del primer artículo, o la URL si no hay artículos.
            articles.firstOrNull()?.title ?: url
        } catch (e: Exception) {
            url // Si falla la red, usa la URL como nombre.
        }

        val newSubscription = SubscriptionEntity(
            url = url,
            name = name,
            category = "General" // Categoría por defecto.
        )
        subscriptionDao.insert(newSubscription)
    }
}
