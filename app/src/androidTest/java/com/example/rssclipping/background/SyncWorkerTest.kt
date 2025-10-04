package com.example.rssclipping.background

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.data.repository.SubscriptionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SyncWorkerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    private lateinit var feedRepository: FeedRepository

    @MockK
    private lateinit var subscriptionRepository: SubscriptionRepository

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun doWork_returnsSuccess_andSyncsAllSubscriptions() = runBlocking {
        // Arrange
        val subscriptions = listOf(
            SubscriptionEntity(id = 1, url = "url1", name = "name1", category = "cat1"),
            SubscriptionEntity(id = 2, url = "url2", name = "name2", category = "cat2")
        )
        coEvery { subscriptionRepository.getAllSubscriptions() } returns flowOf(subscriptions)

        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(TestWorkerFactory(subscriptionRepository, feedRepository))
            .build()

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { feedRepository.syncSubscription("url1", 1L) }
        coVerify(exactly = 1) { feedRepository.syncSubscription("url2", 2L) }
    }
}

/**
 * Una fábrica de Workers personalizada para inyectar dependencias mockeadas en el SyncWorker durante las pruebas.
 */
class TestWorkerFactory(
    private val subscriptionRepository: SubscriptionRepository,
    private val feedRepository: FeedRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == SyncWorker::class.java.name) {
            SyncWorker(appContext, workerParameters, subscriptionRepository, feedRepository)
        } else {
            null
        }
    }
}
