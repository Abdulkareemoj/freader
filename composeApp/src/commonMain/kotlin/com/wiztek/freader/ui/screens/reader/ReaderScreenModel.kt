package com.wiztek.freader.ui.screens.reader

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.Bookmark
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.ReaderStrategyFactory
import com.wiztek.freader.reader.EpubReaderStrategy
import com.wiztek.freader.reader.model.ReadiumManifest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ReaderState(
    val book: LibraryBook? = null,
    val manifest: ReadiumManifest? = null,
    val isLoading: Boolean = false,
    val bookmarks: List<Bookmark> = emptyList()
)

class ReaderScreenModel(
    private val repository: LibraryRepository,
    private val strategyFactory: ReaderStrategyFactory,
    private val bookId: String
) : ScreenModel {

    private val _state = MutableStateFlow(ReaderState())
    val state = _state.asStateFlow()

    init {
        loadBook()
        loadBookmarks()
    }

    private fun loadBook() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBooks().collect { books ->
                val book = books.find { it.id == bookId }
                if (book != null) {
                    val strategy = strategyFactory.create(book.format)
                    val manifest = if (strategy is EpubReaderStrategy) {
                        strategy.getManifest(book)
                    } else null
                    _state.update { it.copy(book = book, manifest = manifest, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun loadBookmarks() {
        screenModelScope.launch {
            repository.getBookmarksForBook(bookId).collect { bookmarks ->
                _state.update { it.copy(bookmarks = bookmarks) }
            }
        }
    }

    fun saveProgress(progress: Double, locator: String?) {
        screenModelScope.launch {
            repository.updateProgress(bookId, progress, locator)
        }
    }

    fun addBookmark(locator: String, label: String = "Bookmark") {
        screenModelScope.launch {
            val bookmark = Bookmark(
                id = kotlinx.datetime.Clock.System.now().toEpochMilliseconds().toString(),
                bookId = bookId,
                location = locator,
                label = label
            )
            repository.insertBookmark(bookmark)
        }
    }

    fun removeBookmark(bookmarkId: String) {
        screenModelScope.launch {
            repository.deleteBookmark(bookmarkId)
        }
    }
}
