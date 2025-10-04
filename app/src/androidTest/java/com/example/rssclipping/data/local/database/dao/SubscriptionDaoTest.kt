package com.example.rssclipping.data.local.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rssclipping.data.local.database.AppDatabase
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
class SubscriptionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var sut: SubscriptionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        sut = db.subscriptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetAll_returnsAllSubscriptions() = runBlocking {
        // Arrange
        val subscription1 = SubscriptionEntity(url = "url1", name = "name1", category = "cat1")
        val subscription2 = SubscriptionEntity(url = "url2", name = "name2", category = "cat2")
        sut.insert(subscription1)
        sut.insert(subscription2)

        // Act
        val subscriptions = sut.getAll().first()

        // Assert
        assertEquals(2, subscriptions.size)
    }

    @Test
    fun delete_removesSubscriptionFromDatabase() = runBlocking {
        // Arrange
        val subscription = SubscriptionEntity(id = 1, url = "url1", name = "name1", category = "cat1")
        sut.insert(subscription)

        // Act
        sut.delete(subscription)
        val subscriptions = sut.getAll().first()

        // Assert
        assertTrue(subscriptions.isEmpty())
    }

    @Test
    fun insertAndGetById_returnsCorrectSubscription() = runBlocking {
        // Arrange
        val subscription = SubscriptionEntity(id = 1, url = "url1", name = "name1", category = "cat1")
        sut.insert(subscription)

        // Act
        val result = sut.getById(1)

        // Assert
        assertNotNull(result)
        assertEquals("url1", result?.url)
        assertEquals("name1", result?.name)
    }
}
