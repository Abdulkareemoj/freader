package com.wiztek.freader.ui.components.reader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.*
import com.wiztek.freader.library.model.LibraryBook
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    setNavigationCallback: (((String) -> Unit)?) -> Unit
) {
    val streamer = koinInject<DesktopStreamer>()
    val strategyFactory = koinInject<ReaderStrategyFactory>()

    var serverPort by remember { mutableStateOf(0) }
    var manifest by remember { mutableStateOf<ReadiumManifest?>(null) }
    var loadingError by remember { mutableStateOf<String?>(null) }
    var jsInjected by remember { mutableStateOf(false) }
    val settings by SettingsManager.settings.collectAsState()
    val json = remember { Json { ignoreUnknownKeys = true } }
    val scope = rememberCoroutineScope()

    val state = rememberWebViewState("about:blank")
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()

    LaunchedEffect(setNavigationCallback) {
        setNavigationCallback { href: String ->
            val escapedHref = href.replace("'", "\\'")
            navigator.evaluateJavaScript("window.freader.navigateToHref('$escapedHref')")
        }
    }

    // 1. Initialize streamer
    LaunchedEffect(book.id) {
        loadingError = null
        jsInjected = false
        try {
            streamer.start()
            serverPort = streamer.port

            val strategy = strategyFactory.create(book.format)
            if (strategy is EpubReaderStrategy) {
                manifest = strategy.getManifest(book)
            }

            if (serverPort > 0) {
                state.content = WebContent.Url("http://localhost:$serverPort/assets/index.html")
            } else {
                loadingError = "Streamer failed to start"
            }
        } catch (e: Exception) {
            loadingError = "Failed to start reader: ${e.message}"
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
                } catch (e: Exception) { e.printStackTrace() }
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
                    json.decodeFromString<NavigateToHrefMessage>(message.params)
                } catch (e: Exception) { e.printStackTrace() }
            }
        })
    }

    // 3. Inject reader JS when WebView finishes loading
    LaunchedEffect(state.loadingState, manifest, serverPort) {
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Finished && serverPort > 0 && !jsInjected) {
            try {
                val normalizedPath = book.filePath.replace("\\", "/")
                val encodedPath = normalizedPath.split("/").joinToString("/") {
                    URLEncoder.encode(it, StandardCharsets.UTF_8.toString()).replace("+", "%20")
                }

                val baseUrl = "http://localhost:$serverPort/$encodedPath/"
                val locator = book.lastReadLocator ?: "null"
                val manifestJson = manifest?.let { json.encodeToString(it) } ?: "null"
                val format = book.format.name

                val escapedBaseUrl = baseUrl.replace("'", "\\'")
                val escapedLocator = if (locator != "null") locator.replace("'", "\\'") else "null"
                val escapedManifestJson = manifestJson.replace("'", "\\'").replace("\n", "")

                val js = "window.initReader('$escapedBaseUrl', '$escapedLocator', '$format', '$escapedManifestJson')"
                navigator.evaluateJavaScript(js)
                jsInjected = true
            } catch (e: Exception) {
                println("JS injection failed: ${e.message}")
                // Retry after a short delay
                delay(500)
            }
        }
    }

    // 4. Fallback: retry JS injection if WebView finishes but injection was missed
    LaunchedEffect(state.loadingState) {
        if (state.loadingState is LoadingState.Finished && serverPort > 0 && !jsInjected) {
            delay(1000)
            if (!jsInjected) {
                loadingError = "Reader took too long to initialize. Try again."
            }
        }
    }

    // 5. Sync reader settings
    LaunchedEffect(settings, state.loadingState) {
        if (state.loadingState is LoadingState.Finished && jsInjected) {
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
                "pageMargins" to settings.pageMargins.toString(),
                "wordSpacing" to settings.wordSpacing.toString(),
                "letterSpacing" to settings.letterSpacing.toString()
            )
            val settingsJson = json.encodeToString(settingsMap)
            navigator.evaluateJavaScript("window.updateSettings('${settingsJson.replace("'", "\\'")}')")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        WebView(
            state = state,
            modifier = Modifier.fillMaxSize(),
            navigator = navigator,
            webViewJsBridge = jsBridge
        )

        if (loadingError != null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = loadingError ?: "Failed to load reader",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { loadingError = null }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
