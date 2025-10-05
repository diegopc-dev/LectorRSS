package com.example.rssclipping.data.network.model

/**
 * Representa un feed RSS completo obtenido de la red.
 * Contiene tanto los metadatos del canal como la lista de artículos.
 *
 * @property channelTitle El título del canal del feed (ej. "Android Developers Blog").
 * @property channelIconUrl La URL del icono o favicon del canal.
 * @property articles La lista de artículos individuales que contiene el feed.
 */
data class NetworkFeed(
    val channelTitle: String,
    val channelIconUrl: String,
    val articles: List<NetworkArticle>
)
