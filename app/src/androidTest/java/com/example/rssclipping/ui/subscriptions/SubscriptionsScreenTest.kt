package com.example.rssclipping.ui.subscriptions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.rssclipping.MainActivity
import com.example.rssclipping.ui.navigation.AppNavigation
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SubscriptionsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.setContent {
            // Inicia la navegación completa para que el test sea más realista
            AppNavigation()
        }
    }

    @Test
    fun addSubscription_workflow() {
        // Navega a la pantalla de suscripciones
        composeTestRule.onNodeWithContentDescription("Más opciones").performClick()
        composeTestRule.onNodeWithText("Suscripciones").performClick()

        // 1. Hacer clic en el botón de añadir
        composeTestRule.onNodeWithContentDescription("Añadir suscripción").performClick()

        // 2. Verificar que el diálogo aparece
        composeTestRule.onNodeWithText("Nueva Suscripción").assertIsDisplayed()

        // 3. Escribir en el campo de texto y confirmar
        val testUrl = "http://test.com/feed"
        composeTestRule.onNodeWithText("Introduce la URL del feed RSS:").performTextInput(testUrl)
        composeTestRule.onNodeWithText("Añadir").performClick()

        // 4. Verificar que el diálogo desaparece
        composeTestRule.onNodeWithText("Nueva Suscripción").assertDoesNotExist()
    }
}
