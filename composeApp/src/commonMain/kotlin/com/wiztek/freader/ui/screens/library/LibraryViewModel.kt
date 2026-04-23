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

    // Cache for all books to filter without re-collecting flow
    private var allBooks = listOf<LibraryBook>()
    
    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBooks().collect { books ->
                allBooks = books
                updateState()
            }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        updateState()
    }

    fun onSortOrderChange(order: String) {
        _state.update { it.copy(sortOrder = order) }
        updateState()
    }

    private fun updateState() {
        val filtered = allBooks.filter { 
            it.title.contains(_state.value.searchQuery, ignoreCase = true) || 
            it.author?.contains(_state.value.searchQuery, ignoreCase = true) ?: false
        }
        val sorted = applySort(filtered, _state.value.sortOrder)
        _state.update { it.copy(books = sorted, isLoading = false) }
    }

    private fun applySort(books: List<LibraryBook>, order: String): List<LibraryBook> {
        return books.sortedWith(
            when (order) {
                "Title" -> compareBy { it.title.lowercase() }
                "Author" -> compareBy({ it.author?.lowercase() }, { it.title.lowercase() })
                "Progress" -> compareByDescending<LibraryBook> { it.progress }.thenBy { it.title.lowercase() }
                "Recently Added" -> compareByDescending { it.addedAt }
                else -> compareByDescending { it.addedAt }
            }
        )
    }
}
