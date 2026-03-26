package com.wiztek.freader.ui.screens.library

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.wiztek.freader.library.model.LibraryBook

class LibraryViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBooks().collect { books ->
                // Apply initial sorting when books are loaded
                val sortedBooks = applySort(books, _state.value.sortOrder)
                _state.update { it.copy(books = sortedBooks, isLoading = false) }
            }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        screenModelScope.launch {
            _state.update { it.copy(searchQuery = query) }
            // Re-filter books when search query changes
            repository.getAllBooks().take(1).collect { allBooks ->
                val filtered = allBooks.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.author?.contains(query, ignoreCase = true) ?: false
                }
                val sorted = applySort(filtered, _state.value.sortOrder)
                _state.update { it.copy(books = sorted) }
            }
        }
    }

    fun onSortOrderChange(order: String) {
        screenModelScope.launch {
            _state.update { it.copy(sortOrder = order) }
            // Re-sort books when sort order changes
            _state.update { currentState ->
                val sorted = applySort(currentState.books, order)
                LibraryState(
                    books = sorted,
                    isLoading = false,
                    searchQuery = currentState.searchQuery,
                    sortOrder = order
                )
            }
        }
    }

    private fun applySort(books: List<LibraryBook>, order: String): List<LibraryBook> {
        return books.sortedWith(
            when (order) {
                "Title" -> compareBy { it.title.lowercase() }
                "Author" -> compareBy({ it.author?.lowercase() }, { it.title.lowercase() })
                "Progress" -> compareByDescending<LibraryBook> { it.progress.toInt() }.thenBy { it.title.lowercase() }
                "Recently Added" -> compareByDescending { it.addedAt }
                else -> compareByDescending { it.addedAt }
            }
        )
    }
}
