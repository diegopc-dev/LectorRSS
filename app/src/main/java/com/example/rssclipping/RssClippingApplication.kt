package com.example.rssclipping

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rssclipping.background.SyncWorker
import com.example.rssclipping.data.preferences.SettingsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Clase Application personalizada para la aplicación RssClipping.
 * Tiene tres responsabilidades principales:
 * 1. Inicializar Hilt para la inyección de dependencias (@HiltAndroidApp).
 * 2. Proveer una configuración de WorkManager que sea consciente de Hilt.
 * 3. Programar y actualizar la tarea de sincronización periódica en segundo plano.
 */
@HiltAndroidApp
class RssClippingApplication : Application(), Configuration.Provider {

    /**
     * Factoría de Workers inyectada por Hilt, necesaria para que WorkManager pueda crear Workers
     * que a su vez tengan dependencias inyectadas.
     */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Repositorio de ajustes para acceder a las preferencias del usuario.
     */
    @Inject
    lateinit var settingsRepository: SettingsRepository

    /**
     * Un CoroutineScope a nivel de aplicación que no se cancela.
     * Se usa para lanzar tareas que deben sobrevivir al ciclo de vida de las Activities/Fragments,
     * como la programación del trabajo en segundo plano.
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        // Al crear la aplicación, se inicia la lógica para programar el trabajo periódico.
        setupRecurringWork()
    }

    /**
     * Proporciona la configuración personalizada de WorkManager a nivel de aplicación.
     * Le dice a WorkManager que use nuestra HiltWorkerFactory para crear los workers.
     */
    override val workManagerConfiguration:
        Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Orquesta la programación de la tarea de sincronización.
     * Lee el valor inicial de las preferencias y se suscribe a futuros cambios para reprogramar el trabajo.
     */
    private fun setupRecurringWork() {
        applicationScope.launch {
            // Leemos el valor inicial del intervalo guardado por el usuario.
            val syncInterval = settingsRepository.syncIntervalHours.first()
            scheduleWork(syncInterval)

            // Nos suscribimos a futuros cambios en el intervalo. Si el usuario elige una nueva
            // opción en los ajustes, este bloque se ejecutará y reprogramará la tarea.
            settingsRepository.syncIntervalHours.collect { newInterval ->
                scheduleWork(newInterval)
            }
        }
    }

    /**
     * Crea y encola una tarea de sincronización periódica en WorkManager.
     * @param syncInterval El intervalo en horas con el que la tarea debe repetirse.
     */
    private fun scheduleWork(syncInterval: Long) {
        // Construimos la petición de trabajo periódico, pasándole nuestro Worker y el intervalo.
        val repeatingRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            syncInterval, TimeUnit.HOURS
        ).build()

        // Encolamos la tarea en WorkManager como un trabajo periódico único.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker::class.java.name, // Un nombre único para nuestra tarea
            ExistingPeriodicWorkPolicy.UPDATE, // Si ya existe una tarea con ese nombre, la actualiza con el nuevo intervalo
            repeatingRequest
        )
    }
}
