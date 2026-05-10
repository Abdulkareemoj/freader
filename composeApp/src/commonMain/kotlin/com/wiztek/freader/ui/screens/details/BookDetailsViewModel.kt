package com.wiztek.freader.ui.screens.details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailsState(
    val collections: List<LibraryCollection> = emptyList(),
    val isLoading: Boolean = false
)

class BookDetailsViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(BookDetailsState())
    val state = _state.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        screenModelScope.launch {
            repository.getAllCollections().collectLatest { collections ->
                _state.update { it.copy(collections = collections) }
            }
        }
    }

    fun addBookToCollection(bookId: String, collectionId: String) {
        screenModelScope.launch {
            repository.addBookToCollection(collectionId, bookId)
        }
    }
}
