package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException

/**
 * Implementación concreta de [FeedRepository].
 * Orquesta las fuentes de datos para los artículos, aplicando la lógica "offline-first"
 * y manejando los errores de red de forma silenciosa.
 *
 * @param articleDao El DAO para acceder a los datos de artículos en la base de datos local.
 * @param networkDataSource La fuente de datos de red para obtener los feeds RSS.
 * @param subscriptionRepository El repositorio de suscripciones para obtener la lista de todas las suscripciones.
 */
class FeedRepositoryImpl(
    private val articleDao: ArticleDao,
    private val networkDataSource: RssNetworkDataSource,
    private val subscriptionRepository: SubscriptionRepository
) : FeedRepository {

    override fun getAllArticles(): Flow<List<ArticleEntity>> {
        return articleDao.getAllArticles()
    }

    override fun getArticles(subscriptionId: Long): Flow<List<ArticleEntity>> {
        return articleDao.getArticlesBySubscriptionId(subscriptionId)
    }

    override fun getArticle(id: Long): Flow<ArticleEntity?> {
        return articleDao.getById(id)
    }

    override suspend fun syncSubscription(subscriptionUrl: String, subscriptionId: Long) {
        try {
            // 1. Obtiene los artículos de la red.
            val networkArticles = networkDataSource.fetchFeed(subscriptionUrl).articles
            // 2. Mapea los artículos de red a entidades de base de datos.
            val articleEntities = networkArticles.map { it.toEntity(subscriptionId) }
            // 3. Inserta las nuevas entidades en la base de datos.
            articleDao.insertAll(articleEntities)
        } catch (e: IOException) {
            // Ignorar errores de red (sin conexión, 404, etc.) para no interrumpir al usuario.
            // En una app de producción, aquí se podría registrar el error en un sistema de logging.
            e.printStackTrace()
        } catch (e: Exception) {
            // Ignorar otras posibles excepciones (ej. durante el parseo del XML) para robustez.
            e.printStackTrace()
        }
    }

    override suspend fun syncAllSubscriptions() {
        // Obtiene la lista actual de suscripciones desde el repositorio. Usa .first() para obtener
        // solo el valor más reciente del Flow y no quedarse esperando futuras emisiones.
        val subscriptions = subscriptionRepository.getAll().first()
        // Itera sobre cada una y lanza su sincronización individual.
        subscriptions.forEach { subscription ->
            syncSubscription(subscription.url, subscription.id)
        }
    }
}

/**
 * Función de extensión privada para mapear un [NetworkArticle] (modelo de red)
 * a una [ArticleEntity] (modelo de base de datos).
 * Esto mantiene la lógica de mapeo encapsulada y el código del repositorio más limpio.
 *
 * @param subscriptionId El ID de la suscripción a la que pertenece este artículo.
 * @return Una instancia de [ArticleEntity] lista para ser insertada en la base de datos.
 */
private fun NetworkArticle.toEntity(subscriptionId: Long) = ArticleEntity(
    subscriptionId = subscriptionId,
    guid = guid,
    title = title,
    link = link,
    pubDate = pubDate,
    content = content,
    thumbnailUrl = thumbnailUrl
)
