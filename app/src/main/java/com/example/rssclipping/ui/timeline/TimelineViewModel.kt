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
 */
data class TimelineUiState(
    val allArticles: List<ArticleEntity> = emptyList(),
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val selectedSubscription: SubscriptionEntity? = null
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
 * Gestiona el estado de la UI y la lógica de negocio.
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
            // Cada vez que cualquiera de ellos emita un nuevo valor, este bloque se ejecutará.
            combine(
                feedRepository.getAllArticles(),
                subscriptionRepository.getAll()
            ) { articles, subscriptions ->
                TimelineUiState(allArticles = articles, subscriptions = subscriptions)
            }.collect { newState ->
                // Actualiza el estado de la UI, manteniendo el filtro seleccionado.
                _uiState.update {
                    it.copy(allArticles = newState.allArticles, subscriptions = newState.subscriptions)
                }
            }
        }
    }

    /**
     * Se llama cuando el usuario selecciona una suscripción diferente en el menú de filtro.
     * @param subscription La suscripción seleccionada, o null para mostrar todos los artículos.
     */
    fun onFilterChanged(subscription: SubscriptionEntity?) {
        _uiState.update { it.copy(selectedSubscription = subscription) }
    }
}
