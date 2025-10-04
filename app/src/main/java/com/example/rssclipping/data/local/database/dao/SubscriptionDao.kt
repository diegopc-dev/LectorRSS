package com.example.rssclipping.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad [SubscriptionEntity].
 * Define los métodos para interactuar con la tabla 'subscriptions' en la base de datos.
 */
@Dao
interface SubscriptionDao {
    /**
     * Inserta una nueva suscripción en la base de datos.
     * Si la suscripción ya existe (basado en su clave primaria), será reemplazada
     * gracias a la estrategia OnConflictStrategy.REPLACE.
     * @param subscription La entidad de suscripción a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity)

    /**
     * Recupera todas las suscripciones de la base de datos, ordenadas por nombre.
     * @return Un Flow que emite la lista de todas las suscripciones cada vez que los datos cambian.
     */
    @Query("SELECT * FROM subscriptions ORDER BY name ASC")
    fun getAll(): Flow<List<SubscriptionEntity>>

    /**
     * Elimina una suscripción de la base de datos.
     * @param subscription La entidad de suscripción a eliminar. Room la identifica por su clave primaria.
     */
    @Delete
    suspend fun delete(subscription: SubscriptionEntity)

    /**
     * Recupera una suscripción específica por su ID.
     * @param id El ID de la suscripción a buscar.
     * @return La [SubscriptionEntity] correspondiente, o null si no se encuentra.
     */
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): SubscriptionEntity?
}
