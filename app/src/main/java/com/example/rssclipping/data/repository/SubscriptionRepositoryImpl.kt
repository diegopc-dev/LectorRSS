package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [SubscriptionRepository].
 * Su única responsabilidad es actuar como intermediario entre la lógica de negocio
 * y la fuente de datos de suscripciones, en este caso, el DAO.
 *
 * @param subscriptionDao El DAO para acceder a los datos de las suscripciones, inyectado por Hilt.
 */
class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {

    /**
     * Simplemente delega la llamada al método correspondiente del DAO.
     * Aunque parece redundante, este nivel de abstracción es crucial para mantener
     * una arquitectura limpia y desacoplada, permitiendo cambiar la fuente de datos
     * en el futuro (ej. a una API remota) sin afectar las capas superiores.
     */
    override fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAll()
    }
}
