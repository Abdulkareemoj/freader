package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook

/**
 * Strategy interface for handling different book formats.
 * Implementation will differ for Ebooks (EPUB/PDF) vs Comics (CBZ/CBR).
 */
interface ReaderStrategy {
    /**
     * Extracts pages or content for the reader screen.
     * Could return a list of image paths for comics or text/structure for epubs.
     */
    suspend fun getPages(book: LibraryBook): List<String>
    
    /**
     * Logic to handle bookmarking or saving current position for this format.
     */
    suspend fun saveProgress(bookId: String, pageIndex: Int)
}
