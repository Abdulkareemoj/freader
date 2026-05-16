package com.wiztek.freader.library

import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File

class JvmBookMetadataExtractor : BookMetadataExtractor {
    override suspend fun extract(filePath: String, format: BookFormat): BookMetadata? = withContext(Dispatchers.IO) {
        try {
            when (format) {
                BookFormat.CBZ -> extractCbzMetadata(filePath)
                BookFormat.EPUB -> extractEpubMetadata(filePath)
                else -> BookMetadata(File(filePath).nameWithoutExtension, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractCbzMetadata(filePath: String): BookMetadata {
        val file = File(filePath)
        var title = file.nameWithoutExtension
        var coverBytes: ByteArray? = null

        ZipFile(file).use { zipFile ->
            val entries = zipFile.entries.asSequence()
                .filter { !it.isDirectory && isImage(it.name) }
                .sortedBy { it.name }
                .toList()

            // Try to find a cover image (often named cover, folder, or the first image)
            val coverEntry = entries.find { it.name.contains("cover", ignoreCase = true) } ?: entries.firstOrNull()
            
            coverEntry?.let { entry ->
                zipFile.getInputStream(entry).use { it.readBytes() }.also { coverBytes = it }
            }
        }

        return BookMetadata(title, null, coverBytes)
    }

    private fun extractEpubMetadata(filePath: String): BookMetadata {
        // Simple placeholder for EPUB metadata extraction on JVM
        // In a real implementation, you'd parse the OPF file inside the EPUB
        return BookMetadata(File(filePath).nameWithoutExtension, null)
    }

    private fun isImage(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")
    }
}
