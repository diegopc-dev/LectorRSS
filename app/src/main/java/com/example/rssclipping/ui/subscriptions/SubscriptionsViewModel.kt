package com.example.rssclipping.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel para la pantalla de gestión de suscripciones.
 * Se encarga de obtener los datos del repositorio y exponerlos a la UI.
 */
@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    /**
     * Un StateFlow que emite la lista actual de suscripciones.
     * La UI observará este Flow para actualizarse automáticamente.
     * Usamos `stateIn` para convertir el Flow "frío" del repositorio en un Flow "caliente"
     * que puede ser compartido por múltiples observadores y que mantiene el último valor.
     */
    val subscriptions: StateFlow<List<SubscriptionEntity>> = subscriptionRepository.getAllSubscriptions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}
