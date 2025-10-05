package com.example.rssclipping.data.network

import com.example.rssclipping.data.network.model.NetworkArticle
import com.example.rssclipping.data.network.model.NetworkFeed
import com.rometools.modules.mediarss.MediaModule
import com.rometools.rome.io.SyndFeedInput
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.io.StringReader

/**
 * Se encarga de la comunicación de red para obtener los datos de los feeds RSS.
 * Utiliza Ktor para las peticiones HTTP y Rome para analizar el XML.
 *
 * @param httpClient El cliente HTTP de Ktor, inyectado para facilitar las pruebas.
 */
class RssNetworkDataSource(
    private val httpClient: HttpClient
) {

    /**
     * Busca y parsea un feed RSS completo desde una URL dada.
     *
     * @param url La URL del feed RSS a obtener.
     * @return Un [NetworkFeed] que contiene los metadatos del canal y la lista de artículos.
     * @throws Exception Si hay un error de red o de parseo.
     */
    suspend fun fetchFeed(url: String): NetworkFeed {
        val feedXml = httpClient.get(url).bodyAsText()

        val input = SyndFeedInput()
        val feed = input.build(StringReader(feedXml))

        // Extraer metadatos del canal
        val channelTitle = feed.title ?: ""
        val channelIconUrl = feed.image?.url ?: ""

        // Mapear los artículos
        val articles = feed.entries.mapNotNull { entry ->
            val guid = entry.uri ?: entry.link ?: return@mapNotNull null

            // Extraer la miniatura del módulo de Media RSS
            val mediaModule = entry.getModule(MediaModule.URI) as? MediaModule
            val thumbnailUrl = mediaModule?.metadata?.thumbnail?.firstOrNull()?.url ?: ""

            NetworkArticle(
                guid = guid,
                title = entry.title ?: "",
                link = entry.link ?: "",
                pubDate = entry.publishedDate?.toString() ?: "",
                content = entry.description?.value ?: "",
                thumbnailUrl = thumbnailUrl
            )
        }

        // Devolver el objeto completo
        return NetworkFeed(
            channelTitle = channelTitle,
            channelIconUrl = channelIconUrl,
            articles = articles
        )
    }
}
