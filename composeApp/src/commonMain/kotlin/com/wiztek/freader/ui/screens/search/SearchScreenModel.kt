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
        search()
    }

    private fun search() {
        val currentQuery = _query.value
        if (currentQuery.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        screenModelScope.launch {
            repository.getAllBooks().collectLatest { books ->
                _searchResults.value = books.filter { book ->
                    book.title.contains(currentQuery, ignoreCase = true) ||
                            (book.author?.contains(currentQuery, ignoreCase = true) == true) ||
                            (book.seriesName?.contains(currentQuery, ignoreCase = true) == true)
                }
            }
        }
    }
}
