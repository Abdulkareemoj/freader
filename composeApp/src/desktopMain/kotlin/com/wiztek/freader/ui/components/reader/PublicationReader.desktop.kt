package com.wiztek.freader.ui.components.reader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.wiztek.freader.library.model.LibraryBook

@Composable
actual fun PublicationReader(
    book: LibraryBook,
    modifier: Modifier,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit
) {
    val state = rememberWebViewState("about:blank")
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()

    // Register a handler for when the reader position changes in JS
    LaunchedEffect(jsBridge) {
        jsBridge.register(
            "onPositionChanged"
        ) { id, args ->
            // args will contain { progress: Double, locator: String }
            // For now, let's just log or dummy update
            // onProgressChanged(progress, locator)
        }
    }

    // Load the reader shell
    LaunchedEffect(book) {
        // Here we would load the local HTML shell that initializes Readium TS
        // For now, we'll just show a placeholder or the book path
        state.loadHtml(
            """
            <html>
                <head>
                    <style>
                        body { background: #121212; color: white; font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
                    </style>
                </head>
                <body>
                    <div style="text-align: center;">
                        <h1>Readium TS Shell</h1>
                        <p>Loading: ${book.title}</p>
                        <p>Path: ${book.filePath}</p>
                        <button onclick="window.freader.onPositionChanged(0.5, '{}')">Simulate Progress</button>
                    </div>
                    <script>
                        // Placeholder for Readium TS Initialization
                        console.log("Initializing Readium for " + "${book.filePath}");
                    </script>
                </body>
            </html>
            """.trimIndent()
        )
    }

    WebView(
        state = state,
        modifier = modifier.fillMaxSize(),
        navigator = navigator,
        webViewJsBridge = jsBridge
    )
}
