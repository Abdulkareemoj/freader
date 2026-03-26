package com.wiztek.freader.library.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wiztek.freader.database.FreaderDatabase
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

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
                        volumeNumber = entity.volumeNumber?.toInt(), // Map Long to Int
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
            volumeNumber = book.volumeNumber?.toLong(), // Map Int to Long
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
            lastReadAt = Clock.System.now().toEpochMilliseconds(),
            id = id
        )
    }
}
