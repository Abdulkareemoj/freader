package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.File
import java.io.FileInputStream

/**
 * Concrete strategy for handling Comic books (CBZ/CBR).
 */
class ComicReaderStrategy(
    private val repository: LibraryRepository
) : ReaderStrategy {

    override suspend fun getPages(book: LibraryBook): List<String> {
        val file = File(book.filePath)
        if (!file.exists()) return emptyList()

        val pages = mutableListOf<String>()

        // For simplicity, we store the full path to the image entry within the zip.
        // In a real-world app, you might extract these to a temporary cache directory.
        ZipArchiveInputStream(FileInputStream(file)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && isImageFile(entry.name)) {
                    // We store a custom URI format: "cbz://[file_path]?[entry_name]"
                    // Your custom ImageLoader/Coil can then intercept this.
                    pages.add("cbz://${book.filePath}?${entry.name}")
                }
                entry = zip.nextEntry
            }
        }

        return pages.sorted()
    }

    private fun isImageFile(name: String): Boolean {
        val lowerName = name.lowercase()
        return lowerName.endsWith(".jpg") || 
               lowerName.endsWith(".jpeg") || 
               lowerName.endsWith(".png") || 
               lowerName.endsWith(".webp")
    }

    override suspend fun saveProgress(bookId: String, pageIndex: Int) {
        repository.updateProgress(bookId, pageIndex.toDouble())
    }
}
