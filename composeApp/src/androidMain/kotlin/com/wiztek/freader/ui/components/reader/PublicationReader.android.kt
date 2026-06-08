package com.wiztek.freader.ui.components.reader

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.engine.ReadiumEngine
import com.wiztek.freader.reader.ui.ReadiumFragmentRenderer
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun PublicationReader(
    book: LibraryBook,
    modifier: Modifier,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit,
    setNavigationCallback: (((String) -> Unit)?) -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var publication by remember { mutableStateOf<Publication?>(null) }
    val initialLocator = remember(book.lastReadLocator) {
        book.lastReadLocator?.let { Locator.fromJSON(JSONObject(it)) }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(book.filePath) {
        isLoading = true
        errorMessage = null
        publication = null

        if (book.filePath.isBlank()) {
            errorMessage = "No file path for \"${book.title}\". Use the Discover tab to import books."
            isLoading = false
            return@LaunchedEffect
        }
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("FreaderPubReader", "Opening book: ${book.title} at ${book.filePath}")
                val engine = ReadiumEngine(context)
                publication = engine.openPublication(book)
                android.util.Log.d("FreaderPubReader", "Publication opened successfully")
            } catch (e: Exception) {
                android.util.Log.e("FreaderPubReader", "Failed to open publication", e)
                errorMessage = "Failed to open: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            publication != null && activity != null -> {
                ReadiumFragmentRenderer(
                    publication = publication!!,
                    fragmentActivity = activity,
                    onProgressChanged = onProgressChanged,
                    onToggleControls = onToggleControls,
                    modifier = modifier,
                    initialLocator = initialLocator,
                    setNavigationCallback = setNavigationCallback
                )
            }
        }
    }
}

private fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}
