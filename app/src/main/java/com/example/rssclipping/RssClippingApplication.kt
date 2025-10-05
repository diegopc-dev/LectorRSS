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

@HiltAndroidApp
class RssClippingApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val workManager = WorkManager.getInstance(applicationContext)

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            12, // Repetir cada 12 horas
            TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "SyncWorker", // Un nombre único para este trabajo
            ExistingPeriodicWorkPolicy.KEEP, // Si ya existe un trabajo con este nombre, no hacer nada
            syncRequest
        )
    }
}
