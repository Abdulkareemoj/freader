package com.wiztek.freader.reader.renderer.epub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Renderer for EPUB files.
 * This will handle rendering text content and chapter structure.
 */
@Composable
fun EpubRenderer(pages: List<String>, currentPage: Int, onPageChanged: (Int) -> Unit) {
    // TODO: Implement EPUB rendering logic (text/html rendering)
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (currentPage < pages.size) {
            Text(text = pages[currentPage])
        }
    }
}
