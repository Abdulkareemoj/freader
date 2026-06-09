package com.wiztek.freader.ui.screens.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.reader.model.BookFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchScreenModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LibraryBook>>(emptyList())
    val searchResults: StateFlow<List<LibraryBook>> = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null
    private var allBooks: List<LibraryBook> = emptyList()

    init {
        screenModelScope.launch {
            repository.getAllBooks().collect { books ->
                allBooks = books.filter { it.filePath.isNotBlank() }
                if (_query.value.isNotBlank()) performSearch(_query.value)
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        _isSearching.value = true
        searchJob = screenModelScope.launch {
            delay(300)
            performSearch(newQuery)
        }
    }

    fun searchByQuery(query: String) {
        _query.value = query
        addRecentSearch(query)
        searchJob?.cancel()
        performSearch(query)
    }

    fun searchByFormat(format: BookFormat?) {
        val filtered = if (format != null) allBooks.filter { it.format == format } else allBooks
        _searchResults.value = filtered
        _isSearching.value = false
    }

    fun clearSearch() {
        _query.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
        searchJob?.cancel()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun removeRecentSearch(query: String) {
        _recentSearches.value = _recentSearches.value - query
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        addRecentSearch(query)

        val results = allBooks.filter { book ->
            book.title.contains(query, ignoreCase = true) ||
                    (book.author?.contains(query, ignoreCase = true) == true) ||
                    (book.seriesName?.contains(query, ignoreCase = true) == true)
        }
        _searchResults.value = results
        _isSearching.value = false
    }

    private fun addRecentSearch(query: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        current.add(0, query)
        _recentSearches.value = current.take(10)
    }
}
