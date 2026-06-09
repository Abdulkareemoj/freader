package com.wiztek.freader.ui.screens.library

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.wiztek.freader.library.model.LibraryBook
import kotlinx.datetime.Clock

class LibraryViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private var allBooks = listOf<LibraryBook>()
    
    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    private val _collections = MutableStateFlow<List<LibraryCollection>>(emptyList())
    val collections = _collections.asStateFlow()

    init {
        loadBooks()
        loadCollections()
    }

    private fun loadBooks() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBooks().collect { books ->
                allBooks = books.filter { it.filePath.isNotBlank() }
                updateState()
            }
        }
    }

    private fun loadCollections() {
        screenModelScope.launch {
            repository.getAllCollections().collect { cols ->
                _collections.value = cols
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

    fun onFilterFormatChange(format: String) {
        _state.update { it.copy(filterFormat = format) }
        updateState()
    }

    private fun updateState() {
        var filtered = allBooks.filter { 
            it.title.contains(_state.value.searchQuery, ignoreCase = true) || 
            it.author?.contains(_state.value.searchQuery, ignoreCase = true) ?: false
        }
        
        if (_state.value.filterFormat != "All") {
            filtered = filtered.filter { it.format.name == _state.value.filterFormat }
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

    fun deleteBooks(ids: Set<String>) {
        screenModelScope.launch {
            ids.forEach { repository.deleteBook(it) }
        }
    }

    fun renameBook(id: String, newTitle: String) {
        screenModelScope.launch {
            repository.renameBook(id, newTitle)
        }
    }

    fun addToCollection(collectionId: String, bookIds: Set<String>) {
        screenModelScope.launch {
            bookIds.forEach { repository.addBookToCollection(collectionId, it) }
        }
    }

    fun createCollection(name: String) {
        screenModelScope.launch {
            val collection = LibraryCollection(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                name = name,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            repository.insertCollection(collection)
        }
    }
}
