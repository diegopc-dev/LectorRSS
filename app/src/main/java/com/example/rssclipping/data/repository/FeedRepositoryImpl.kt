package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkArticle
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/**
 * Implementación concreta de [FeedRepository].
 * Orquesta las fuentes de datos para los artículos, aplicando la lógica "offline-first"
 * y manejando los errores de red de forma silenciosa.
 *
 * @param articleDao El DAO para acceder a los datos de artículos en la base de datos local.
 * @param networkDataSource La fuente de datos de red para obtener los feeds RSS.
 */
class FeedRepositoryImpl(
    private val articleDao: ArticleDao,
    private val networkDataSource: RssNetworkDataSource
) : FeedRepository {

    override fun getArticles(subscriptionId: Long): Flow<List<ArticleEntity>> {
        return articleDao.getArticlesBySubscriptionId(subscriptionId)
    }

    override fun getArticle(id: Long): Flow<ArticleEntity?> {
        return articleDao.getById(id)
    }

    override suspend fun syncSubscription(subscriptionUrl: String, subscriptionId: Long) {
        try {
            val networkArticles = networkDataSource.fetchFeed(subscriptionUrl).articles
            val articleEntities = networkArticles.map { it.toEntity(subscriptionId) }
            articleDao.insertAll(articleEntities)
        } catch (e: IOException) {
            // Ignorar errores de red (sin conexión, 404, etc.) para no interrumpir al usuario.
            e.printStackTrace()
        } catch (e: Exception) {
            // Ignorar otras posibles excepciones (ej. durante el parseo del XML) para robustez.
            e.printStackTrace()
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
