package com.wiztek.freader.library

import android.content.Context
import android.graphics.Bitmap
import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.toUrl
import org.readium.r2.shared.util.http.HttpClient
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.shared.util.http.HttpError
import org.readium.r2.shared.util.http.HttpStreamResponse
import org.readium.r2.shared.util.Try
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import java.io.ByteArrayOutputStream
import java.io.File

class AndroidBookMetadataExtractor(private val context: Context) : BookMetadataExtractor {
    
    private val httpClient = object : HttpClient {
        override suspend fun stream(request: HttpRequest): Try<HttpStreamResponse, HttpError> =
            Try.failure(HttpError.MalformedResponse(null))
    }

    private val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
    private val pdfFactory = PdfiumDocumentFactory(context)
    private val publicationParser = DefaultPublicationParser(context, httpClient, assetRetriever, pdfFactory)
    private val publicationOpener = PublicationOpener(publicationParser)

    override suspend fun extract(filePath: String, format: BookFormat): BookMetadata? = withContext(Dispatchers.IO) {
        println("Freader: Extracting metadata for $filePath")
        var publication: org.readium.r2.shared.publication.Publication? = null
        try {
            val file = File(filePath)
            if (!file.exists()) {
                println("Freader: File does not exist at $filePath")
                return@withContext null
            }
            val url = file.toUrl()
            
            println("Freader: Retrieving asset...")
            val asset = assetRetriever.retrieve(url).getOrElse { 
                println("Freader: Failed to retrieve asset: $it")
                return@withContext null 
            }
            
            println("Freader: Opening publication...")
            publication = publicationOpener.open(asset, allowUserInteraction = false).getOrElse { 
                println("Freader: Failed to open publication: $it")
                return@withContext null 
            }
            
            val pub = publication!!
            val title = pub.metadata.title ?: file.name
            val author = pub.metadata.authors.firstOrNull()?.name
            println("Freader: Metadata found - Title: $title, Author: $author")
            
            // Get the cover as a Bitmap and convert to ByteArray
            println("Freader: Extracting cover...")
            val bitmap = pub.cover()
            val coverBytes = bitmap?.let {
                ByteArrayOutputStream().use { stream ->
                    it.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    stream.toByteArray()
                }
            }
            println("Freader: Cover extraction ${if (coverBytes != null) "successful" else "failed or not found"}")
            
            BookMetadata(
                title = title,
                author = author,
                coverBytes = coverBytes
            )
        } catch (e: Exception) {
            println("Freader: Error during metadata extraction: ${e.message}")
            e.printStackTrace()
            null
        } finally {
            publication?.close()
            println("Freader: Finished extraction attempt for $filePath")
        }
    }
}
