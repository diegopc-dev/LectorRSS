package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkArticle
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/**
 * Implementación concreta de [FeedRepository].
 * Esta clase es el corazón de la capa de datos, orquestando las fuentes de datos
 * y aplicando la lógica "offline-first".
 *
 * @param articleDao El DAO para acceder a los datos de los artículos en la base de datos local, inyectado por Hilt.
 * @param networkDataSource La fuente de datos de red, inyectada por Hilt.
 */
class FeedRepositoryImpl(
    private val articleDao: ArticleDao,
    private val networkDataSource: RssNetworkDataSource
) : FeedRepository {

    /**
     * Devuelve un Flow directamente desde el DAO. Esto asegura que la UI siempre muestra
     * los datos de la base de datos local y se actualiza automáticamente cuando estos cambian.
     */
    override fun getArticles(subscriptionId: Long): Flow<List<ArticleEntity>> {
        return articleDao.getArticlesBySubscriptionId(subscriptionId)
    }

    /**
     * Realiza la sincronización con la red. Su lógica es obtener los datos de la red y guardarlos
     * en la base de datos, manejando los errores de forma silenciosa para no crashear la aplicación.
     */
    override suspend fun syncSubscription(subscriptionUrl: String, subscriptionId: Long) {
        try {
            // 1. Obtener los artículos de la red.
            val networkArticles = networkDataSource.fetchArticles(subscriptionUrl)

            // 2. Mapear los artículos de red a entidades de base de datos.
            val articleEntities = networkArticles.map { it.toEntity(subscriptionId) }

            // 3. Insertar las nuevas entidades en la base de datos.
            // Room se encargará de reemplazar los artículos existentes gracias a OnConflictStrategy.REPLACE.
            articleDao.insertAll(articleEntities)
        } catch (e: IOException) {
            // Ignorar errores de red (sin conexión, 404, etc.). El repositorio no debe
            // propagar estos errores. La UI puede mostrar un mensaje de error si lo desea,
            // pero la app no debe crashear.
            e.printStackTrace() // Loguear el error para depuración.
        } catch (e: Exception) {
            // Capturar otras posibles excepciones (ej. durante el parseo del XML) para robustez.
            e.printStackTrace()
        }
    }
}

/**
 * Función de extensión privada para mapear un [NetworkArticle] (modelo de red) a una [ArticleEntity] (modelo de base de datos).
 * Esto mantiene la lógica de mapeo encapsulada y el código del repositorio más limpio.
 */
private fun NetworkArticle.toEntity(subscriptionId: Long) = ArticleEntity(
    subscriptionId = subscriptionId,
    guid = guid,
    title = title,
    link = link,
    pubDate = pubDate
)
