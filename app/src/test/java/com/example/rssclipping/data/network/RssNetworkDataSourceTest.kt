package com.example.rssclipping.data.network

import com.example.rssclipping.data.network.model.NetworkArticle
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class RssNetworkDataSourceTest {

    private val fakeRssXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <rss version="2.0">
        <channel>
            <title>Test Feed</title>
            <item>
                <title>Test Title 1</title>
                <link>http://example.com/1</link>
                <guid>guid1</guid>
                <pubDate>Mon, 01 Jan 2024 12:00:00 GMT</pubDate>
            </item>
            <item>
                <title>Test Title 2</title>
                <link>http://example.com/2</link>
                <guid>guid2</guid>
                <pubDate>Tue, 02 Jan 2024 12:00:00 GMT</pubDate>
            </item>
        </channel>
        </rss>
    """

    @Test
    fun `fetchArticles returns mapped article list on success`() = runBlocking {
        // Arrange
        val mockEngine = MockEngine {
            respond(
                content = fakeRssXml,
                headers = headersOf(HttpHeaders.ContentType, "application/xml")
            )
        }
        val httpClient = HttpClient(mockEngine)
        val sut = RssNetworkDataSource(httpClient)
        val url = "https://example.com/rss"

        // Act
        val result: List<NetworkArticle> = sut.fetchArticles(url)

        // Assert
        assertEquals(2, result.size)
        assertEquals("guid1", result[0].guid)
        assertEquals("Test Title 1", result[0].title)
    }

    @Test
    fun `fetchArticles throws exception on network error`() {
        // Arrange
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        val httpClient = HttpClient(mockEngine)
        val sut = RssNetworkDataSource(httpClient)
        val url = "https://example.com/rss"

        // Act & Assert
        assertThrows<ClientRequestException> {
            runBlocking { sut.fetchArticles(url) }
        }
    }
}
