package com.wiztek.freader.reader

import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.BookFormat
import okio.FileSystem

/**
 * Factory to provide the correct reader strategy based on book format.
 */
class ReaderStrategyFactory(
    private val repository: LibraryRepository,
    private val fileSystem: FileSystem
) {
    fun create(format: BookFormat): ReaderStrategy {
        return when (format) {
            BookFormat.EPUB -> EpubReaderStrategy(repository, fileSystem)
            BookFormat.CBZ, BookFormat.CBR -> ComicReaderStrategy(repository, fileSystem)
            BookFormat.PDF -> PdfReaderStrategy(repository, fileSystem)
            BookFormat.MOBI -> MobiReaderStrategy(repository, fileSystem)
        }
    }
}

