package com.wiztek.freader.reader.engine

import android.content.Context
import com.wiztek.freader.library.model.LibraryBook
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.http.HttpClient
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.shared.util.http.HttpResponse
import org.readium.r2.shared.util.http.HttpError
import org.readium.r2.shared.util.http.HttpStreamResponse
import org.readium.r2.shared.util.Url
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import java.io.File

/**
 * Android-specific Readium Engine configured for offline-only use.
 */
class ReadiumEngine(private val context: Context) {

    // Offline-only HttpClient
    private val offlineHttpClient = object : HttpClient {
         suspend fun execute(request: HttpRequest): Try<HttpResponse, HttpError> {
            throw IllegalStateException("Network access is not permitted in offline-only mode.")
        }
        override suspend fun stream(request: HttpRequest): Try<HttpStreamResponse, HttpError> {
            throw IllegalStateException("Network access is not permitted in offline-only mode.")
        }
    }

    private val assetRetriever = AssetRetriever(context.contentResolver, offlineHttpClient)
    
    // DefaultPublicationParser requires pdfFactory, passing null
    private val publicationParser = DefaultPublicationParser(context, offlineHttpClient, assetRetriever, null)
    private val publicationOpener = PublicationOpener(publicationParser)

    suspend fun openPublication(book: LibraryBook): Publication {
        val file = File(book.filePath)
        val url = Url(file.toURI().toURL().toString())
        
        // Explicitly specifying generic types for the Try result to help type inference
        val asset = assetRetriever.retrieve(
            url as AbsoluteUrl,
            format = TODO()
        )
            .getOrElse { throw Exception("Failed to retrieve asset") }
            
        return publicationOpener.open(asset, allowUserInteraction = false)
            .getOrElse { throw Exception("Failed to open publication") }
    }
}
