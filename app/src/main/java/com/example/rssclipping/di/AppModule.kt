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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "rss_clipping_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        .build()
    }

    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    @Provides
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao = database.subscriptionDao()

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(OkHttp)

    @Provides
    @Singleton
    fun provideRssNetworkDataSource(httpClient: HttpClient): RssNetworkDataSource = RssNetworkDataSource(httpClient)

    @Provides
    @Singleton
    fun provideFeedRepository(articleDao: ArticleDao, networkDataSource: RssNetworkDataSource): FeedRepository {
        return FeedRepositoryImpl(articleDao, networkDataSource)
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(subscriptionDao: SubscriptionDao, networkDataSource: RssNetworkDataSource): SubscriptionRepository {
        return SubscriptionRepositoryImpl(subscriptionDao, networkDataSource)
    }
}
