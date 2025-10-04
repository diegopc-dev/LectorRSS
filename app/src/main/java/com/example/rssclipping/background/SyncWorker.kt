package com.example.rssclipping.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.data.repository.SubscriptionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Un CoroutineWorker que se encarga de sincronizar todas las suscripciones de feeds RSS en segundo plano.
 * La anotación @HiltWorker le indica a Hilt que debe gestionar la creación de este Worker,
 * permitiendo la inyección de dependencias definidas en los módulos de Hilt.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    // @Assisted le dice a Hilt que estos parámetros serán proporcionados por WorkManager, no por Hilt.
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    // Hilt se encargará de inyectar las implementaciones de estos repositorios.
    private val subscriptionRepository: SubscriptionRepository,
    private val feedRepository: FeedRepository
) : CoroutineWorker(appContext, params) {

    /**
     * El trabajo principal que se ejecuta en segundo plano. No debe contener lógica de negocio compleja,
     * sino delegarla a los repositorios correspondientes.
     */
    override suspend fun doWork(): Result {
        return try {
            // 1. Obtener la lista de todas las suscripciones del usuario.
            // Usamos .first() para obtener solo la instantánea actual de la lista desde el Flow.
            val subscriptions = subscriptionRepository.getAllSubscriptions().first()

            // 2. Iterar sobre cada suscripción y pedir al FeedRepository que la sincronice.
            // La lógica compleja de red y base de datos está encapsulada en el repositorio.
            subscriptions.forEach { subscription ->
                feedRepository.syncSubscription(subscription.url, subscription.id)
            }

            // 3. Si el bucle se completa sin lanzar excepciones, el trabajo se considera exitoso.
            Result.success()
        } catch (e: Exception) {
            // Si algo falla durante el proceso (ej. al obtener suscripciones de la BD), el trabajo
            // se marca como fallido para que el sistema pueda reintentarlo más tarde según la política definida.
            e.printStackTrace()
            Result.failure()
        }
    }
}
