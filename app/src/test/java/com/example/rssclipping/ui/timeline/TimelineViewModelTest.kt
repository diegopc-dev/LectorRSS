package com.example.rssclipping.ui.timeline

import app.cash.turbine.test
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.FeedRepository
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
class TimelineViewModelTest {

    @RelaxedMockK
    private lateinit var feedRepository: FeedRepository

    @RelaxedMockK
    private lateinit var subscriptionRepository: SubscriptionRepository

    private lateinit var sut: TimelineViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mockear los repositorios para que devuelvan datos de prueba
        val articles = listOf(ArticleEntity(1, 1, "g1", "t1", "l1", "d1", "c1", "th1"))
        val subscriptions = listOf(SubscriptionEntity(1, "url1", "name1", "icon1", "cat1"))
        coEvery { feedRepository.getAllArticles() } returns flowOf(articles)
        coEvery { subscriptionRepository.getAll() } returns flowOf(subscriptions)

        sut = TimelineViewModel(feedRepository, subscriptionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is updated with data from repositories on init`() = runTest {
        // Assert: Usa Turbine para probar las emisiones del StateFlow
        sut.uiState.test {
            // Avanza el dispatcher para que las corrutinas del init se completen
            testDispatcher.scheduler.advanceUntilIdle()

            // El estado inicial puede variar, así que consumimos el primer item
            awaitItem()
            val finalState = awaitItem()

            // Verificar que el estado final contiene los datos de los mocks
            assertEquals(1, finalState.allArticles.size)
            assertEquals(1, finalState.subscriptions.size)
            assertFalse(finalState.isRefreshing)
        }
    }

    @Test
    fun `onFilterChanged SHOULD update selectedSubscription`() = runTest {
        val subscription = SubscriptionEntity(2, "url2", "name2", "icon2", "cat2")
        sut.uiState.test {
            awaitItem() // Estado inicial

            // Act
            sut.onFilterChanged(subscription)

            // Assert
            val updatedState = awaitItem()
            assertEquals(subscription, updatedState.selectedSubscription)
        }
    }

    @Test
    fun `refreshFeeds SHOULD set refreshing to true then false`() = runTest {
        sut.uiState.test {
            awaitItem() // Estado inicial

            // Act
            sut.refreshFeeds()

            // Assert: Verificar la secuencia de estados de isRefreshing
            assertTrue(awaitItem().isRefreshing)
            assertFalse(awaitItem().isRefreshing)
            coVerify { feedRepository.syncAllSubscriptions() }
        }
    }
}
