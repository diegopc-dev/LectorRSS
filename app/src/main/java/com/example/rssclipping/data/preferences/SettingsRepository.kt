package com.example.rssclipping.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Define las claves de tipo [Preferences.Key] que se usarán para guardar y recuperar
 * valores de [DataStore]. Usar un objeto privado asegura que estas claves no se
 * expongan fuera del repositorio, manteniendo la encapsulación.
 */
private object PreferencesKeys {
    /** Clave para guardar el intervalo de sincronización en segundo plano, en horas. */
    val SYNC_INTERVAL_HOURS = longPreferencesKey("sync_interval_hours")
}

/**
 * Repositorio para gestionar las preferencias del usuario. Abstrae el acceso a [DataStore]
 * para que el resto de la aplicación no necesite conocer los detalles de implementación
 * de cómo se guardan las preferencias.
 *
 * @param dataStore La instancia de [DataStore] proporcionada por Hilt.
 */
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /** El intervalo de sincronización por defecto (en horas) si el usuario no ha guardado ninguno. */
    private val DEFAULT_SYNC_INTERVAL_HOURS = 4L

    /**
     * Expone un [Flow] con el intervalo de sincronización actual en horas.
     * Cada vez que el valor cambie en DataStore, este Flow emitirá el nuevo valor.
     * Si no hay ningún valor guardado, emitirá el [DEFAULT_SYNC_INTERVAL_HOURS].
     */
    val syncIntervalHours: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SYNC_INTERVAL_HOURS] ?: DEFAULT_SYNC_INTERVAL_HOURS
        }

    /**
     * Guarda un nuevo intervalo de sincronización en DataStore.
     * @param hours El intervalo en horas a guardar.
     */
    suspend fun setSyncInterval(hours: Long) {
        dataStore.edit {
            it[PreferencesKeys.SYNC_INTERVAL_HOURS] = hours
        }
    }
}
