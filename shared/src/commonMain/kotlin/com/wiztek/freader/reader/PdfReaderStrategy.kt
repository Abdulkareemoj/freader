package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Concrete strategy for handling PDF books.
 */
class PdfReaderStrategy(
    private val repository: LibraryRepository,
    private val fileSystem: FileSystem
) : ReaderStrategy {

    override suspend fun getPages(book: LibraryBook): List<String> {
        val path = book.filePath.toPath()
        if (!fileSystem.exists(path)) return emptyList()

        // For PDF, we treat the file itself as the single 'page' or source.
        // The reader (WebView or native) will handle internal page navigation.
        return listOf(book.filePath)
    }

    override suspend fun exists(filePath: String): Boolean {
        return fileSystem.exists(filePath.toPath())
    }

    override suspend fun saveProgress(bookId: String, progress: Double, locator: String?) {
        repository.updateProgress(bookId, progress, locator)
    }
}

