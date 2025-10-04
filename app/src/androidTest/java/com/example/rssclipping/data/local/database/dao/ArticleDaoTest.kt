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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ArticleDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var articleDao: ArticleDao
    private lateinit var subscriptionDao: SubscriptionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        articleDao = db.articleDao()
        subscriptionDao = db.subscriptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetBySubscriptionId_returnsCorrectArticles() = runBlocking {
        // Arrange
        val subscription = SubscriptionEntity(id = 1, url = "url", name = "name", category = "cat")
        subscriptionDao.insert(subscription)

        val article1 = ArticleEntity(subscriptionId = 1, guid = "guid1", title = "title1", link = "link1", pubDate = "date1")
        val article2 = ArticleEntity(subscriptionId = 1, guid = "guid2", title = "title2", link = "link2", pubDate = "date2")
        articleDao.insertAll(listOf(article1, article2))

        // Act
        val articles = articleDao.getArticlesBySubscriptionId(1).first()
        val guids = articles.map { it.guid }

        // Assert
        assertEquals(2, articles.size)
        assertTrue(guids.containsAll(listOf("guid1", "guid2")))
    }

    @Test
    fun deleteAllForSubscription_removesOnlyCorrectArticles() = runBlocking {
        // Arrange
        val sub1 = SubscriptionEntity(id = 1, url = "url1", name = "name1", category = "cat1")
        val sub2 = SubscriptionEntity(id = 2, url = "url2", name = "name2", category = "cat2")
        subscriptionDao.insert(sub1)
        subscriptionDao.insert(sub2)

        val sub1Article = ArticleEntity(subscriptionId = 1, guid = "guid1", title = "t1", link = "l1", pubDate = "d1")
        val sub2Article = ArticleEntity(subscriptionId = 2, guid = "guid2", title = "t2", link = "l2", pubDate = "d2")
        articleDao.insertAll(listOf(sub1Article, sub2Article))

        // Act
        articleDao.deleteAllForSubscription(1)

        // Assert
        val sub1Articles = articleDao.getArticlesBySubscriptionId(1).first()
        val sub2Articles = articleDao.getArticlesBySubscriptionId(2).first()
        assertTrue(sub1Articles.isEmpty())
        assertEquals(1, sub2Articles.size)
    }

    @Test
    fun insertAndGetById_returnsCorrectArticle() = runBlocking {
        // Arrange
        val subscription = SubscriptionEntity(id = 1, url = "url", name = "name", category = "cat")
        subscriptionDao.insert(subscription)
        val article = ArticleEntity(id = 1, subscriptionId = 1, guid = "guid1", title = "title1", link = "link1", pubDate = "date1")
        articleDao.insertAll(listOf(article))

        // Act
        val result = articleDao.getById(1)

        // Assert
        assertNotNull(result)
        assertEquals("guid1", result?.guid)
        assertEquals("title1", result?.title)
    }
}
