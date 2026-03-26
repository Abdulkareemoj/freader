package com.wiztek.freader

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.di.jvmAppModule
import org.koin.core.context.startKoin

fun main() = application {
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
