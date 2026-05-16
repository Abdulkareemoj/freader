package com.wiztek.freader.reader

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

class ZipFetcher(
    private val path: String,
    private val entryName: String,
    private val fileSystem: FileSystem
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        return try {
            val zipFileSystem = openZip(fileSystem, path.toPath())
            // Ensure entryPath is treated correctly regardless of whether it starts with /
            val normalizedEntry = if (entryName.startsWith("/")) entryName else "/$entryName"
            val entryPath = normalizedEntry.toPath()
            
            SourceFetchResult(
                source = ImageSource(
                    source = zipFileSystem.source(entryPath).buffer(),
                    fileSystem = zipFileSystem
                ),
                mimeType = null,
                dataSource = DataSource.DISK
            )
        } catch (e: Exception) {
            null
        }
    }

    class Factory(private val fileSystem: FileSystem) : Fetcher.Factory<ZipImageModel> {
        override fun create(data: ZipImageModel, options: Options, imageLoader: ImageLoader): Fetcher? {
            return ZipFetcher(data.zipPath, data.entryName, fileSystem)
        }
    }
}

data class ZipImageModel(
    val zipPath: String,
    val entryName: String
)
