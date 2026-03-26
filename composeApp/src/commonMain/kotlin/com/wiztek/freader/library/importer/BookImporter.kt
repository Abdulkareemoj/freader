package com.wiztek.freader.library.importer

import com.wiztek.freader.library.model.LibraryBook

class BookImporter {

    suspend fun import(filePath: String): LibraryBook {

        return when {

            filePath.endsWith(".epub", true) ->
                EpubImporter.import(filePath)

            filePath.endsWith(".pdf", true) ->
                PdfImporter.import(filePath)

            else ->
                throw Exception("Unsupported format")

        }

    }

}