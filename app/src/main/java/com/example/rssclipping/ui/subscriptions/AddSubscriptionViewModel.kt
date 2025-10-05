package com.example.rssclipping.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de "Añadir Suscripción".
 * Se encarga de la lógica de negocio para añadir una nueva suscripción y orquestar
 * la sincronización inicial de sus artículos.
 *
 * @property subscriptionRepository Repositorio para gestionar los datos de las suscripciones.
 * @property feedRepository Repositorio para gestionar los datos de los artículos.
 */
@HiltViewModel
class AddSubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val feedRepository: FeedRepository
) : ViewModel() {

    /**
     * El estado de la UI, encapsulado en un [MutableStateFlow] para ser observado por el Composable.
     */
    private val _uiState = MutableStateFlow(AddSubscriptionUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Un flujo de eventos de un solo uso para comunicar acciones a la UI (ej. navegar hacia atrás).
     */
    private val _events = MutableSharedFlow<AddSubscriptionEvent>()
    val events = _events.asSharedFlow()

    /**
     * Actualiza la URL en el estado de la UI cada vez que el usuario escribe en el campo de texto.
     * @param newUrl El nuevo texto introducido por el usuario.
     */
    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(url = newUrl) }
    }

    /**
     * Guarda la nueva suscripción. Orquesta la creación de la suscripción en la base de datos
     * y la posterior sincronización de sus artículos desde la red.
     */
    fun saveSubscription() {
        viewModelScope.launch {
            val url = _uiState.value.url
            if (url.isNotBlank()) {
                val newId = subscriptionRepository.addSubscription(url)
                feedRepository.syncSubscription(url, newId)
                _events.emit(AddSubscriptionEvent.NavigateBack)
            }
        }
    }
}

/**
 * Representa el estado de la UI para la pantalla "Añadir Suscripción".
 * @property url La URL del feed RSS que el usuario está introduciendo.
 */
data class AddSubscriptionUiState(val url: String = "")

/**
 * Define los eventos que el ViewModel puede enviar a la UI para acciones de un solo uso.
 */
sealed class AddSubscriptionEvent {
    /**
     * Indica a la UI que debe navegar hacia la pantalla anterior.
     */
    object NavigateBack : AddSubscriptionEvent()
}
