package com.wiztek.freader.library

import android.content.Context
import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.http.HttpClient
import org.readium.r2.shared.util.http.HttpRequest
import org.readium.r2.shared.util.http.HttpResponse
import org.readium.r2.shared.util.http.HttpError
import org.readium.r2.shared.util.http.HttpStreamResponse
import org.readium.r2.shared.util.Try
import java.io.File

class AndroidBookMetadataExtractor(private val context: Context) : BookMetadataExtractor {
    
    private val httpClient = object : HttpClient {
        suspend fun execute(request: HttpRequest): Try<HttpResponse, HttpError> =
            Try.failure(HttpError.MalformedResponse(null))
        override suspend fun stream(request: HttpRequest): Try<HttpStreamResponse, HttpError> =
            Try.failure(HttpError.MalformedResponse(null))
    }

    private val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
    private val publicationParser = DefaultPublicationParser(context, httpClient, assetRetriever)
    private val publicationOpener = PublicationOpener(publicationParser)

    override suspend fun extract(filePath: String, format: BookFormat): BookMetadata? = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            val url = Url(file.toURI().toURL().toString()) as? AbsoluteUrl ?: return@withContext null
            
            val asset = assetRetriever.retrieve(url).getOrElse { return@withContext null }
            val publication = publicationOpener.open(asset, allowUserInteraction = false).getOrElse { return@withContext null }
            
            val title = publication.metadata.title ?: file.name
            val author = publication.metadata.authors.firstOrNull()?.name
            
            val coverResource = publication.cover()
            val coverBytes = coverResource?.read()?.getOrNull()
            
            BookMetadata(
                title = title,
                author = author,
                coverBytes = coverBytes
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
