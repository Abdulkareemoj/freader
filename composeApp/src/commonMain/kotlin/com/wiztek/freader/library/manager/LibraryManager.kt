package com.wiztek.freader.library.manager

import com.wiztek.freader.library.importer.BookImporter
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LibraryManager(
    private val repository: LibraryRepository,
    private val importer: BookImporter
) {

    suspend fun importBook(filePath: String): LibraryBook {
        val book = importer.import(filePath)
        repository.insertBook(book)
        return book
    }

    // In a reactive architecture, you typically expose the Flow directly
    // rather than using a blocking/immediate list return.
    fun getLibrary(): Flow<List<LibraryBook>> {
        return repository.getAllBooks()
    }
}
