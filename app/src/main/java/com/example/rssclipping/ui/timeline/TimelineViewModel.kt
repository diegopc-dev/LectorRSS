package com.example.rssclipping.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.ArticleEntity
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
 * @property allArticles La lista completa de todos los artículos de la base de datos.
 * @property subscriptions La lista de todas las suscripciones del usuario.
 * @property selectedSubscription La suscripción actualmente seleccionada para filtrar, o null si no hay filtro.
 * @property isRefreshing true si hay una operación de refresco en curso.
 * @property displayedArticles Una lista computada que contiene los artículos a mostrar según el filtro aplicado.
 */
data class TimelineUiState(
    val allArticles: List<ArticleEntity> = emptyList(),
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val selectedSubscription: SubscriptionEntity? = null,
    val isRefreshing: Boolean = false
) {
    /**
     * La lista de artículos que se deben mostrar en la UI.
     * Si hay una suscripción seleccionada, filtra los artículos, si no, los muestra todos.
     */
    val displayedArticles: List<ArticleEntity>
        get() = if (selectedSubscription != null) {
            allArticles.filter { it.subscriptionId == selectedSubscription.id }
        } else {
            allArticles
        }
}

/**
 * ViewModel para la pantalla del Timeline.
 * Gestiona el estado de la UI ([TimelineUiState]) y la lógica de negocio, como el filtrado
 * y el refresco de los feeds.
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
            // Combina los flows de todos los artículos y todas las suscripciones. Este bloque se
            // ejecutará cada vez que cualquiera de las dos fuentes de datos emita un nuevo valor.
            combine(
                feedRepository.getAllArticles(),
                subscriptionRepository.getAll()
            ) { articles, subscriptions ->
                // Cuando los datos de la BD cambian, actualizamos el estado, pero manteniendo el filtro y el estado de refresco
                _uiState.update {
                    it.copy(allArticles = articles, subscriptions = subscriptions)
                }
            }.collect {}
        }

        // Sincroniza los feeds al iniciar el ViewModel por primera vez para asegurar datos frescos.
        refreshFeeds()
    }

    /**
     * Se llama cuando el usuario selecciona una suscripción diferente en el menú de filtro.
     * @param subscription La suscripción seleccionada, o null para mostrar todos los artículos.
     */
    fun onFilterChanged(subscription: SubscriptionEntity?) {
        _uiState.update { it.copy(selectedSubscription = subscription) }
    }

    /**
     * Lanza la sincronización de todos los feeds.
     * Muestra el indicador de refresco mientras dura la operación y lo oculta al terminar.
     */
    fun refreshFeeds() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            feedRepository.syncAllSubscriptions()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}
