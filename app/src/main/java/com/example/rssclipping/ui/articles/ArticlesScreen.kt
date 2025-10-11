package com.example.rssclipping.ui.articles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rssclipping.navigation.Screen
import com.example.rssclipping.ui.shared.ArticleItem

/**
 * Composable que representa la pantalla que muestra la lista de artículos de una suscripción.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    viewModel: ArticlesViewModel = hiltViewModel(),
    navController: NavController
) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            // El título ahora se toma del primer artículo, que ya contiene el nombre de la suscripción.
            TopAppBar(
                title = { Text(articles.firstOrNull()?.subscription?.name ?: "Artículos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (articles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay artículos para esta suscripción.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(articles, key = { it.article.id }) { articleWithSubscription ->
                    ArticleItem(
                        articleWithSubscription = articleWithSubscription,
                        onClick = { 
                            navController.navigate(Screen.ArticleDetail.createRoute(articleWithSubscription.article.id))
                        }
                    )
                }
            }
        }
    }
}
