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

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    feedRepository: FeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Hilt nos proporciona el SavedStateHandle, que contiene los argumentos de navegación.
    private val subscriptionId: Long = checkNotNull(savedStateHandle["subscriptionId"])

    // Obtenemos el Flow de artículos del repositorio, pasándole el ID de la suscripción.
    val articles: StateFlow<List<ArticleEntity>> = feedRepository.getArticles(subscriptionId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}
