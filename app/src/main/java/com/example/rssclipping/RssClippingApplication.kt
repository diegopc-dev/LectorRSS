package com.example.rssclipping

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rssclipping.background.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Clase principal de la aplicación. Su rol es triple:
 * 1. `@HiltAndroidApp`: Actúa como el punto de entrada para el grafo de dependencias de Hilt.
 * 2. `Configuration.Provider`: Proporciona una configuración personalizada de WorkManager que utiliza Hilt.
 * 3. En su `onCreate`, programa el trabajo periódico de sincronización.
 */
@HiltAndroidApp
class RssClippingApplication : Application(), Configuration.Provider {

    // Hilt inyecta la fábrica de workers que sabe cómo crear workers anotados con @HiltWorker.
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Proporciona la configuración de WorkManager. Este método se llama una vez al inicio de la app.
     * Le decimos a WorkManager que use nuestra `HiltWorkerFactory` para crear instancias de sus workers.
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    /**
     * Se llama cuando la aplicación es creada. Es el lugar ideal para inicializaciones globales.
     */
    override fun onCreate() {
        super.onCreate()
        // Inicia la configuración del trabajo periódico de sincronización.
        setupRecurringWork()
    }

    /**
     * Configura y encola un trabajo periódico para el [SyncWorker].
     */
    private fun setupRecurringWork() {
        val workManager = WorkManager.getInstance(applicationContext)

        // Construye una petición de trabajo periódico que se repetirá cada 12 horas.
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            12, // El intervalo de repetición.
            TimeUnit.HOURS // La unidad de tiempo para el intervalo.
        ).build()

        // Encola el trabajo con un nombre único y una política de existencia.
        workManager.enqueueUniquePeriodicWork(
            "SyncWorker", // Un nombre único para que no haya duplicados.
            ExistingPeriodicWorkPolicy.KEEP, // Si ya existe un trabajo con este nombre, no hacer nada y mantener el anterior.
            syncRequest
        )
    }
}
