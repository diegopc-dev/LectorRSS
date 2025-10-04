package com.example.rssclipping.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * La clase principal de la base de datos de la aplicación, que hereda de RoomDatabase.
 * Es el punto de acceso central a los datos persistentes.
 */
@Database(
    // Lista de todas las entidades (tablas) que pertenecen a esta base de datos.
    entities = [SubscriptionEntity::class, ArticleEntity::class],
    // La versión actual de la base de datos. Debe incrementarse en cada cambio de esquema.
    version = 1,
    // Indica a Room que debe exportar el esquema de la base de datos a un fichero JSON.
    // Esto es esencial para las pruebas de migración.
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona una instancia del DAO para las suscripciones.
     * Room se encarga de generar la implementación concreta de este método.
     */
    abstract fun subscriptionDao(): SubscriptionDao

    /**
     * Proporciona una instancia del DAO para los artículos.
     * Room se encarga de generar la implementación concreta de este método.
     */
    abstract fun articleDao(): ArticleDao
}
