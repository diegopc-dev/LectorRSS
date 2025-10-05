package com.example.rssclipping.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rssclipping.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSubscriptionUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddSubscriptionEvent>()
    val events = _events.asSharedFlow()

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(url = newUrl) }
    }

    fun saveSubscription() {
        viewModelScope.launch {
            val url = _uiState.value.url
            if (url.isNotBlank()) {
                subscriptionRepository.addSubscription(url)
                _events.emit(AddSubscriptionEvent.NavigateBack)
            }
        }
    }
}

data class AddSubscriptionUiState(val url: String = "")

sealed class AddSubscriptionEvent {
    object NavigateBack : AddSubscriptionEvent()
}
