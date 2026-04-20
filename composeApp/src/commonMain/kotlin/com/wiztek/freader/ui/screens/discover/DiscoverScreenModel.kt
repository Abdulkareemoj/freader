package com.wiztek.freader.ui.screens.discover

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.BookMetadataExtractor
import com.wiztek.freader.library.LibraryImporter
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.BookFormat
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoverScreenModel(
    private val importer: LibraryImporter,
    private val repository: LibraryRepository,
    private val metadataExtractor: BookMetadataExtractor
) : ScreenModel {

    private val _isImporting = MutableStateFlow(false)
    val isImporting = _isImporting.asStateFlow()

    fun importFile(file: PlatformFile) {
        screenModelScope.launch {
            _isImporting.value = true
            try {
                val path = file.path ?: return@launch
                val format = extractFormat(file.name)
                
                // 1. Copy file to internal storage
                val internalPath = importer.importBook(path).getOrThrow()
                
                // 2. Extract metadata
                val metadata = metadataExtractor.extract(internalPath, format)
                
                // 3. Add to database
                val book = LibraryBook(
                    id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                    title = metadata?.title ?: file.name,
                    author = metadata?.author ?: "Unknown",
                    format = format,
                    filePath = internalPath,
                    coverPath = null, // TODO: Handle cover image storage
                    progress = 0.0,
                    addedAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
                )
                repository.insertBook(book)
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isImporting.value = false
            }
        }
    }

    private fun extractFormat(fileName: String): BookFormat {
            return when {
                fileName.endsWith(".pdf", true) -> BookFormat.PDF
                fileName.endsWith(".epub", true) -> BookFormat.EPUB
                fileName.endsWith(".cbz", true) -> BookFormat.CBZ
                fileName.endsWith(".cbr", true) -> BookFormat.CBR
                fileName.endsWith(".mobi", true) -> BookFormat.MOBI
                else -> BookFormat.EPUB
            }
        }
    }
