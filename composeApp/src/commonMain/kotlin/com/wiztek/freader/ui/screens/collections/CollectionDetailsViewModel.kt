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
    val isLoading: Boolean = false
)

class CollectionDetailsViewModel(
    private val repository: LibraryRepository,
    private val collectionId: String
) : ScreenModel {

    private val _state = MutableStateFlow(CollectionDetailsState())
    val state = _state.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getBooksForCollection(collectionId).collectLatest { books ->
                _state.update { it.copy(books = books, isLoading = false) }
            }
        }
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
