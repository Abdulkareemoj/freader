package com.wiztek.freader.ui.screens.reader

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReaderState(
    val book: LibraryBook? = null,
    val isLoading: Boolean = false
)

class ReaderScreenModel(
    private val repository: LibraryRepository,
    private val bookId: String
) : ScreenModel {

    private val _state = MutableStateFlow(ReaderState())
    val state = _state.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // In a real app, we might want a flow for a single book, 
            // but for now we'll just find it in the list or add a getBookById to repo.
            repository.getAllBooks().collect { books ->
                val book = books.find { it.id == bookId }
                _state.update { it.copy(book = book, isLoading = false) }
            }
        }
    }

    fun saveProgress(progress: Double, locator: String?) {
        screenModelScope.launch {
            repository.updateProgress(bookId, progress, locator)
        }
    }
}
