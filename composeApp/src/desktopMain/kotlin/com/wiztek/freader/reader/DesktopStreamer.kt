package com.wiztek.freader.reader

import freader.composeapp.generated.resources.Res
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.net.ServerSocket
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class DesktopStreamer(private val fileSystem: FileSystem) {
    private var server: NettyApplicationEngine? = null
    var port: Int = 0
        private set

    @OptIn(ExperimentalResourceApi::class)
    fun start() {
        if (server != null) return

        port = findFreePort()
        println("Streamer: Starting on port $port")
        
        server = embeddedServer(Netty, port = port) {
            install(CORS) {
                anyHost()
                allowHeader(HttpHeaders.ContentType)
            }
            routing {
                get("/ping") {
                    call.respondText("pong")
                }

                get("/test") {
                    call.respondText("Freader Streamer OK on port $port", ContentType.Text.Html)
                }

                get("/assets/{assetPath...}") {
                    val assetPath = call.parameters.getAll("assetPath")?.joinToString("/") ?: ""
                    try {
                        val resourcePath = "files/readium-shell/$assetPath"
                        val bytes = Res.readBytes(resourcePath)
                        val contentType = ContentType.fromFilePath(assetPath).firstOrNull() ?: ContentType.Application.OctetStream
                        call.respondBytes(bytes, contentType)
                    } catch (e: Exception) {
                        println("Streamer: Failed to load asset $assetPath: ${e.message}")
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                get("/{bookPath...}") {
                    val fullPath = call.parameters.getAll("bookPath")?.joinToString("/") ?: ""
                    println("Streamer: Book request raw=$fullPath")
                    try {
                        handleRequest(call, fullPath)
                    } catch (e: Exception) {
                        println("Streamer: Error handling request: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }.start(wait = false)
    }

    private suspend fun handleRequest(call: ApplicationCall, encodedPath: String) {
        try {
            // Decode the path from the URL
            val fullPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())
            
            // Clean up the path: remove trailing slash
            val cleanedPathString = fullPath.removeSuffix("/")
            
            // On Windows, the path might start with "C:/". Okio's toPath() handles this well
            // if it's a valid absolute path.
            val path = cleanedPathString.toPath()

            // 1. Check if it's a direct file (like PDF)
            if (fileSystem.exists(path) && !fileSystem.metadata(path).isDirectory) {
                val bytes = withContext(Dispatchers.IO) {
                    fileSystem.read(path) { readByteArray() }
                }
                val contentType = ContentType.fromFilePath(cleanedPathString).firstOrNull() ?: ContentType.Application.OctetStream
                call.respondBytes(bytes, contentType)
                return
            }

            // 2. It might be a zip/epub entry
            val zipExtensions = listOf(".epub", ".cbz", ".cbr", ".zip")
            var zipPathString: String? = null
            var entryPathString: String? = null

            for (ext in zipExtensions) {
                val extWithSlash = "$ext/"
                if (fullPath.contains(extWithSlash)) {
                    val index = fullPath.indexOf(extWithSlash) + ext.length
                    zipPathString = fullPath.substring(0, index).removeSuffix("/")
                    entryPathString = fullPath.substring(index).removePrefix("/")
                    break
                } else if (fullPath.endsWith(ext)) {
                    zipPathString = fullPath
                    entryPathString = ""
                    break
                }
            }

            if (zipPathString == null) {
                println("Streamer: Could not find zip extension in path: $fullPath")
                call.respond(HttpStatusCode.NotFound)
                return
            }

            val zipPath = zipPathString.toPath()
            if (!fileSystem.exists(zipPath)) {
                println("Streamer: Zip file does not exist: $zipPath")
                call.respond(HttpStatusCode.NotFound)
                return
            }

            val zipFs = openZip(fileSystem, zipPath)
            val entryPath = if (entryPathString.isNullOrEmpty()) "/".toPath() else if (entryPathString.startsWith("/")) entryPathString.toPath() else "/$entryPathString".toPath()

            if (!zipFs.exists(entryPath)) {
                println("Streamer: Entry $entryPath not found in $zipPath")
                call.respond(HttpStatusCode.NotFound)
                return
            }

            val bytes = withContext(Dispatchers.IO) {
                zipFs.read(entryPath) { readByteArray() }
            }
            val contentType = ContentType.fromFilePath(entryPathString ?: "").firstOrNull() ?: ContentType.Application.OctetStream
            
            call.respondBytes(bytes, contentType)
        } catch (e: Exception) {
            println("Streamer: Error handling request for $encodedPath: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
        }
    }

    fun stop() {
        server?.stop(1000, 2000)
        server = null
    }

    private fun findFreePort(): Int {
        ServerSocket(0).use { return it.localPort }
    }
}
