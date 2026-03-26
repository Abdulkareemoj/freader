package com.wiztek.freader.reader.renderer

import androidx.compose.runtime.Composable
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.reader.renderer.comic.ComicRenderer
import com.wiztek.freader.reader.renderer.epub.EpubRenderer
import com.wiztek.freader.reader.renderer.pdf.PdfRenderer

/**
 * Main entry point for rendering different book formats.
 * Decides which specialized renderer to use based on BookFormat.
 */
@Composable
fun ReaderRenderer(
    book: LibraryBook,
    pages: List<String>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit
) {
    when (book.format) {
        BookFormat.EPUB -> EpubRenderer(pages, currentPage, onPageChanged)
        BookFormat.PDF -> PdfRenderer(pages, currentPage, onPageChanged)
        BookFormat.CBZ, BookFormat.CBR -> ComicRenderer(pages, currentPage, onPageChanged)
        BookFormat.MOBI -> {
            // Placeholder: Call MobiRenderer here
        }
    }
}
