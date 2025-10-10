package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona los datos de los artículos, actuando como única fuente de verdad
 * para la información de los feeds. Abstrae el origen de los datos de las capas superiores.
 */
interface FeedRepository {

    /**
     * Obtiene un [Flow] con la lista de todos los artículos de todas las suscripciones
     * desde la base de datos local.
     *
     * @return Un [Flow] que emite la lista completa de [ArticleEntity] cada vez que los datos cambian.
     */
    fun getAllArticles(): Flow<List<ArticleEntity>>

    /**
     * Obtiene un [Flow] con los artículos de una suscripción específica desde la base de datos local.
     *
     * @param subscriptionId El ID de la suscripción cuyos artículos se quieren obtener.
     * @return Un [Flow] que emite la lista de [ArticleEntity] para la suscripción dada.
     */
    fun getArticles(subscriptionId: Long): Flow<List<ArticleEntity>>

    /**
     * Obtiene un [Flow] con un único artículo por su ID desde la base de datos local.
     *
     * @param id El ID del artículo a obtener.
     * @return Un [Flow] que emite la [ArticleEntity] o null si no se encuentra.
     */
    fun getArticle(id: Long): Flow<ArticleEntity?>

    /**
     * Fuerza la sincronización de una suscripción individual. Esta operación obtiene los últimos
     * artículos desde la red y los guarda en la base de datos local. La UI, al estar observando
     * los Flows de `getArticles` o `getAllArticles`, se actualizará automáticamente.
     *
     * @param subscriptionUrl La URL del feed RSS a sincronizar.
     * @param subscriptionId El ID de la suscripción a la que pertenecen los nuevos artículos.
     */
    suspend fun syncSubscription(subscriptionUrl: String, subscriptionId: Long)

    /**
     * Fuerza la sincronización de TODAS las suscripciones del usuario.
     * Itera sobre todas las suscripciones guardadas y llama a [syncSubscription] para cada una.
     */
    suspend fun syncAllSubscriptions()

}
