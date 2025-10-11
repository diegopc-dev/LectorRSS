package com.example.rssclipping.ui.subscriptions

import app.cash.turbine.test
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.SubscriptionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class SubscriptionsViewModelTest {

    @RelaxedMockK
    private lateinit var subscriptionRepository: SubscriptionRepository

    private lateinit var sut: SubscriptionsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is updated with subscriptions from repository`() = runTest {
        // Arrange: Configurar el mock para que emita una lista de suscripciones
        val subscriptions = listOf(SubscriptionEntity(1, "url1", "name1", "icon1", "cat1"))
        coEvery { subscriptionRepository.getAll() } returns flowOf(subscriptions)

        // Act: Crear el ViewModel después de configurar el mock
        sut = SubscriptionsViewModel(subscriptionRepository)

        // Assert: Usa Turbine para probar el StateFlow
        sut.subscriptions.test {
            val emittedList = awaitItem()
            assertEquals(1, emittedList.size)
            assertEquals("name1", emittedList[0].name)
        }
    }

    @Test
    fun `addSubscription SHOULD call repository's addSubscription`() = runTest {
        // Arrange
        coEvery { subscriptionRepository.getAll() } returns flowOf(emptyList())
        sut = SubscriptionsViewModel(subscriptionRepository)
        val url = "http://new.url.com"

        // Act
        sut.addSubscription(url)

        // Assert
        coVerify { subscriptionRepository.addSubscription(url) }
    }

    @Test
    fun `updateSubscription SHOULD call repository's updateSubscription`() = runTest {
        // Arrange
        coEvery { subscriptionRepository.getAll() } returns flowOf(emptyList())
        sut = SubscriptionsViewModel(subscriptionRepository)
        val subscription = SubscriptionEntity(1, "url", "name", "icon", "cat")

        // Act
        sut.updateSubscription(subscription)

        // Assert
        coVerify { subscriptionRepository.updateSubscription(subscription) }
    }

    @Test
    fun `deleteSubscription SHOULD call repository's deleteSubscription`() = runTest {
        // Arrange
        coEvery { subscriptionRepository.getAll() } returns flowOf(emptyList())
        sut = SubscriptionsViewModel(subscriptionRepository)
        val subscription = SubscriptionEntity(1, "url", "name", "icon", "cat")

        // Act
        sut.deleteSubscription(subscription)

        // Assert
        coVerify { subscriptionRepository.deleteSubscription(subscription) }
    }
}
