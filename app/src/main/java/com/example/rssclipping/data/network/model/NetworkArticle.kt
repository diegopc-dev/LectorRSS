package com.example.rssclipping.data.network.model

/**
 * Representa un artículo obtenido directamente desde la fuente de red (el feed RSS).
 * Es un DTO (Data Transfer Object) específico de la capa de red.
 */
data class NetworkArticle(
    val guid: String,
    val title: String,
    val link: String,
    val pubDate: String,
    val content: String
)
