package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import java.io.File

/**
 * Concrete strategy for handling PDF books.
 */
class PdfReaderStrategy(
    private val repository: LibraryRepository
) : ReaderStrategy {

    override suspend fun getPages(book: LibraryBook): List<String> {
        // TODO: Implement PDF parsing.
        // PDF parsing is typically done using libraries like PDFBox (JVM) or 
        // platform-specific renderers (Android PdfRenderer, iOS PDFKit).
        // Since this is KMP, you might need a cross-platform wrapper or 
        // specialized libraries for each platform.
        val file = File(book.filePath)
        if (!file.exists()) return emptyList()

        return listOf("Page 1", "Page 2", "Page 3")
    }

    override suspend fun saveProgress(bookId: String, pageIndex: Int) {
        repository.updateProgress(bookId, pageIndex.toDouble())
    }
}
