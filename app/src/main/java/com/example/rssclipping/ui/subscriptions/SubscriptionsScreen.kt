package com.example.rssclipping.ui.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.rssclipping.data.local.database.model.SubscriptionEntity

/**
 * Composable principal para la pantalla de gestión de suscripciones.
 * Muestra la lista de suscripciones y permite al usuario añadirlas, editarlas y eliminarlas.
 * @param navController El controlador de navegación para volver a la pantalla anterior.
 * @param viewModel El [SubscriptionsViewModel] que proporciona el estado y la lógica de negocio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    navController: NavController,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    // Estado para controlar la visibilidad del diálogo de añadir
    var showAddDialog by remember { mutableStateOf(false) }
    // Estado para guardar la suscripción que se está editando, o null si ninguna
    var subscriptionToEdit by remember { mutableStateOf<SubscriptionEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suscripciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir suscripción")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subscriptions, key = { it.id }) { subscription ->
                SubscriptionItem(
                    subscription = subscription,
                    onEdit = { subscriptionToEdit = subscription },
                    onDelete = { viewModel.deleteSubscription(subscription) }
                )
            }
        }

        // Muestra el diálogo de añadir si showAddDialog es true
        if (showAddDialog) {
            AddSubscriptionDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { url ->
                    viewModel.addSubscription(url)
                    showAddDialog = false
                }
            )
        }

        // Muestra el diálogo de editar si hay una suscripción para editar
        subscriptionToEdit?.let { subscription ->
            EditSubscriptionDialog(
                subscription = subscription,
                onDismiss = { subscriptionToEdit = null },
                onConfirm = { updatedSubscription ->
                    viewModel.updateSubscription(updatedSubscription)
                    subscriptionToEdit = null
                }
            )
        }
    }
}

/**
 * Diálogo para añadir una nueva suscripción a partir de una URL.
 * @param onDismiss Lambda que se ejecuta cuando el usuario cierra el diálogo.
 * @param onConfirm Lambda que se ejecuta cuando el usuario confirma la acción, pasando la URL introducida.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSubscriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Suscripción") },
        text = {
            Column {
                Text("Introduce la URL del feed RSS:")
                TextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(url) }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo para editar una suscripción existente.
 * @param subscription La suscripción a editar.
 * @param onDismiss Lambda que se ejecuta cuando el usuario cierra el diálogo.
 * @param onConfirm Lambda que se ejecuta cuando el usuario confirma la acción, pasando la suscripción con los datos actualizados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSubscriptionDialog(
    subscription: SubscriptionEntity,
    onDismiss: () -> Unit,
    onConfirm: (SubscriptionEntity) -> Unit
) {
    var name by remember { mutableStateOf(subscription.name) }
    var category by remember { mutableStateOf(subscription.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Suscripción") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val updatedSubscription = subscription.copy(name = name, category = category)
                onConfirm(updatedSubscription) 
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
