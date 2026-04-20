package com.wiztek.freader.library

import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class BookMetadataExtractor {
    actual suspend fun extract(filePath: String, format: BookFormat): BookMetadata? = withContext(Dispatchers.IO) {
        null
    }
}
