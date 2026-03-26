package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import java.io.File

/**
 * Concrete strategy for handling MOBI (Kindle) files.
 */
class MobiReaderStrategy(
    private val repository: LibraryRepository
) : ReaderStrategy {

    override suspend fun getPages(book: LibraryBook): List<String> {
        // TODO: Implement MOBI parsing.
        // MOBI parsing often requires dedicated libraries like Calibre's 
        // internal parsing logic or specialized MOBI readers.
        val file = File(book.filePath)
        if (!file.exists()) return emptyList()

        return listOf("Chapter 1", "Chapter 2")
    }

    override suspend fun saveProgress(bookId: String, pageIndex: Int) {
        repository.updateProgress(bookId, pageIndex.toDouble())
    }
}
