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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Pruebas instrumentadas para el [SubscriptionDao].
 * Estas pruebas se ejecutan en un dispositivo o emulador y validan la capa de base de datos.
 */
@RunWith(AndroidJUnit4::class)
class SubscriptionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var sut: SubscriptionDao // System Under Test

    /**
     * Crea una base de datos en memoria antes de cada prueba.
     * Usar una base de datos en memoria asegura que cada prueba sea aislada y no deje rastros.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Permite que las consultas se ejecuten en el hilo principal (solo para testing)
            .allowMainThreadQueries()
            .build()
        sut = db.subscriptionDao()
    }

    /**
     * Cierra la base de datos después de cada prueba para liberar recursos.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetAll_returnsAllSubscriptionsInOrder() = runBlocking {
        // Arrange: Insertar dos suscripciones en un orden no alfabético
        val subscription1 = SubscriptionEntity(url = "url1", name = "B Name", iconUrl = "", category = "cat1")
        val subscription2 = SubscriptionEntity(url = "url2", name = "A Name", iconUrl = "", category = "cat2")
        sut.insert(subscription1)
        sut.insert(subscription2)

        // Act: Obtener todas las suscripciones (la consulta las ordena por nombre ASC)
        val subscriptions = sut.getAll().first()

        // Assert
        assertEquals(2, subscriptions.size)
        assertEquals("A Name", subscriptions[0].name)
        assertEquals("B Name", subscriptions[1].name)
    }

    @Test
    fun getById_returnsCorrectSubscription() = runBlocking {
        // Arrange: Insertar una suscripción y obtener el ID auto-generado
        val subscription = SubscriptionEntity(url = "url1", name = "name1", iconUrl = "", category = "cat1")
        val insertedId = sut.insert(subscription)

        // Act: Buscar la suscripción usando el ID devuelto
        val result = sut.getById(insertedId)

        // Assert: Comprobar que los datos son correctos
        assertNotNull(result)
        assertEquals(insertedId, result?.id)
        assertEquals("url1", result?.url)
        assertEquals("name1", result?.name)
    }

    @Test
    fun delete_removesSubscriptionFromDatabase() = runBlocking {
        // Arrange: Insertar una suscripción y obtener el ID
        val subscription = SubscriptionEntity(url = "url1", name = "name1", iconUrl = "", category = "cat1")
        val insertedId = sut.insert(subscription)
        val entityToDelete = subscription.copy(id = insertedId)

        // Act: Borrar la entidad y comprobar la lista de suscripciones
        sut.delete(entityToDelete)
        val subscriptions = sut.getAll().first()

        // Assert
        assertTrue(subscriptions.isEmpty())
    }

    @Test
    fun update_modifiesSubscriptionCorrectly() = runBlocking {
        // Arrange: Insertar una suscripción original
        val originalSubscription = SubscriptionEntity(url = "url1", name = "Original Name", iconUrl = "original_icon", category = "Original Category")
        val insertedId = sut.insert(originalSubscription)

        // Act: Crear una versión actualizada con el mismo ID y llamar a update
        val updatedSubscription = originalSubscription.copy(
            id = insertedId,
            name = "Updated Name",
            category = "Updated Category"
        )
        sut.update(updatedSubscription)
        val result = sut.getById(insertedId)

        // Assert: Verificar que los campos se han actualizado
        assertNotNull(result)
        assertEquals("Updated Name", result?.name)
        assertEquals("Updated Category", result?.category)
        assertEquals("original_icon", result?.iconUrl) // El iconUrl no debería haber cambiado
        assertEquals("url1", result?.url) // El URL no debería haber cambiado
    }
}
