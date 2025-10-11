package com.example.rssclipping.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.ArticleWithSubscription
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
 * @param savedStateHandle Manejador del estado guardado para obtener el ID de la suscripción.
 */
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    feedRepository: FeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subscriptionId: Long = checkNotNull(savedStateHandle["subscriptionId"])

    /**
     * Un StateFlow que emite la lista de artículos (con su suscripción) para la suscripción actual.
     * La UI observará este Flow para mostrar los artículos.
     */
    val articles: StateFlow<List<ArticleWithSubscription>> = feedRepository.getArticles(subscriptionId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}
