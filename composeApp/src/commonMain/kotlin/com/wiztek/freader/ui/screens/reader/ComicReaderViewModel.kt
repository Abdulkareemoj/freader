package com.wiztek.freader.ui.screens.reader

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.ReaderStrategyFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComicReaderState(
    val book: LibraryBook,
    val pages: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val initialPage: Int = 0,
    val error: String? = null
)

class ComicReaderViewModel(
    private val book: LibraryBook,
    private val readerStrategyFactory: ReaderStrategyFactory,
    private val libraryRepository: com.wiztek.freader.library.repository.LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(ComicReaderState(book))
    val state: StateFlow<ComicReaderState> = _state.asStateFlow()

    private val _seriesBooks = MutableStateFlow<List<LibraryBook>>(emptyList())
    val seriesBooks: StateFlow<List<LibraryBook>> = _seriesBooks.asStateFlow()

    init {
        loadPages()
        loadSeriesInfo()
    }

    private fun loadSeriesInfo() {
        book.seriesName?.let { seriesName ->
            screenModelScope.launch {
                libraryRepository.getAllBooks().collect { allBooks ->
                    _seriesBooks.value = allBooks
                        .filter { it.seriesName == seriesName }
                        .sortedBy { it.volumeNumber ?: 0 }
                }
            }
        }
    }

    fun getNextBook(): LibraryBook? {
        val currentVolume = book.volumeNumber ?: return null
        return _seriesBooks.value.firstOrNull { (it.volumeNumber ?: 0) > currentVolume }
    }

    fun getPrevBook(): LibraryBook? {
        val currentVolume = book.volumeNumber ?: return null
        return _seriesBooks.value.lastOrNull { (it.volumeNumber ?: 0) < currentVolume }
    }

    private fun loadPages() {
        screenModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val strategy = readerStrategyFactory.create(book.format)
                val pages = strategy.getPages(book)
                
                val initialPage = book.lastReadLocator?.toIntOrNull() ?: 0
                
                _state.update { 
                    it.copy(
                        pages = pages, 
                        isLoading = false,
                        initialPage = initialPage.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun saveProgress(pageIndex: Int) {
        screenModelScope.launch {
            val progress = pageIndex.toDouble() / _state.value.pages.size.coerceAtLeast(1)
            val strategy = readerStrategyFactory.create(book.format)
            strategy.saveProgress(book.id, progress, pageIndex.toString())
        }
    }
}
