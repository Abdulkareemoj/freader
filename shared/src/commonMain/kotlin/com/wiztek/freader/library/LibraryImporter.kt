package com.wiztek.freader.library

import okio.Path
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

interface LibraryImporter {
    fun importBook(sourcePath: String): Result<String>
}

class LibraryImporterImpl(
    private val fileSystem: FileSystem,
    private val appStorageDir: Path
) : LibraryImporter {

    override fun importBook(sourcePath: String): Result<String> {
        return runCatching {
            val source = sourcePath.toPath()
            if (!fileSystem.exists(source)) throw Exception("Source file does not exist")

            // Ensure destination directory exists
            val booksDir = appStorageDir.div("books")
            if (!fileSystem.exists(booksDir)) {
                fileSystem.createDirectories(booksDir)
            }

            val destination = booksDir.div(source.name)
            
            // Copy the file using streams for cross-platform reliability
            fileSystem.source(source).buffer().use { sourceBuffer ->
                fileSystem.sink(destination).buffer().use { sinkBuffer ->
                    sinkBuffer.writeAll(sourceBuffer)
                }
            }
            
            destination.toString()
        }
    }
}
