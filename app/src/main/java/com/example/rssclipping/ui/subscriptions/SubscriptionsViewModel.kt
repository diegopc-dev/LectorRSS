package com.example.rssclipping.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de gestión de suscripciones.
 */
@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    /**
     * Un StateFlow que emite la lista de suscripciones.
     * Se obtiene directamente del repositorio y se convierte en un StateFlow para que la UI
     * pueda observarlo de forma segura y eficiente.
     */
    val subscriptions: StateFlow<List<SubscriptionEntity>> = subscriptionRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Añade una nueva suscripción a partir de una URL.
     * La operación se lanza en una corrutina de viewModelScope.
     */
    fun addSubscription(url: String) {
        viewModelScope.launch {
            subscriptionRepository.addSubscription(url)
        }
    }

    /**
     * Actualiza una suscripción existente.
     */
    fun updateSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            subscriptionRepository.updateSubscription(subscription)
        }
    }

    /**
     * Elimina una suscripción.
     */
    fun deleteSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            subscriptionRepository.deleteSubscription(subscription)
        }
    }
}
