package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Concrete strategy for handling Comic books (CBZ/CBR).
 */
class ComicReaderStrategy(
    private val repository: LibraryRepository,
    private val fileSystem: FileSystem
) : ReaderStrategy {

    override suspend fun getPages(book: LibraryBook): List<String> {
        val path = book.filePath.toPath()
        if (!fileSystem.exists(path)) return emptyList()

        return try {
            val pages = mutableListOf<String>()
            val zipFileSystem = openZip(fileSystem, path)
            
            // Recursively find all image files in the zip
            fun collectImages(dir: okio.Path) {
                for (entryPath in zipFileSystem.list(dir)) {
                    val metadata = zipFileSystem.metadata(entryPath)
                    if (metadata.isDirectory) {
                        collectImages(entryPath)
                    } else if (isImageFile(entryPath.name)) {
                        // Use the full path relative to the zip root for loading
                        pages.add(entryPath.toString())
                    }
                }
            }
            
            collectImages("/".toPath())
            
            pages.sortedWith(NaturalOrderComparator())
        } catch (e: Exception) {
            println("Error reading comic: ${e.message}")
            emptyList()
        }
    }

    /**
     * Natural order comparator for strings (e.g., "page 2" comes before "page 10")
     */
    private class NaturalOrderComparator : Comparator<String> {
        override fun compare(a: String, b: String): Int {
            val numbers1 = extractNumbers(a)
            val numbers2 = extractNumbers(b)
            
            val minSize = minOf(numbers1.size, numbers2.size)
            for (i in 0 until minSize) {
                if (numbers1[i] != numbers2[i]) {
                    return numbers1[i].compareTo(numbers2[i])
                }
            }
            return a.compareTo(b, ignoreCase = true)
        }

        private fun extractNumbers(s: String): List<Double> {
            val regex = Regex("(\\d+\\.?\\d*)")
            return regex.findAll(s).map { it.value.toDoubleOrNull() ?: 0.0 }.toList()
        }
    }

    override suspend fun exists(filePath: String): Boolean {
        return fileSystem.exists(filePath.toPath())
    }

    private fun isImageFile(name: String): Boolean {
        val lowerName = name.lowercase()
        return lowerName.endsWith(".jpg") || 
               lowerName.endsWith(".jpeg") || 
               lowerName.endsWith(".png") || 
               lowerName.endsWith(".webp")
    }

    override suspend fun saveProgress(bookId: String, progress: Double, locator: String?) {
        repository.updateProgress(bookId, progress, locator)
    }
}
