package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona los datos de los artículos de los feeds, actuando como única fuente de verdad.
 * Orquesta las operaciones entre la fuente de datos de red (`RssNetworkDataSource`) y la local (`ArticleDao`).
 * Su objetivo es abstraer a las capas superiores (ViewModels) de la complejidad de la obtención de datos.
 */
interface FeedRepository {

    /**
     * Obtiene un Flow con todos los artículos de una suscripción directamente desde la base de datos.
     * Sigue el principio de "offline-first", devolviendo inmediatamente los datos cacheados.
     * No se encarga de la sincronización; esa es una responsabilidad separada.
     *
     * @param subscriptionId El ID de la suscripción cuyos artículos se quieren obtener.
     * @return Un Flow que emite la lista de artículos de la base de datos cada vez que cambian.
     */
    fun getArticles(subscriptionId: Long): Flow<List<ArticleEntity>>

    /**
     * Fuerza la sincronización de una suscripción. Esta operación obtiene los últimos artículos desde la red
     * y los guarda en la base de datos local. La UI, al estar observando el Flow de `getArticles`,
     * se actualizará automáticamente cuando los nuevos datos se inserten.
     * Es una función `suspend` porque realiza operaciones de red.
     *
     * @param subscriptionUrl La URL del feed RSS a sincronizar.
     * @param subscriptionId El ID de la suscripción a la que pertenecen los nuevos artículos.
     */
    suspend fun syncSubscription(subscriptionUrl: String, subscriptionId: Long)

}
