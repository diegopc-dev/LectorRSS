package com.example.rssclipping.data.network.model

/**
 * Representa un artículo obtenido directamente desde la fuente de red (el feed RSS).
 * Es un DTO (Data Transfer Object) específico de la capa de red.
 *
 * @property guid El Identificador Único Global del artículo, usado para evitar duplicados.
 * @property title El título del artículo.
 * @property link La URL que enlaza al contenido completo del artículo.
 * @property pubDate La fecha de publicación del artículo, como un String.
 * @property content El cuerpo o contenido del artículo, usualmente en formato HTML.
 * @property thumbnailUrl La URL de la imagen en miniatura del artículo.
 */
data class NetworkArticle(
    val guid: String,
    val title: String,
    val link: String,
    val pubDate: String,
    val content: String,
    val thumbnailUrl: String
)
