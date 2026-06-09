package com.wiztek.freader.ui.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HomeViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _collections = MutableStateFlow<List<LibraryCollection>>(emptyList())
    val collections = _collections.asStateFlow()

    init {
        loadHomeData()
        loadCollections()
    }

    private fun loadHomeData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBooks().collect { allBooks ->
                val validBooks = allBooks.filter { it.filePath.isNotBlank() }
                _state.update {
                    it.copy(
                        recentlyReadBooks = validBooks
                            .filter { it.lastReadAt != null }
                            .sortedByDescending { it.lastReadAt },
                        newlyAddedBooks = validBooks
                            .sortedByDescending { it.addedAt }
                            .take(20),
                        recentBooks = validBooks,
                        isLoading = false
                    )
                }
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
