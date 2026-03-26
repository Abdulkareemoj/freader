package com.wiztek.freader.reader.engine

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.ReaderStrategyFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReaderState(
    val pages: List<String> = emptyList(),
    val currentPage: Int = 0,
    val isLoading: Boolean = true
)

class ReaderScreenModel(
    private val book: LibraryBook,
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(ReaderState())
    val state = _state.asStateFlow()

    private val strategy = ReaderStrategyFactory(repository).create(book.format)

    init {
        loadBook()
    }

    private fun loadBook() {
        screenModelScope.launch {
            val pages = strategy.getPages(book)
            _state.value = ReaderState(
                pages = pages, 
                currentPage = book.progress.toInt(), // Restore progress
                isLoading = false
            )
        }
    }

    fun onPageChanged(index: Int) {
        _state.value = _state.value.copy(currentPage = index)
        screenModelScope.launch {
            strategy.saveProgress(book.id, index)
        }
    }
}
