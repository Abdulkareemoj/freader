package com.wiztek.freader.ui.screens.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchScreenModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LibraryBook>>(emptyList())
    val searchResults: StateFlow<List<LibraryBook>> = _searchResults.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        screenModelScope.launch {
            repository.getAllBooks().take(1).collect { books ->
                _searchResults.value = books.filter { book ->
                    book.title.contains(newQuery, ignoreCase = true) ||
                            (book.author?.contains(newQuery, ignoreCase = true) == true)
                }
            }
        }
    }
}
