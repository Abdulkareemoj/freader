package com.wiztek.freader.ui.components.reader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.*
import com.wiztek.freader.library.model.LibraryBook
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.wiztek.freader.reader.DesktopStreamer
import com.wiztek.freader.reader.EpubReaderStrategy
import com.wiztek.freader.reader.ReaderStrategyFactory
import com.wiztek.freader.reader.model.ReadiumManifest
import com.wiztek.freader.settings.ReaderTheme
import com.wiztek.freader.settings.SettingsManager
import org.koin.compose.koinInject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Serializable
private data class ProgressMessage(val progress: Double, val locator: String? = null)

@Serializable
private data class NavigateToHrefMessage(val href: String)

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun PublicationReader(
    book: LibraryBook,
    modifier: Modifier,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit,
    setNavigationCallback: (((String) -> Unit)?) -> Unit // Updated parameter
) {
    val streamer = koinInject<DesktopStreamer>()
    val strategyFactory = koinInject<ReaderStrategyFactory>()
    
    var serverPort by remember { mutableStateOf(0) }
    var manifest by remember { mutableStateOf<ReadiumManifest?>(null) }
    val settings by SettingsManager.settings.collectAsState()
    val json = remember { Json { ignoreUnknownKeys = true } }

    val state = rememberWebViewState("about:blank")
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()

    // Sync external navigation callback
    LaunchedEffect(setNavigationCallback) {
        setNavigationCallback { href: String ->
            val escapedHref = href.replace("'", "\\'")
            navigator.evaluateJavaScript("window.freader.navigateToHref('$escapedHref')")
        }
    }

    // 1. Initialize streamer and fetch book manifest
    LaunchedEffect(book) {
        try {
            streamer.start()
            serverPort = streamer.port
            
            val strategy = strategyFactory.create(book.format)
            if (strategy is EpubReaderStrategy) {
                manifest = strategy.getManifest(book)
            }

            // Load the shell URL once the streamer is ready
            if (serverPort > 0) {
                val shellUrl = "http://localhost:$serverPort/assets/index.html"
                state.content = WebContent.Url(shellUrl)
            }
        } catch (e: Exception) {
            println("PublicationReader Error: ${e.message}")
        }
    }

    // 2. JS Bridge Handlers
    LaunchedEffect(jsBridge) {
        jsBridge.register(object : IJsMessageHandler {
            override fun methodName(): String = "onPositionChanged"
            override fun handle(message: JsMessage, navigator: WebViewNavigator?, callback: (String) -> Unit) {
                try {
                    val data = json.decodeFromString<ProgressMessage>(message.params)
                    onProgressChanged(data.progress, data.locator)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        jsBridge.register(object : IJsMessageHandler {
            override fun methodName(): String = "onToggleControls"
            override fun handle(message: JsMessage, navigator: WebViewNavigator?, callback: (String) -> Unit) {
                onToggleControls()
            }
        })

        jsBridge.register(object : IJsMessageHandler {
            override fun methodName(): String = "navigateToHref"
            override fun handle(message: JsMessage, navigator: WebViewNavigator?, callback: (String) -> Unit) {
                try {
                    val data = json.decodeFromString<NavigateToHrefMessage>(message.params)
                    // This is for links clicked INSIDE the webview that we might want to handle in Kotlin
                    // but for now reader.js handles them.
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    // 3. Initialize reader in WebView once shell is loaded
    LaunchedEffect(state.loadingState, manifest, serverPort) {
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Finished && serverPort > 0) {
            // Encode the file path to handle spaces and special characters on Windows
            val normalizedPath = book.filePath.replace("\\", "/")
            val encodedPath = normalizedPath.split("/").joinToString("/") { 
                URLEncoder.encode(it, StandardCharsets.UTF_8.toString()).replace("+", "%20")
            }
            
            val baseUrl = "http://localhost:$serverPort/$encodedPath/"
            val locator = book.lastReadLocator ?: "null"
            val manifestJson = manifest?.let { json.encodeToString(it) } ?: "null"
            val format = book.format.name
            
            // Clean strings for JS injection
            val escapedBaseUrl = baseUrl.replace("'", "\\'")
            val escapedLocator = if (locator != "null") locator.replace("'", "\\'") else "null"
            val escapedManifestJson = manifestJson.replace("'", "\\'").replace("\n", "")
            
            val js = "window.initReader('$escapedBaseUrl', '$escapedLocator', '$format', '$escapedManifestJson')"
            navigator.evaluateJavaScript(js)
        }
    }

    // 4. Sync Settings
    LaunchedEffect(settings, state.loadingState) {
        if (state.loadingState is LoadingState.Finished) {
            val appearance = when (settings.theme) {
                ReaderTheme.SEPIA -> "readium-sepia-on"
                ReaderTheme.SOLARIZED -> "readium-default-on"
                ReaderTheme.PAPER -> if (settings.isDarkMode) "readium-night-on" else "readium-default-on"
            }
            val fontSize = "${(settings.fontScaling * 100).toInt()}%"
            val columnCount = if (settings.useTwoColumns) "2" else "1"
            val settingsMap = mapOf(
                "appearance" to appearance,
                "fontSize" to fontSize,
                "columnCount" to columnCount,
                "lineHeight" to settings.lineHeight.toString(),
                "pageMargins" to settings.pageMargins.toString()
            )
            val settingsJson = json.encodeToString(settingsMap)
            navigator.evaluateJavaScript("window.updateSettings('${settingsJson.replace("'", "\\'")}')")
        }
    }

    WebView(
        state = state,
        modifier = modifier.fillMaxSize(),
        navigator = navigator,
        webViewJsBridge = jsBridge
    )
}
