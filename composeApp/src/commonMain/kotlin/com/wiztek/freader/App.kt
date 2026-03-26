package com.wiztek.freader

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.wiztek.freader.navigation.VoyagerScreen
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.theme.AppTheme
import com.wiztek.freader.ui.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun App() {
    val isDarkMode by SettingsManager.isDarkMode.collectAsState()

    AppTheme(isDarkTheme = isDarkMode) {
        // Start from Onboarding for first run experience
        Navigator(VoyagerScreen.Onboarding) { navigator ->
            BoxWithConstraints {
                val screenWidth = maxWidth

                if (screenWidth < 700.dp) {
                    MobileLayout(navigator)
                } else {
                    DesktopLayout(navigator)
                }
            }
        }
    }
}
