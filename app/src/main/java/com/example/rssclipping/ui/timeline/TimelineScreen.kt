package com.example.rssclipping.ui.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rssclipping.data.local.database.model.ArticleEntity
import com.example.rssclipping.data.local.database.model.SubscriptionEntity
import com.example.rssclipping.ui.navigation.Screen

/**
 * Composable principal de la pantalla del Timeline. Muestra la lista de artículos
 * y gestiona la interacción del usuario (navegación, filtrado, refresco).
 * @param navController El controlador de navegación para moverse a otras pantallas.
 * @param viewModel El [TimelineViewModel] que proporciona el estado de la UI.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TimelineScreen(
    navController: NavController,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Estado para el gesto de "tirar para refrescar"
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = viewModel::refreshFeeds
    )

    Scaffold(
        topBar = {
            TimelineTopAppBar(
                state = uiState,
                onFilterChanged = viewModel::onFilterChanged,
                onNavigateToSubscriptions = { navController.navigate(Screen.Subscriptions.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState) // Aplica el modificador de pull-refresh
        ) {
            if (uiState.allArticles.isEmpty() && !uiState.isRefreshing) {
                // Muestra un mensaje si la lista está vacía y no se está refrescando
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay artículos. Añade una suscripción para empezar.")
                }
            } else {
                // Muestra la lista de artículos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(uiState.displayedArticles, key = { it.id }) { article ->
                        ArticleItem(article = article) {
                            navController.navigate(Screen.ArticleDetail.createRoute(article.id))
                        }
                    }
                }
            }

            // Indicador visual que se muestra en la parte superior durante el refresco
            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * Composable para la barra superior de la pantalla del Timeline.
 * Contiene el título y los menús de acciones (filtrar y opciones).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineTopAppBar(
    state: TimelineUiState,
    onFilterChanged: (SubscriptionEntity?) -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(state.selectedSubscription?.name ?: "Timeline") },
        actions = {
            // Botón de Filtro
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrar por suscripción")
            }
            DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                DropdownMenuItem(text = { Text("Todos") }, onClick = { onFilterChanged(null); showFilterMenu = false })
                state.subscriptions.forEach { subscription ->
                    DropdownMenuItem(
                        text = { Text(subscription.name) },
                        onClick = { onFilterChanged(subscription); showFilterMenu = false }
                    )
                }
            }

            // Botón de Opciones
            IconButton(onClick = { showOptionsMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
            }
            DropdownMenu(expanded = showOptionsMenu, onDismissRequest = { showOptionsMenu = false }) {
                DropdownMenuItem(text = { Text("Suscripciones") }, onClick = onNavigateToSubscriptions)
                DropdownMenuItem(text = { Text("Ajustes") }, onClick = onNavigateToSettings)
            }
        }
    )
}

/**
 * Composable que representa un único artículo en la lista del timeline.
 * @param article La entidad del artículo a mostrar.
 * @param onClick La acción a ejecutar cuando se pulsa sobre el elemento.
 */
@Composable
private fun ArticleItem(
    article: ArticleEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (article.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = article.thumbnailUrl,
                    contentDescription = "Miniatura del artículo",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = article.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = article.pubDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
