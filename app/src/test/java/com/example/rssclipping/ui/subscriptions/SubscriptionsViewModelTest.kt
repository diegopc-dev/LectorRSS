package com.example.rssclipping.ui.subscriptions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.SubscriptionRepository
import com.example.rssclipping.util.MainCoroutineRule
import app.cash.turbine.test
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SubscriptionsViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var subscriptionRepository: SubscriptionRepository

    private lateinit var sut: SubscriptionsViewModel

    @Test
    fun `state is updated with subscriptions from repository`() = runBlocking {
        // Arrange: Preparamos los datos falsos y el comportamiento del mock.
        val fakeSubscriptions = listOf(
            SubscriptionEntity(id = 1, name = "Blog 1", url = "url1", category = "Tech")
        )
        every { subscriptionRepository.getAllSubscriptions() } returns flowOf(fakeSubscriptions)

        // Act: Creamos el ViewModel, lo que debería activar la recogida del Flow.
        sut = SubscriptionsViewModel(subscriptionRepository)

        // Assert: Verificamos que el StateFlow emite la lista de suscripciones.
        sut.subscriptions.test { 
            val emission = awaitItem()
            assertEquals(fakeSubscriptions, emission)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
