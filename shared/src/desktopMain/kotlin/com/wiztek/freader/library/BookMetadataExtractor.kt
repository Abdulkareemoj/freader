package com.wiztek.freader.library

import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.reader.openZip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
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

            val coverEntry = entries.find { it.name.contains("cover", ignoreCase = true) } ?: entries.firstOrNull()

            coverEntry?.let { entry ->
                zipFile.getInputStream(entry).use { it.readBytes() }.also { coverBytes = it }
            }
        }

        return BookMetadata(title, null, coverBytes)
    }

    private fun extractEpubMetadata(filePath: String): BookMetadata? {
        val path = filePath.toPath()
        val fs = FileSystem.SYSTEM

        return try {
            val zipFs = openZip(fs, path)
            var title = path.name.substringBeforeLast(".")

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

    private fun isImage(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")
    }
}
