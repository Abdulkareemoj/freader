package com.wiztek.freader.ui.components.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook

@Composable
actual fun PublicationReader(
    book: LibraryBook,
    modifier: Modifier,
    onProgressChanged: (Double) -> Unit,
    onToggleControls: () -> Unit
) {
    // Placeholder for the WebView implementation (Readium TS-toolkit)
    Box(
        modifier = modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.White)
            Spacer(Modifier.height(16.dp))
            Text(
                "Loading Desktop Reader (WebView + TS-Toolkit)...",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Reading: ${book.title}",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
