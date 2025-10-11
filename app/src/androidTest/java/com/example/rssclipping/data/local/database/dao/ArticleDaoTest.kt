package com.example.rssclipping.data.local.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rssclipping.data.local.database.AppDatabase
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Pruebas instrumentadas para el [ArticleDao].
 */
@RunWith(AndroidJUnit4::class)
class ArticleDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var articleDao: ArticleDao
    private lateinit var subscriptionDao: SubscriptionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        articleDao = db.articleDao()
        subscriptionDao = db.subscriptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAllAndGetAll_returnsAllArticlesOrderedByDate() = runBlocking {
        // Arrange: Crear una suscripción para poder asociar los artículos
        val subId = subscriptionDao.insert(SubscriptionEntity(url = "url", name = "name", iconUrl = "", category = "cat"))

        // Artículos con fechas en orden no cronológico
        val article1 = ArticleEntity(subscriptionId = subId, guid = "guid1", title = "Old Article", link = "", content = "", thumbnailUrl = "", pubDate = "2023-01-01T12:00:00Z")
        val article2 = ArticleEntity(subscriptionId = subId, guid = "guid2", title = "New Article", link = "", content = "", thumbnailUrl = "", pubDate = "2024-01-01T12:00:00Z")
        val article3 = ArticleEntity(subscriptionId = subId, guid = "guid3", title = "Medium Article", link = "", content = "", thumbnailUrl = "", pubDate = "2023-06-01T12:00:00Z")
        articleDao.insertAll(listOf(article1, article2, article3))

        // Act: Obtener todos los artículos (la consulta los ordena por fecha DESC)
        val articles = articleDao.getAllArticles().first()

        // Assert
        assertEquals(3, articles.size)
        assertEquals("New Article", articles[0].title) // El más nuevo primero
        assertEquals("Medium Article", articles[1].title)
        assertEquals("Old Article", articles[2].title) // El más viejo al final
    }

    @Test
    fun getArticlesBySubscriptionId_returnsCorrectArticlesOrderedByDate() = runBlocking {
        // Arrange: Crear dos suscripciones
        val subId1 = subscriptionDao.insert(SubscriptionEntity(url = "url1", name = "sub1", iconUrl = "", category = "cat"))
        val subId2 = subscriptionDao.insert(SubscriptionEntity(url = "url2", name = "sub2", iconUrl = "", category = "cat"))

        // Artículos para ambas suscripciones
        val article1_sub1 = ArticleEntity(subscriptionId = subId1, guid = "guid1", title = "New Article Sub 1", link = "", content = "", thumbnailUrl = "", pubDate = "2024-01-01T12:00:00Z")
        val article2_sub1 = ArticleEntity(subscriptionId = subId1, guid = "guid2", title = "Old Article Sub 1", link = "", content = "", thumbnailUrl = "", pubDate = "2023-01-01T12:00:00Z")
        val article_sub2 = ArticleEntity(subscriptionId = subId2, guid = "guid3", title = "Article Sub 2", link = "", content = "", thumbnailUrl = "", pubDate = "2024-01-01T12:00:00Z")
        articleDao.insertAll(listOf(article1_sub1, article2_sub1, article_sub2))

        // Act: Obtener solo los artículos de la primera suscripción
        val articles = articleDao.getArticlesBySubscriptionId(subId1).first()

        // Assert
        assertEquals(2, articles.size)
        assertEquals("New Article Sub 1", articles[0].title)
        assertEquals("Old Article Sub 1", articles[1].title)
    }

    @Test
    fun deleteAllForSubscription_removesOnlyCorrectArticles() = runBlocking {
        // Arrange: Crear dos suscripciones y sus artículos
        val subId1 = subscriptionDao.insert(SubscriptionEntity(url = "url1", name = "sub1", iconUrl = "", category = "cat"))
        val subId2 = subscriptionDao.insert(SubscriptionEntity(url = "url2", name = "sub2", iconUrl = "", category = "cat"))
        articleDao.insertAll(listOf(
            ArticleEntity(subscriptionId = subId1, guid = "guid1", title = "t1", link = "", content = "", thumbnailUrl = "", pubDate = "d1"),
            ArticleEntity(subscriptionId = subId2, guid = "guid2", title = "t2", link = "", content = "", thumbnailUrl = "", pubDate = "d2")
        ))

        // Act: Borrar todos los artículos de la primera suscripción
        articleDao.deleteAllForSubscription(subId1)

        // Assert
        val allArticles = articleDao.getAllArticles().first()
        assertEquals(1, allArticles.size)
        assertEquals(subId2, allArticles[0].subscriptionId)
    }

    @Test
    fun onConflict_replacesExistingArticle() = runBlocking {
        // Arrange: Insertar un artículo
        val subId = subscriptionDao.insert(SubscriptionEntity(url = "url", name = "name", iconUrl = "", category = "cat"))
        val originalArticle = ArticleEntity(subscriptionId = subId, guid = "unique_guid", title = "Original Title", link = "", content = "", thumbnailUrl = "", pubDate = "2023-01-01T12:00:00Z")
        articleDao.insertAll(listOf(originalArticle))

        // Act: Insertar un artículo con el mismo GUID pero título diferente
        val updatedArticle = ArticleEntity(subscriptionId = subId, guid = "unique_guid", title = "Updated Title", link = "", content = "", thumbnailUrl = "", pubDate = "2024-01-01T12:00:00Z")
        articleDao.insertAll(listOf(updatedArticle))

        // Assert: Comprobar que solo hay un artículo y que es el actualizado
        val allArticles = articleDao.getAllArticles().first()
        assertEquals(1, allArticles.size)
        assertEquals("Updated Title", allArticles[0].title)
    }
}
