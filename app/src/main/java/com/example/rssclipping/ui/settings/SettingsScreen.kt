package com.example.rssclipping.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

/**
 * Composable para la pantalla de Ajustes.
 * Permite al usuario configurar el intervalo de sincronización en segundo plano.
 * @param navController El controlador de navegación para volver a la pantalla anterior.
 * @param viewModel El [SettingsViewModel] que proporciona el estado y la lógica de negocio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Lista de opciones de intervalo de sincronización disponibles para el usuario (en horas)
    val syncIntervalOptions = listOf(1L, 4L, 8L, 24L)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        ) {
            Text(
                text = "Intervalo de sincronización en segundo plano",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // El selectableGroup asegura la accesibilidad para los lectores de pantalla
            Column(Modifier.selectableGroup()) {
                syncIntervalOptions.forEach { hours ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (hours == uiState.syncIntervalHours),
                                onClick = { viewModel.updateSyncInterval(hours) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (hours == uiState.syncIntervalHours),
                            onClick = null // El onClick ya se maneja en la Row para hacer toda la fila clickeable
                        )
                        Text(
                            text = "$hours horas",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
