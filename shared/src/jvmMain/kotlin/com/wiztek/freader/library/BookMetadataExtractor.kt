package com.wiztek.freader.library

import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.reader.openZip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath

class JvmBookMetadataExtractor : BookMetadataExtractor {
    override suspend fun extract(filePath: String, format: BookFormat): BookMetadata? = withContext(Dispatchers.IO) {
        try {
            when (format) {
                BookFormat.EPUB -> extractEpubMetadata(filePath)
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractEpubMetadata(filePath: String): BookMetadata? {
        val path = filePath.toPath()
        val fs = FileSystem.SYSTEM
        
        return try {
            val zipFs = openZip(fs, path)
            // Simplified metadata extraction
            // Real implementation would parse container.xml and then the OPF file
            var title = path.name.substringBeforeLast(".")
            
            // Try to find a cover image
            var coverBytes: ByteArray? = null
            val coverCandidates = listOf(
                "cover.jpg", "cover.jpeg", "OEBPS/cover.jpg", "OEBPS/images/cover.jpg",
                "OPS/cover.jpg", "OPS/images/cover.jpg", "cover.png"
            )
            
            for (candidate in coverCandidates) {
                val candidatePath = candidate.toPath()
                if (zipFs.exists(candidatePath)) {
                    coverBytes = zipFs.read(candidatePath) { readByteArray() }
                    break
                }
            }

            BookMetadata(
                title = title,
                author = "Unknown Author",
                coverBytes = coverBytes
            )
        } catch (e: Exception) {
            null
        }
    }
}
