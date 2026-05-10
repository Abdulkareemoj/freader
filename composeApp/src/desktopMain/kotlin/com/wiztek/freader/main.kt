package com.wiztek.freader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.di.jvmAppModule
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.settings.SettingsPersistence
import org.koin.core.context.startKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

fun main() = application {
    System.setProperty("kotlinx.coroutines.main.delay", "true")

    val koinApp = startKoin {
        modules(commonAppModule, jvmAppModule)
    }

    // Initialize Settings
    SettingsManager.init(koinApp.koin.get<SettingsPersistence>())
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Freader",
    ) {
        App()
    }
}
