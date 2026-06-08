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
import org.readium.r2.shared.util.format.FormatHints
import org.readium.r2.shared.util.toUrl
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import java.io.File

/**
 * Android-specific Readium Engine configured for offline-only use.
 */
class ReadiumEngine(private val context: Context) {

    // Offline-only HttpClient
    private val offlineHttpClient = object : HttpClient {
        override suspend fun stream(request: HttpRequest): Try<HttpStreamResponse, HttpError> {
            throw IllegalStateException("Network access is not permitted in offline-only mode.")
        }
    }

    private val assetRetriever = AssetRetriever(context.contentResolver, offlineHttpClient)
    
    // Use PdfiumDocumentFactory to support PDF files
    private val pdfFactory = PdfiumDocumentFactory(context)
    private val publicationParser = DefaultPublicationParser(context, offlineHttpClient, assetRetriever, pdfFactory)
    private val publicationOpener = PublicationOpener(publicationParser)

    suspend fun openPublication(book: LibraryBook): Publication {
        val file = File(book.filePath)
        val url = file.toUrl()
        
        val asset = assetRetriever.retrieve(
            url,
            formatHints = FormatHints(fileExtensions = listOf(file.extension))
        )
            .getOrElse { throw Exception("Failed to retrieve asset: $it") }
            
        return publicationOpener.open(asset, allowUserInteraction = false)
            .getOrElse { throw Exception("Failed to open publication: $it") }
    }
}
