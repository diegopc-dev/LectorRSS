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
 * Expone la lista de suscripciones y los métodos para modificarlas (añadir, actualizar, eliminar).
 */
@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    /**
     * Un [StateFlow] que emite la lista de suscripciones del usuario.
     * Se obtiene directamente del repositorio y se convierte en un StateFlow para que la UI
     * pueda observarlo de forma segura y eficiente. El `stateIn` asegura que el Flow subyacente
     * solo esté activo cuando la UI está visible.
     */
    val subscriptions: StateFlow<List<SubscriptionEntity>> = subscriptionRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Pide al repositorio que añada una nueva suscripción a partir de una URL.
     * La operación se lanza en el [viewModelScope] para que se gestione su ciclo de vida.
     * @param url La URL del feed RSS a añadir.
     */
    fun addSubscription(url: String) {
        viewModelScope.launch {
            subscriptionRepository.addSubscription(url)
        }
    }

    /**
     * Pide al repositorio que actualice una suscripción existente.
     * @param subscription La entidad [SubscriptionEntity] con los datos actualizados.
     */
    fun updateSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            subscriptionRepository.updateSubscription(subscription)
        }
    }

    /**
     * Pide al repositorio que elimine una suscripción.
     * @param subscription La entidad [SubscriptionEntity] a eliminar.
     */
    fun deleteSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            subscriptionRepository.deleteSubscription(subscription)
        }
    }
}
