package com.example.rssclipping.ui.settings

import app.cash.turbine.test
import com.example.rssclipping.data.preferences.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class SettingsViewModelTest {

    @RelaxedMockK
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var sut: SettingsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState SHOULD reflect syncIntervalHours from repository`() = runTest {
        // Arrange: Configurar el mock para que emita un intervalo específico
        val expectedInterval = 8L
        coEvery { settingsRepository.syncIntervalHours } returns flowOf(expectedInterval)

        // Act: Crear el ViewModel después de configurar el mock
        sut = SettingsViewModel(settingsRepository)

        // Assert: Probar el StateFlow con Turbine
        sut.uiState.test {
            val initialState = awaitItem()
            assertEquals(expectedInterval, initialState.syncIntervalHours)
        }
    }

    @Test
    fun `updateSyncInterval SHOULD call repository's setSyncInterval`() = runTest {
        // Arrange
        coEvery { settingsRepository.syncIntervalHours } returns flowOf(4L) // Valor inicial
        sut = SettingsViewModel(settingsRepository)
        val newInterval = 24L

        // Act
        sut.updateSyncInterval(newInterval)

        // Assert: Verificar que se llamó al método del repositorio con el nuevo intervalo
        coVerify { settingsRepository.setSyncInterval(newInterval) }
    }
}
