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
    version = 2, // Se ha incrementado la versión de 1 a 2
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun articleDao(): ArticleDao

    companion object {
        /**
         * Define la migración de la base de datos de la versión 1 a la 2.
         * En este caso, solo necesitamos añadir la nueva columna 'content' a la tabla 'articles'.
         */
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE articles ADD COLUMN content TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
