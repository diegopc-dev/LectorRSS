package com.example.rssclipping.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

@Database(
    entities = [SubscriptionEntity::class, ArticleEntity::class],
    version = 3, // Se ha incrementado la versión de 2 a 3
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun articleDao(): ArticleDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE articles ADD COLUMN content TEXT NOT NULL DEFAULT ''")
            }
        }

        /**
         * Define la migración de la base de datos de la versión 2 a la 3.
         * En este caso, añadimos la nueva columna 'iconUrl' a la tabla 'subscriptions'.
         */
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE subscriptions ADD COLUMN iconUrl TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
