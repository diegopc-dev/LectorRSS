package com.example.rssclipping.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel para la pantalla que muestra la lista de artículos de una suscripción.
 *
 * @param feedRepository Repositorio para obtener los datos de los artículos.
 * @param savedStateHandle Manejador del estado guardado, usado aquí para obtener los argumentos de navegación (el ID de la suscripción).
 */
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    feedRepository: FeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subscriptionId: Long = checkNotNull(savedStateHandle["subscriptionId"])

    /**
     * Un StateFlow que emite la lista de artículos para la suscripción actual.
     * La UI observará este Flow para mostrar los artículos y actualizarse automáticamente.
     */
    val articles: StateFlow<List<ArticleEntity>> = feedRepository.getArticles(subscriptionId)
        .stateIn(
            scope = viewModelScope,
            // El Flow se inicia cuando la UI está suscrita y se detiene 5s después para ahorrar recursos.
            started = SharingStarted.WhileSubscribed(5000L),
            // El valor inicial mientras se cargan los datos de la BD es una lista vacía.
            initialValue = emptyList()
        )
}
