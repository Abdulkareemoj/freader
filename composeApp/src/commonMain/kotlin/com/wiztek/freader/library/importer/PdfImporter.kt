package com.wiztek.freader.library.importer

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat
import java.util.UUID

object PdfImporter {

    suspend fun import(filePath: String): LibraryBook {

        val title = filePath
            .substringAfterLast("/")
            .substringBeforeLast(".")

        return LibraryBook(
            id = UUID.randomUUID().toString(),
            title = title,
            author = null,
            format = BookFormat.PDF,
            filePath = filePath,
            coverPath = null
        )

    }

}