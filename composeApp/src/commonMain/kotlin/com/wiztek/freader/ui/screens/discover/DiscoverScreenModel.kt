package com.wiztek.freader.ui.screens.discover

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.BookMetadataExtractor
import com.wiztek.freader.library.LibraryImporter
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.BookFormat
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ImportProgress(
    val currentFile: String = "",
    val progress: Float = 0f,
    val totalFiles: Int = 0,
    val processedFiles: Int = 0,
    val isImporting: Boolean = false,
    val successCount: Int = 0,
    val failCount: Int = 0,
    val showResult: Boolean = false
)

class DiscoverScreenModel(
    private val importer: LibraryImporter,
    private val repository: LibraryRepository,
    private val metadataExtractor: BookMetadataExtractor
) : ScreenModel {

    private val _importState = MutableStateFlow(ImportProgress())
    val importState = _importState.asStateFlow()

    fun importFiles(files: List<PlatformFile>) {
        screenModelScope.launch {
            println("Freader: Starting import of ${files.size} files")
            _importState.update { it.copy(
                isImporting = true, totalFiles = files.size, processedFiles = 0,
                successCount = 0, failCount = 0, showResult = false
            ) }
            
            files.forEachIndexed { index, file ->
                val fileName = file.name
                println("Freader: Processing file $index: $fileName")
                _importState.update { it.copy(currentFile = fileName, progress = index.toFloat() / files.size) }
                
                var success = false
                try {
                    val format = extractFormat(fileName)
                    println("Freader: Format identified as $format")
                    
                    println("Freader: Reading bytes for $fileName...")
                    val bytes = file.readBytes()
                    println("Freader: Read ${bytes.size} bytes")
                    
                    withContext(Dispatchers.IO) {
                        println("Freader: Importing to internal storage...")
                        val result = importer.importBook(fileName, bytes)
                        val internalPath = result.getOrThrow()
                        println("Freader: Imported to $internalPath")
                        
                        println("Freader: Extracting metadata...")
                        val metadata = metadataExtractor.extract(internalPath, format)
                        println("Freader: Metadata extraction finished")
                        
                        val now = Clock.System.now()
                        val bookId = now.toEpochMilliseconds().toString() + "_$index"

                        val coverPath = metadata?.coverBytes?.let { coverBytes ->
                            println("Freader: Saving cover...")
                            importer.saveCover(bookId, coverBytes).getOrNull()
                        }
                        
                        println("Freader: Saving to database...")
                        val book = LibraryBook(
                            id = bookId,
                            title = metadata?.title ?: fileName,
                            author = metadata?.author ?: "Unknown",
                            format = format,
                            filePath = internalPath,
                            coverPath = coverPath,
                            progress = 0.0,
                            lastReadLocator = null,
                            addedAt = now.toEpochMilliseconds()
                        )
                        repository.insertBook(book)
                        println("Freader: Successfully imported $fileName")
                    }
                    success = true
                } catch (e: Exception) {
                    println("Freader: Failed to import $fileName: ${e.message}")
                    e.printStackTrace()
                } finally {
                    _importState.update {
                        it.copy(
                            processedFiles = index + 1,
                            successCount = it.successCount + if (success) 1 else 0,
                            failCount = it.failCount + if (success) 0 else 1
                        )
                    }
                    println("Freader: Finished loop for $fileName, processedFiles: ${index + 1}")
                }
            }
            
            _importState.update { it.copy(isImporting = false, progress = 1f, showResult = true) }
            println("Freader: All imports finished")
        }
    }

    fun dismissResult() {
        _importState.update { it.copy(showResult = false) }
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
