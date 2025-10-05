package com.example.rssclipping.data.network.model

/**
 * Representa un feed RSS completo obtenido de la red.
 * Contiene tanto los metadatos del canal como la lista de artículos.
 */
data class NetworkFeed(
    val channelTitle: String,
    val channelIconUrl: String,
    val articles: List<NetworkArticle>
)
