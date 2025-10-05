package com.example.rssclipping.data.repository

import com.example.rssclipping.data.local.database.dao.ArticleDao
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.network.RssNetworkDataSource
import com.example.rssclipping.data.network.model.NetworkArticle
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class FeedRepositoryImpl(
    private val articleDao: ArticleDao,
    private val networkDataSource: RssNetworkDataSource
) : FeedRepository {

    override fun getArticles(subscriptionId: Long): Flow<List<ArticleEntity>> {
        return articleDao.getArticlesBySubscriptionId(subscriptionId)
    }

    override fun getArticle(id: Long): Flow<ArticleEntity?> {
        return articleDao.getById(id)
    }

    override suspend fun syncSubscription(subscriptionUrl: String, subscriptionId: Long) {
        try {
            // Obtenemos el feed completo y extraemos la lista de artículos
            val networkArticles = networkDataSource.fetchFeed(subscriptionUrl).articles
            val articleEntities = networkArticles.map { it.toEntity(subscriptionId) }
            articleDao.insertAll(articleEntities)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun NetworkArticle.toEntity(subscriptionId: Long) = ArticleEntity(
    subscriptionId = subscriptionId,
    guid = guid,
    title = title,
    link = link,
    pubDate = pubDate,
    content = content
)
