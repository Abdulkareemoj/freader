package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Concrete strategy for handling MOBI (Kindle) files.
 */
class MobiReaderStrategy(
    private val repository: LibraryRepository,
    private val fileSystem: FileSystem
) : ReaderStrategy {

    override suspend fun getPages(book: LibraryBook): List<String> {
        // TODO: Implement MOBI parsing.
        // MOBI parsing often requires dedicated libraries like Calibre's 
        // internal parsing logic or specialized MOBI readers.
        val path = book.filePath.toPath()
        if (!fileSystem.exists(path)) return emptyList()

        return listOf("Chapter 1", "Chapter 2")
    }

    override suspend fun exists(filePath: String): Boolean {
        return fileSystem.exists(filePath.toPath())
    }

    override suspend fun saveProgress(bookId: String, progress: Double, locator: String?) {
        repository.updateProgress(bookId, progress, locator)
    }
}

