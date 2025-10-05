package com.example.rssclipping.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Clase principal de la base de datos de la aplicación, que hereda de RoomDatabase.
 * Es el punto de acceso central a los datos persistentes de la app.
 *
 * @property subscriptionDao Proporciona acceso a las operaciones de la tabla de suscripciones.
 * @property articleDao Proporciona acceso a las operaciones de la tabla de artículos.
 */
@Database(
    entities = [SubscriptionEntity::class, ArticleEntity::class],
    version = 4,
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

    /**
     * Contenedor para los objetos de migración de la base de datos.
     */
    companion object {
        /**
         * Migración de la base de datos de la versión 1 a la 2.
         * Añade la columna 'content' a la tabla 'articles' para almacenar el cuerpo del artículo.
         */
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE articles ADD COLUMN content TEXT NOT NULL DEFAULT ''")
            }
        }

        /**
         * Migración de la base de datos de la versión 2 a la 3.
         * Añade la columna 'iconUrl' a la tabla 'subscriptions' para almacenar el icono del feed.
         */
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE subscriptions ADD COLUMN iconUrl TEXT NOT NULL DEFAULT ''")
            }
        }

        /**
         * Migración de la base de datos de la versión 3 a la 4.
         * Añade la columna 'thumbnailUrl' a la tabla 'articles' para almacenar la miniatura del artículo.
         */
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE articles ADD COLUMN thumbnailUrl TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
