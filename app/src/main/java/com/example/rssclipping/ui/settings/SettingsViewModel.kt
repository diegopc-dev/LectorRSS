package com.example.rssclipping.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Define el estado de la interfaz de usuario para la pantalla de Ajustes.
 * @property syncIntervalHours El intervalo de sincronización actual, en horas.
 */
data class SettingsUiState(
    val syncIntervalHours: Long = 4L
)

/**
 * ViewModel para la pantalla de Ajustes.
 * Gestiona el estado de la UI y la lógica para leer y escribir las preferencias de sincronización.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     * Expone el estado de la UI como un [StateFlow].
     * Se conecta al `syncIntervalHours` del repositorio, mapea el `Long` a un objeto `SettingsUiState`,
     * y lo convierte en un `StateFlow` para que la UI lo pueda observar de forma segura.
     */
    val uiState: StateFlow<SettingsUiState> = settingsRepository.syncIntervalHours
        .map { hours -> SettingsUiState(syncIntervalHours = hours) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // El flow se mantiene activo 5s después de que el último observador desaparezca
            initialValue = SettingsUiState() // Valor inicial mientras se carga el real desde DataStore
        )

    /**
     * Pide al repositorio que actualice el intervalo de sincronización cuando el usuario selecciona una nueva opción.
     * @param hours El nuevo intervalo en horas a guardar.
     */
    fun updateSyncInterval(hours: Long) {
        viewModelScope.launch {
            settingsRepository.setSyncInterval(hours)
        }
    }
}
