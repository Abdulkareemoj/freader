package com.wiztek.freader.library.importer

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat
import java.util.UUID

object EpubImporter {

    suspend fun import(filePath: String): LibraryBook {

        val title = filePath
            .substringAfterLast("/")
            .substringBeforeLast(".")

        return LibraryBook(
            id = UUID.randomUUID().toString(),
            title = title,
            author = null,
            format = BookFormat.EPUB,
            filePath = filePath,
            coverPath = null
        )

    }

}