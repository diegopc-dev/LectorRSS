package com.example.rssclipping.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.rssclipping.data.local.database.AppDatabase
import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.preferences.SettingsRepository
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

/** Nombre del fichero donde se guardarán las preferencias del usuario. */
private const val USER_PREFERENCES_NAME = "user_preferences"

/**
 * Extensión de [Context] para obtener una instancia de [DataStore] de forma segura.
 * Se crea como un singleton a nivel de aplicación.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

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
     * @return Una instancia de [AppDatabase] gestionada por Room.
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
     * Proporciona la instancia única (Singleton) de [DataStore] para las preferencias del usuario.
     * @param context El contexto de la aplicación, inyectado por Hilt.
     * @return La instancia de [DataStore] para toda la app.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Proporciona una instancia de [ArticleDao] a partir de la base de datos.
     * @param database La instancia de [AppDatabase] proporcionada por Hilt.
     * @return Una instancia del DAO para interactuar con la tabla de artículos.
     */
    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    /**
     * Proporciona una instancia de [SubscriptionDao] a partir de la base de datos.
     * @param database La instancia de [AppDatabase] proporcionada por Hilt.
     * @return Una instancia del DAO para interactuar con la tabla de suscripciones.
     */
    @Provides
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao = database.subscriptionDao()

    /**
     * Proporciona la instancia única (Singleton) del cliente HTTP [HttpClient] de Ktor.
     * Reutilizar el cliente HTTP es una buena práctica para optimizar recursos.
     * @return Una instancia de [HttpClient] configurada con el motor OkHttp.
     */
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(OkHttp)

    /**
     * Proporciona la instancia única (Singleton) de [RssNetworkDataSource].
     * @param httpClient El cliente Ktor para realizar las peticiones de red.
     * @return Una instancia de la fuente de datos de red.
     */
    @Provides
    @Singleton
    fun provideRssNetworkDataSource(httpClient: HttpClient): RssNetworkDataSource = RssNetworkDataSource(httpClient)

    /**
     * Proporciona la instancia única (Singleton) de [SettingsRepository].
     * @param dataStore La instancia de DataStore para guardar y leer preferencias.
     * @return La implementación del repositorio de ajustes.
     */
    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository {
        return SettingsRepository(dataStore)
    }

    /**
     * Proporciona la instancia única (Singleton) de la implementación de [FeedRepository].
     * Hilt inyectará [FeedRepositoryImpl] cada vez que se solicite un [FeedRepository].
     * @param articleDao El DAO de artículos.
     * @param networkDataSource La fuente de datos de red.
     * @param subscriptionRepository El repositorio de suscripciones.
     * @return La implementación del repositorio de feeds.
     */
    @Provides
    @Singleton
    fun provideFeedRepository(
        articleDao: ArticleDao,
        networkDataSource: RssNetworkDataSource,
        subscriptionRepository: SubscriptionRepository
    ): FeedRepository {
        return FeedRepositoryImpl(articleDao, networkDataSource, subscriptionRepository)
    }

    /**
     * Proporciona la instancia única (Singleton) de la implementación de [SubscriptionRepository].
     * @param subscriptionDao El DAO de suscripciones.
     * @param networkDataSource La fuente de datos de red.
     * @return La implementación del repositorio de suscripciones.
     */
    @Provides
    @Singleton
    fun provideSubscriptionRepository(subscriptionDao: SubscriptionDao, networkDataSource: RssNetworkDataSource): SubscriptionRepository {
        return SubscriptionRepositoryImpl(subscriptionDao, networkDataSource)
    }
}
