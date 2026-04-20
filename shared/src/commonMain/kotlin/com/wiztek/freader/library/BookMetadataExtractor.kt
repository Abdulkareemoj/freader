package com.wiztek.freader.library

import com.wiztek.freader.reader.model.BookFormat

data class BookMetadata(
    val title: String,
    val author: String?,
    val coverBytes: ByteArray? = null
)

expect class BookMetadataExtractor {
    suspend fun extract(filePath: String, format: BookFormat): BookMetadata?
}
