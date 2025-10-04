package com.example.rssclipping.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rssclipping.data.local.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad [ArticleEntity].
 * Define los métodos para interactuar con la tabla 'articles' en la base de datos.
 */
@Dao
interface ArticleDao {
    /**
     * Inserta una lista de artículos en la base de datos.
     * Si un artículo ya existe (basado en el índice único 'guid'), será reemplazado
     * gracias a la estrategia OnConflictStrategy.REPLACE.
     * @param articles La lista de entidades de artículo a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    /**
     * Recupera todos los artículos asociados a un ID de suscripción específico.
     * @param subscriptionId El ID de la suscripción padre.
     * @return Un Flow que emite la lista de artículos para la suscripción dada.
     */
    @Query("SELECT * FROM articles WHERE subscriptionId = :subscriptionId ORDER BY pubDate DESC")
    fun getArticlesBySubscriptionId(subscriptionId: Long): Flow<List<ArticleEntity>>

    /**
     * Elimina todos los artículos asociados a un ID de suscripción específico.
     * @param subscriptionId El ID de la suscripción cuyos artículos se eliminarán.
     */
    @Query("DELETE FROM articles WHERE subscriptionId = :subscriptionId")
    suspend fun deleteAllForSubscription(subscriptionId: Long)

    /**
     * Recupera un artículo específico por su ID.
     * @param id El ID del artículo a buscar.
     * @return La [ArticleEntity] correspondiente, o null si no se encuentra.
     */
    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getById(id: Long): ArticleEntity?
}
