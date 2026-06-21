package com.wiztek.freader.reader

import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.*
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Concrete strategy for handling Ebooks (EPUB).
 */
class EpubReaderStrategy(
    private val repository: LibraryRepository,
    private val fileSystem: FileSystem
) : ReaderStrategy {

    suspend fun getManifest(book: LibraryBook): ReadiumManifest? {
        val path = book.filePath.toPath()
        if (!fileSystem.exists(path)) return null

        return try {
            val zipFileSystem = openZip(fileSystem, path)
            val containerXml = zipFileSystem.read("/META-INF/container.xml".toPath()) { readUtf8() }
            val rootFilePath = Regex("full-path=\"([^\"]+)\"").find(containerXml)?.groupValues?.get(1) ?: return null
            val rootFileDir = rootFilePath.toPath().parent ?: "/".toPath()
            val opfContent = zipFileSystem.read(rootFilePath.toPath()) { readUtf8() }

            val itemTagRegex = Regex("<item\\s[^>]*/?>")
            val attrRegex = Regex("""(\w[\w-]*)\s*=\s*"([^"]+)"""")
            val manifestItems = itemTagRegex.findAll(opfContent).mapNotNull { tagMatch ->
                val attrs = attrRegex.findAll(tagMatch.value).associate { it.groupValues[1] to it.groupValues[2] }
                val id = attrs["id"] ?: return@mapNotNull null
                val href = attrs["href"] ?: return@mapNotNull null
                val mediaType = attrs["media-type"] ?: return@mapNotNull null
                id to (href to mediaType)
            }.toMap()

            val spineItemRefs = Regex("""<itemref\s[^>]*/?>""").findAll(opfContent).mapNotNull { tagMatch ->
                val attrs = attrRegex.findAll(tagMatch.value).associate { it.groupValues[1] to it.groupValues[2] }
                attrs["idref"]
            }

            val readingOrder = spineItemRefs.mapNotNull { idref ->
                manifestItems[idref]?.let { (href, type) ->
                    val fullPath = (rootFileDir / href).toString().removePrefix("/")
                    ManifestLink(href = fullPath, type = type)
                }
            }.toList()

            val resources = manifestItems.values.map { (href, type) ->
                val fullPath = (rootFileDir / href).toString().removePrefix("/")
                ManifestLink(href = fullPath, type = type)
            }

            // Extract TOC (NCX or Nav)
            val toc = mutableListOf<ManifestLink>()
            val ncxItem = manifestItems.values.find { it.second == "application/x-dtbncx+xml" }
            if (ncxItem != null) {
                val ncxPath = (rootFileDir / ncxItem.first).toString().removePrefix("/")
                try {
                    val ncxContent = zipFileSystem.read(ncxPath.toPath()) { readUtf8() }
                    val navPoints = Regex("<navPoint[^>]+>\\s*<navLabel>\\s*<text>([^<]+)</text>\\s*</navLabel>\\s*<content src=\"([^\"]+)\"").findAll(ncxContent)
                    navPoints.forEach { match ->
                        val title = match.groupValues[1]
                        val href = match.groupValues[2]
                        val fullHref = (rootFileDir / href).toString().removePrefix("/")
                        toc.add(ManifestLink(href = fullHref, title = title))
                    }
                } catch (e: Exception) {
                    // Ignore TOC errors for now
                }
            }

            ReadiumManifest(
                metadata = Metadata(title = book.title),
                readingOrder = readingOrder,
                resources = resources,
                toc = toc
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPages(book: LibraryBook): List<String> {
        return getManifest(book)?.readingOrder?.map { it.href } ?: emptyList()
    }

    override suspend fun exists(filePath: String): Boolean {
        return fileSystem.exists(filePath.toPath())
    }

    override suspend fun saveProgress(bookId: String, progress: Double, locator: String?) {
        repository.updateProgress(bookId, progress, locator)
    }
}

