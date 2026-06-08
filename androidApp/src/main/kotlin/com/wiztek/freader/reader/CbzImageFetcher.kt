package com.wiztek.freader.reader

import android.content.Context
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.Buffer
import okio.FileSystem
import okio.buffer
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.File
import java.io.FileInputStream

/**
 * A custom Coil Fetcher to load images from within CBZ files.
 * Format: "cbz://[file_path]?[entry_name]"
 */

class CbzImageFetcher(
    private val data: coil3.Uri,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val fullPath = data.toString().removePrefix("cbz://")
        val parts = fullPath.split("?", limit = 2)
        if (parts.size < 2) return null
        
        val filePath = parts[0]
        val entryName = parts[1]
        
        val file = File(filePath)
        if (!file.exists()) return null

        ZipArchiveInputStream(FileInputStream(file)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == entryName) {
                    val bytes = zip.readBytes()
                    val buffer = Buffer().write(bytes)
                    return SourceFetchResult(
                        source = ImageSource(buffer, FileSystem.SYSTEM),
                        mimeType = "image/jpeg",
                        dataSource = DataSource.DISK
                    )
                }
                entry = zip.nextEntry
            }
        }
        return null
    }

    class Factory : Fetcher.Factory<coil3.Uri> {
        override fun create(data: coil3.Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
            return if (data.scheme == "cbz") CbzImageFetcher(data, options) else null
        }
    }
}
