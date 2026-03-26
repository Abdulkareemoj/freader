package com.wiztek.freader.reader

import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.BookFormat

/**
 * Factory to provide the correct reader strategy based on book format.
 */
class ReaderStrategyFactory(
    private val repository: LibraryRepository
) {
    fun create(format: BookFormat): ReaderStrategy {
        return when (format) {
            BookFormat.EPUB -> EpubReaderStrategy(repository)
            BookFormat.CBZ, BookFormat.CBR -> ComicReaderStrategy(repository)
            BookFormat.PDF -> PdfReaderStrategy(repository)
            BookFormat.MOBI -> MobiReaderStrategy(repository)
        }
    }
}
