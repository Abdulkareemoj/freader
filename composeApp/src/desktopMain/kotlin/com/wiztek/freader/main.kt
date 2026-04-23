package com.wiztek.freader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.di.jvmAppModule
import org.koin.core.context.startKoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
fun main() = application {
    System.setProperty("kotlinx.coroutines.main.delay", "true")

    startKoin {
        modules(commonAppModule, jvmAppModule)
    }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Freader",
    ) {
        App()
    }
}
