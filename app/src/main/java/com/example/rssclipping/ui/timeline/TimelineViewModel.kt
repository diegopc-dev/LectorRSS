package com.example.rssclipping.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.ArticleWithSubscription
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Define el estado de la interfaz de usuario para la pantalla del Timeline.
 * @property allArticles La lista completa de todos los artículos con su suscripción asociada.
 * @property subscriptions La lista de todas las suscripciones del usuario.
 * @property selectedSubscription La suscripción actualmente seleccionada para filtrar, o null.
 * @property isRefreshing true si hay una operación de refresco en curso.
 */
data class TimelineUiState(
    val allArticles: List<ArticleWithSubscription> = emptyList(),
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val selectedSubscription: SubscriptionEntity? = null,
    val isRefreshing: Boolean = false
) {
    /**
     * La lista de artículos que se deben mostrar en la UI.
     * Si hay una suscripción seleccionada, filtra los artículos, si no, los muestra todos.
     */
    val displayedArticles: List<ArticleWithSubscription>
        get() = if (selectedSubscription != null) {
            allArticles.filter { it.subscription.id == selectedSubscription.id }
        } else {
            allArticles
        }
}

/**
 * ViewModel para la pantalla del Timeline.
 * Gestiona el estado de la UI ([TimelineUiState]) y la lógica de negocio.
 */
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Combina los flows de todos los artículos y todas las suscripciones.
            combine(
                feedRepository.getAllArticles(),
                subscriptionRepository.getAll()
            ) { articles, subscriptions ->
                _uiState.update {
                    it.copy(allArticles = articles, subscriptions = subscriptions)
                }
            }.collect {}
        }

        // Sincroniza los feeds al iniciar el ViewModel por primera vez.
        refreshFeeds()
    }

    /**
     * Actualiza la suscripción seleccionada para filtrar.
     * @param subscription La suscripción seleccionada, o null para mostrar todos.
     */
    fun onFilterChanged(subscription: SubscriptionEntity?) {
        _uiState.update { it.copy(selectedSubscription = subscription) }
    }

    /**
     * Lanza la sincronización de todos los feeds, mostrando el indicador de refresco.
     */
    fun refreshFeeds() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            feedRepository.syncAllSubscriptions()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}
