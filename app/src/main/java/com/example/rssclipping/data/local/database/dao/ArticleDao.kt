package com.example.rssclipping.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.ArticleWithSubscription
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad [ArticleEntity].
 * Define los métodos para interactuar con la tabla 'articles' en la base de datos.
 */
@Dao
interface ArticleDao {
    /**
     * Inserta una lista de artículos en la base de datos.
     * Si un artículo ya existe (basado en el índice único 'guid'), será reemplazado.
     * @param articles La lista de entidades de artículo a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    /**
     * Recupera todos los artículos con su información de suscripción, ordenados por fecha descendente.
     * @return Un Flow que emite la lista completa de artículos con sus suscripciones.
     */
    @Transaction
    @Query("SELECT * FROM articles ORDER BY pubDate DESC")
    fun getAllArticles(): Flow<List<ArticleWithSubscription>>

    /**
     * Recupera todos los artículos de una suscripción específica, con su información de suscripción.
     * @param subscriptionId El ID de la suscripción padre.
     * @return Un Flow que emite la lista de artículos para la suscripción dada.
     */
    @Transaction
    @Query("SELECT * FROM articles WHERE subscriptionId = :subscriptionId ORDER BY pubDate DESC")
    fun getArticlesBySubscriptionId(subscriptionId: Long): Flow<List<ArticleWithSubscription>>

    /**
     * Elimina todos los artículos asociados a un ID de suscripción específico.
     * @param subscriptionId El ID de la suscripción cuyos artículos se eliminarán.
     */
    @Query("DELETE FROM articles WHERE subscriptionId = :subscriptionId")
    suspend fun deleteAllForSubscription(subscriptionId: Long)

    /**
     * Recupera un artículo específico por su ID, con su información de suscripción.
     * @param id El ID del artículo a buscar.
     * @return Un Flow que emite el [ArticleWithSubscription] correspondiente, o null si no se encuentra.
     */
    @Transaction
    @Query("SELECT * FROM articles WHERE id = :id")
    fun getById(id: Long): Flow<ArticleWithSubscription?>
}
