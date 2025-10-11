package com.example.rssclipping

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Un [AndroidJUnitRunner] personalizado para usar Hilt en las pruebas instrumentadas.
 * Hilt necesita una clase Application diferente para las pruebas para poder inyectar
 * dependencias de test en lugar de las de producción.
 */
@Suppress("unused") // Esta clase es referenciada en el build.gradle.kts, por lo que el IDE no detecta su uso.
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // Usa HiltTestApplication en lugar de la Application normal de la app.
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
