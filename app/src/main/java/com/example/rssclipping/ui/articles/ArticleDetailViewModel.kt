package com.example.rssclipping.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.ArticleWithSubscription
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.navigation.KEY_ARTICLE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel para la pantalla de detalle de un artículo.
 *
 * @param feedRepository Repositorio para obtener los datos del artículo.
 * @param savedStateHandle Manejador del estado guardado, usado para obtener el ID del artículo.
 */
@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    feedRepository: FeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId: Long = checkNotNull(savedStateHandle[KEY_ARTICLE_ID])

    /**
     * Un StateFlow que emite el artículo actual con su suscripción asociada.
     * La UI observará este Flow para mostrar los detalles.
     */
    val articleWithSubscription: StateFlow<ArticleWithSubscription?> = feedRepository.getArticle(articleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )
}
