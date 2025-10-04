package com.example.rssclipping.data.network

import com.example.rssclipping.data.network.model.NetworkArticle
import com.rometools.rome.io.SyndFeedInput
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.io.StringReader

/**
 * Se encarga de la comunicación de red para obtener los datos de los feeds RSS.
 * Es una clase especializada que solo tiene una responsabilidad: obtener datos de la red.
 * Utiliza Ktor para las peticiones HTTP y Rome para analizar el XML.
 *
 * @param httpClient El cliente HTTP de Ktor, inyectado para facilitar las pruebas.
 */
class RssNetworkDataSource(
    private val httpClient: HttpClient
) {

    /**
     * Busca y parsea los artículos de un feed RSS desde una URL dada.
     * Esta función es 'suspend' porque realiza operaciones de red asíncronas.
     *
     * @param url La URL del feed RSS a obtener.
     * @return Una lista de [NetworkArticle] que representan los artículos del feed.
     * @throws Exception Si hay un error de red (ej. 404, sin conexión) o un error de parseo.
     */
    suspend fun fetchArticles(url: String): List<NetworkArticle> {
        // 1. Usar Ktor para realizar una petición GET a la URL y obtener el cuerpo como texto.
        val feedXml = httpClient.get(url).bodyAsText()

        // 2. Preparar el parser de Rome. Rome trabaja con 'Readers', por lo que convertimos el String XML.
        val input = SyndFeedInput()
        val feed = input.build(StringReader(feedXml))

        // 3. Mapear las entradas del feed (SyndEntry de Rome) a nuestro modelo de red (NetworkArticle).
        // Usamos mapNotNull para descartar automáticamente cualquier entrada que no tenga un 'guid' o 'link'.
        return feed.entries.mapNotNull { entry ->
            // El 'guid' (o 'uri' en Rome) es el campo más fiable para la unicidad. Si no existe, usamos el 'link'.
            // Si ninguno de los dos existe, el artículo no es válido para nosotros y lo descartamos (return@mapNotNull null).
            val guid = entry.uri ?: entry.link ?: return@mapNotNull null
            NetworkArticle(
                guid = guid,
                title = entry.title ?: "", // Usamos el operador Elvis para evitar nulos.
                link = entry.link ?: "",
                pubDate = entry.publishedDate?.toString() ?: ""
            )
        }
    }
}
