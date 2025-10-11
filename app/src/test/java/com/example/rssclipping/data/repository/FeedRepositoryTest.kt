package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkArticle
import com.example.rssclipping.data.network.model.NetworkFeed
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class FeedRepositoryTest {

    @RelaxedMockK
    private lateinit var articleDao: ArticleDao

    @RelaxedMockK
    private lateinit var networkDataSource: RssNetworkDataSource

    @RelaxedMockK
    private lateinit var subscriptionRepository: SubscriptionRepository

    private lateinit var sut: FeedRepositoryImpl

    @Before
    fun setUp() {
        sut = FeedRepositoryImpl(articleDao, networkDataSource, subscriptionRepository)
    }

    @Test
    fun `syncSubscription WHEN network succeeds THEN inserts articles with normalized dates`() = runTest {
        // Arrange: Configurar el mock de red para que devuelva una fecha en formato RFC_1123
        val feedUrl = "http://example.com/feed"
        val subscriptionId = 1L
        val dirtyDate = "Tue, 05 Jun 2024 10:00:00 GMT"
        val expectedNormalizedDate = "2024-06-05T10:00:00Z"

        val networkArticles = listOf(
            NetworkArticle("guid1", "title1", "link1", dirtyDate, "content1", "thumb1"),
            NetworkArticle("guid2", "title2", "link2", "invalid-date", "content2", "thumb2")
        )
        coEvery { networkDataSource.fetchFeed(feedUrl) } returns NetworkFeed("channel", "icon", networkArticles)

        // Capturar la lista de entidades que se pasa al DAO
        val slot = slot<List<ArticleEntity>>()
        coEvery { articleDao.insertAll(capture(slot)) } returns Unit

        // Act: Llamar al método que estamos probando
        sut.syncSubscription(feedUrl, subscriptionId)

        // Assert: Verificar que las fechas se han normalizado correctamente
        assertEquals(2, slot.captured.size)
        // El primer artículo debe tener la fecha normalizada
        assertEquals(expectedNormalizedDate, slot.captured[0].pubDate)
        // El segundo artículo, con fecha inválida, debe tener un string vacío
        assertEquals("", slot.captured[1].pubDate)
    }

    @Test
    fun `syncSubscription WHEN network fails THEN does not insert anything`() = runTest {
        // Arrange: Configurar el mock de red para que lance una excepción
        val feedUrl = "http://example.com/feed"
        val subscriptionId = 1L
        coEvery { networkDataSource.fetchFeed(feedUrl) } throws Exception("Network error")

        // Act: Llamar al método
        sut.syncSubscription(feedUrl, subscriptionId)

        // Assert: Verificar que NUNCA se llamó al método insertAll del DAO
        coVerify(exactly = 0) { articleDao.insertAll(any()) }
    }

    @Test
    fun `syncAllSubscriptions SHOULD sync each subscription`() = runTest {
        // Arrange: Configurar el mock del repositorio de suscripciones para que devuelva una lista
        val subscriptions = listOf(
            SubscriptionEntity(id = 1, url = "url1", name = "name1", iconUrl = "", category = "cat1"),
            SubscriptionEntity(id = 2, url = "url2", name = "name2", iconUrl = "", category = "cat2")
        )
        coEvery { subscriptionRepository.getAll() } returns flowOf(subscriptions)
        // Configurar el mock de red para que no falle
        coEvery { networkDataSource.fetchFeed(any()) } returns NetworkFeed("", "", emptyList())

        // Act: Llamar al método
        sut.syncAllSubscriptions()

        // Assert: Verificar que se llamó a fetchFeed para cada una de las URLs de las suscripciones
        coVerify(exactly = 1) { networkDataSource.fetchFeed("url1") }
        coVerify(exactly = 1) { networkDataSource.fetchFeed("url2") }
    }
}
