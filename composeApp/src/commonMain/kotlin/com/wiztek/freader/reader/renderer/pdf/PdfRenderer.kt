package com.wiztek.freader.reader.renderer.pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Renderer for PDF files.
 * This will use platform-specific PDF rendering when fully implemented.
 */
@Composable
fun PdfRenderer(pages: List<String>, currentPage: Int, onPageChanged: (Int) -> Unit) {
    // TODO: Implement PDF rendering logic
    // On Android: Use Android PdfRenderer
    // On Desktop/iOS: Use a cross-platform PDF library or platform-specific wrapper
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("PDF Renderer: Showing page ${currentPage + 1} of ${pages.size}")
    }
}
