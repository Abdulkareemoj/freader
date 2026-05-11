package com.wiztek.freader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.di.jvmAppModule
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.settings.SettingsPersistence
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.startKoin
import org.koin.core.component.get
import java.io.File

fun main() = application {
    val koinApp = remember {
        startKoin {
            modules(commonAppModule, jvmAppModule)
        }
    }

    var isBrowserInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            KCEF.init(
                builder = {
                    installDir(File("kcef-bundle"))
                },
                onError = { it?.printStackTrace() },
                onSuccess = {
                    isBrowserInitialized = true
                }
            )
        }
    }

    // Initialize Settings
    SettingsManager.init(koinApp.koin.get<SettingsPersistence>())
    
    Window(
        onCloseRequest = {
            KCEF.disposeBlocking()
            exitApplication()
        },
        title = "Freader",
    ) {
        if (isBrowserInitialized) {
            App()
        } else {
            LoadingBrowserEngine()
        }
    }
}

@Composable
fun LoadingBrowserEngine() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Initializing Reader Engine...")
        }
    }
}
