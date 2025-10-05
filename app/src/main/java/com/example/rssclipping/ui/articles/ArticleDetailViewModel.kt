package com.example.rssclipping.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.repository.FeedRepository
import com.example.rssclipping.navigation.KEY_ARTICLE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    feedRepository: FeedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId: Long = checkNotNull(savedStateHandle[KEY_ARTICLE_ID])

    val article: StateFlow<ArticleEntity?> = feedRepository.getArticle(articleId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )
}
