package com.example.rssclipping.di

import android.content.Context
import androidx.room.Room
import com.example.rssclipping.data.local.database.AppDatabase
import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.data.repository.FeedRepositoryImpl
import com.example.rssclipping.data.repository.SubscriptionRepository
import com.example.rssclipping.data.repository.SubscriptionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton

/**
 * Módulo de Hilt que define cómo proporcionar las dependencias a nivel de aplicación (Singleton).
 * Hilt usará estas definiciones para inyectar las instancias correctas donde se necesiten.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Proporciona la instancia única (Singleton) de la base de datos [AppDatabase].
     * @param context El contexto de la aplicación, inyectado por Hilt.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "rss_clipping_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
        .build()
    }

    /**
     * Proporciona una instancia de [ArticleDao] a partir de la base de datos.
     * No necesita ser Singleton porque Room ya gestiona su ciclo de vida.
     */
    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    /**
     * Proporciona una instancia de [SubscriptionDao] a partir de la base de datos.
     */
    @Provides
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao = database.subscriptionDao()

    /**
     * Proporciona la instancia única (Singleton) del cliente HTTP [HttpClient] de Ktor.
     * Reutilizar el cliente HTTP es una buena práctica para optimizar recursos.
     */
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(OkHttp)

    /**
     * Proporciona la instancia única (Singleton) de [RssNetworkDataSource].
     */
    @Provides
    @Singleton
    fun provideRssNetworkDataSource(httpClient: HttpClient): RssNetworkDataSource = RssNetworkDataSource(httpClient)

    /**
     * Proporciona la instancia única (Singleton) de la implementación de [FeedRepository].
     * Hilt inyectará [FeedRepositoryImpl] cada vez que se solicite un [FeedRepository].
     */
    @Provides
    @Singleton
    fun provideFeedRepository(articleDao: ArticleDao, networkDataSource: RssNetworkDataSource): FeedRepository {
        return FeedRepositoryImpl(articleDao, networkDataSource)
    }

    /**
     * Proporciona la instancia única (Singleton) de la implementación de [SubscriptionRepository].
     */
    @Provides
    @Singleton
    fun provideSubscriptionRepository(subscriptionDao: SubscriptionDao, networkDataSource: RssNetworkDataSource): SubscriptionRepository {
        return SubscriptionRepositoryImpl(subscriptionDao, networkDataSource)
    }
}
