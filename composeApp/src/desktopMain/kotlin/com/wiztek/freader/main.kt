package com.wiztek.freader

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window

import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.di.jvmAppModule
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.settings.SettingsPersistence
import org.koin.core.context.startKoin

fun main() = application {
    val koinApp = startKoin {
        modules(commonAppModule, jvmAppModule)
    }

    // Initialize Settings before first composition
    val settingsPersistence = koinApp.koin.get<SettingsPersistence>()
    SettingsManager.init(settingsPersistence)

    Window(
        onCloseRequest = {
            exitApplication()
        },
        title = "Freader",
        state = rememberWindowState(width = 1400.dp, height = 900.dp),
    ) {
        App()
    }
}
