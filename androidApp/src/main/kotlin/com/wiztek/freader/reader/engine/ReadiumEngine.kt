package com.wiztek.freader.reader.engine

import android.content.Context
import com.wiztek.freader.library.model.LibraryBook
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.http.HttpClient
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.shared.util.http.HttpResponse
import org.readium.r2.shared.util.http.HttpError
import org.readium.r2.shared.util.http.HttpStreamResponse
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.shared.util.asset.Asset
import org.readium.r2.shared.util.asset.AssetRetriever
import java.io.File

class ReadiumEngine(private val context: Context) {

    private val offlineHttpClient = object : HttpClient {
        suspend fun execute(request: HttpRequest): Try<HttpResponse, HttpError> {
            throw IllegalStateException("Network access is not permitted in offline-only mode.")
        }
        override suspend fun stream(request: HttpRequest): Try<HttpStreamResponse, HttpError> {
            throw IllegalStateException("Network access is not permitted in offline-only mode.")
        }
    }

    private val assetRetriever = AssetRetriever(context.contentResolver, offlineHttpClient)
    private val publicationParser = DefaultPublicationParser(context, offlineHttpClient, assetRetriever, null)
    private val publicationOpener = PublicationOpener(publicationParser)

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> Try<T, *>.unwrap(message: String): T = when (this) {
        is Try.Success<*, *> -> (this as Try.Success<T, *>).value
        is Try.Failure<*, *> -> throw Exception(message)
    }

    suspend fun openPublication(book: LibraryBook): Publication {
        val file = File(book.filePath)
        val url = Url(file.toURI().toURL().toString()) as AbsoluteUrl
        val asset = assetRetriever.retrieve(url).unwrap("Failed to retrieve asset")
        return publicationOpener.open(asset, allowUserInteraction = false).unwrap("Failed to open publication")
    }
}
