package com.wiztek.freader.ui.screens.collections

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionDetailsState(
    val books: List<LibraryBook> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val sortOrder: String = "Recently Added"
)

class CollectionDetailsViewModel(
    private val repository: LibraryRepository,
    private val collectionId: String
) : ScreenModel {

    private var allBooks = listOf<LibraryBook>()

    private val _state = MutableStateFlow(CollectionDetailsState())
    val state = _state.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getBooksForCollection(collectionId).collectLatest { books ->
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
        val filtered = if (_state.value.searchQuery.isBlank()) {
            allBooks
        } else {
            val q = _state.value.searchQuery.lowercase()
            allBooks.filter {
                it.title.lowercase().contains(q) ||
                it.author?.lowercase()?.contains(q) == true
            }
        }
        val sorted = applySort(filtered, _state.value.sortOrder)
        _state.update { it.copy(books = sorted, isLoading = false) }
    }

    private fun applySort(books: List<LibraryBook>, order: String): List<LibraryBook> {
        return books.sortedWith(
            when (order) {
                "Title" -> compareBy { it.title.lowercase() }
                "Author" -> compareBy<LibraryBook> { it.author?.lowercase() ?: "" }.thenBy { it.title.lowercase() }
                "Progress" -> compareByDescending<LibraryBook> { it.progress }.thenBy { it.title.lowercase() }
                "Recently Added" -> compareByDescending { it.addedAt }
                else -> compareByDescending { it.addedAt }
            }
        )
    }

    fun removeBookFromCollection(bookId: String) {
        screenModelScope.launch {
            repository.removeBookFromCollection(collectionId, bookId)
        }
    }

    fun deleteCollection() {
        screenModelScope.launch {
            repository.deleteCollection(collectionId)
        }
    }
}
