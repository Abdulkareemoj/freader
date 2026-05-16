package com.wiztek.freader

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.di.jvmAppModule
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.settings.SettingsPersistence
import org.koin.core.context.startKoin

fun main() = application {
    val koinApp = remember {
        startKoin {
            modules(commonAppModule, jvmAppModule)
        }
    }

    // Initialize Settings safely within Compose context
    val settingsPersistence = remember { koinApp.koin.get<SettingsPersistence>() }
    LaunchedEffect(settingsPersistence) {
        SettingsManager.init(settingsPersistence)
    }

    Window(
        onCloseRequest = {
            exitApplication()
        },
        title = "Freader",
    ) {
        App()
    }
}
