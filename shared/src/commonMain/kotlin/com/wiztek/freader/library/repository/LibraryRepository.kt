package com.wiztek.freader.library.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wiztek.freader.database.FreaderDatabase
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepository(database: FreaderDatabase) {
    private val queries = database.freaderDatabaseQueries

    fun getAllBooks(): Flow<List<LibraryBook>> {
        return queries.selectAllBooks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    LibraryBook(
                        id = entity.id,
                        title = entity.title,
                        author = entity.author,
                        format = BookFormat.valueOf(entity.format),
                        filePath = entity.filePath,
                        coverPath = entity.coverPath,
                        seriesName = entity.seriesName,
                        volumeNumber = entity.volumeNumber?.toInt(),
                        progress = entity.progress,
                        addedAt = entity.addedAt
                    )
                }
            }
    }

    suspend fun insertBook(book: LibraryBook) {
        queries.insertBook(
            id = book.id,
            title = book.title,
            author = book.author,
            format = book.format.name,
            filePath = book.filePath,
            coverPath = book.coverPath,
            seriesName = book.seriesName,
            volumeNumber = book.volumeNumber?.toLong(),
            addedAt = book.addedAt,
            lastReadAt = null,
            progress = book.progress
        )
    }

    suspend fun deleteBook(id: String) {
        queries.deleteBook(id)
    }

    suspend fun updateProgress(id: String, progress: Double) {
        queries.updateProgress(
            progress = progress,
            lastReadAt = kotlin.time.Clock.System.now().toEpochMilliseconds(),
            id = id
        )
    }

    // Collections
    fun getAllCollections(): Flow<List<LibraryCollection>> {
        return queries.selectAllCollections()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    LibraryCollection(
                        id = entity.id,
                        name = entity.name,
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    suspend fun insertCollection(collection: LibraryCollection) {
        queries.insertCollection(collection.id, collection.name, collection.createdAt)
    }

    suspend fun deleteCollection(id: String) {
        queries.deleteCollection(id)
    }

    suspend fun addBookToCollection(collectionId: String, bookId: String) {
        queries.addBookToCollection(collectionId, bookId)
    }

    suspend fun removeBookFromCollection(collectionId: String, bookId: String) {
        queries.removeBookFromCollection(collectionId, bookId)
    }
}
