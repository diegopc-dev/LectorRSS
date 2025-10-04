package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkArticle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FeedRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    private lateinit var articleDao: ArticleDao

    @MockK
    private lateinit var networkDataSource: RssNetworkDataSource

    private lateinit var sut: FeedRepository

    @Before
    fun setUp() {
        sut = FeedRepositoryImpl(articleDao, networkDataSource)
    }

    @Test
    fun `syncSubscription fetches and saves articles`() = runBlocking {
        // Arrange
        val url = "https://example.com/rss"
        val subscriptionId = 1L
        val fakeNetworkArticles = listOf(
            NetworkArticle(guid = "guid1", title = "title1", link = "link1", pubDate = "date1")
        )
        coEvery { networkDataSource.fetchArticles(url) } returns fakeNetworkArticles

        val articleEntitiesSlot = slot<List<ArticleEntity>>()

        // Act
        sut.syncSubscription(url, subscriptionId)

        // Assert
        coVerify { networkDataSource.fetchArticles(url) }
        coVerify { articleDao.insertAll(capture(articleEntitiesSlot)) }

        val capturedArticles = articleEntitiesSlot.captured
        assertEquals(1, capturedArticles.size)
        assertEquals("guid1", capturedArticles[0].guid)
        assertEquals(subscriptionId, capturedArticles[0].subscriptionId)
    }

    @Test
    fun `getArticles returns cached data on network error`() = runBlocking {
        // Arrange
        val subscriptionId = 1L
        val cachedArticles = listOf(ArticleEntity(id = 1, subscriptionId = subscriptionId, guid = "cachedGuid", title = "Cached Title", link = "", pubDate = ""))
        
        // Simulamos un error de red.
        coEvery { networkDataSource.fetchArticles(any()) } throws Exception("Network Error")
        
        // Simulamos que el DAO devuelve datos cacheados.
        every { articleDao.getArticlesBySubscriptionId(subscriptionId) } returns flowOf(cachedArticles)

        // Act
        val result = sut.getArticles(subscriptionId).first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("cachedGuid", result[0].guid)
    }
}
