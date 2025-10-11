package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.SubscriptionDao
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkFeed
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class SubscriptionRepositoryTest {

    @RelaxedMockK
    private lateinit var subscriptionDao: SubscriptionDao

    @RelaxedMockK
    private lateinit var networkDataSource: RssNetworkDataSource

    private lateinit var sut: SubscriptionRepositoryImpl

    @Before
    fun setUp() {
        sut = SubscriptionRepositoryImpl(subscriptionDao, networkDataSource)
    }

    @Test
    fun `addSubscription WHEN network succeeds THEN inserts subscription with network data`() = runTest {
        // Arrange: Configurar el mock de red para que devuelva un feed con datos
        val feedUrl = "http://example.com/feed"
        val networkFeed = NetworkFeed(
            channelTitle = "Test Feed Title",
            channelIconUrl = "http://example.com/icon.png",
            articles = emptyList()
        )
        coEvery { networkDataSource.fetchFeed(feedUrl) } returns networkFeed

        // Capturar la entidad que se pasa al DAO para verificarla
        val slot = slot<SubscriptionEntity>()
        coEvery { subscriptionDao.insert(capture(slot)) } returns 1L

        // Act: Llamar al método que estamos probando
        sut.addSubscription(feedUrl)

        // Assert: Verificar que la entidad insertada tiene los datos correctos
        assertEquals("Test Feed Title", slot.captured.name)
        assertEquals("http://example.com/icon.png", slot.captured.iconUrl)
        assertEquals(feedUrl, slot.captured.url)
    }

    @Test
    fun `addSubscription WHEN network fails THEN inserts subscription with URL as name`() = runTest {
        // Arrange: Configurar el mock de red para que lance una excepción
        val feedUrl = "http://example.com/feed"
        coEvery { networkDataSource.fetchFeed(feedUrl) } throws Exception("Network error")

        // Capturar la entidad que se pasa al DAO
        val slot = slot<SubscriptionEntity>()
        coEvery { subscriptionDao.insert(capture(slot)) } returns 1L

        // Act: Llamar al método
        sut.addSubscription(feedUrl)

        // Assert: Verificar que el nombre de la entidad es la URL y el icono está vacío
        assertEquals(feedUrl, slot.captured.name)
        assertEquals("", slot.captured.iconUrl)
    }

    @Test
    fun `updateSubscription SHOULD call dao's update method`() = runTest {
        // Arrange
        val subscription = SubscriptionEntity(id = 1, url = "url", name = "name", iconUrl = "", category = "cat")

        // Act
        sut.updateSubscription(subscription)

        // Assert: Verificar que se llamó al método update del DAO con la entidad correcta
        coVerify { subscriptionDao.update(subscription) }
    }

    @Test
    fun `deleteSubscription SHOULD call dao's delete method`() = runTest {
        // Arrange
        val subscription = SubscriptionEntity(id = 1, url = "url", name = "name", iconUrl = "", category = "cat")

        // Act
        sut.deleteSubscription(subscription)

        // Assert: Verificar que se llamó al método delete del DAO con la entidad correcta
        coVerify { subscriptionDao.delete(subscription) }
    }
}
