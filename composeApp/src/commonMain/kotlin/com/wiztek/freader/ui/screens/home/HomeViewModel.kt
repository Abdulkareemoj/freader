package com.wiztek.freader.ui.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBooks().collect { allBooks ->
                _state.update { 
                    it.copy(
                        trendingBooks = allBooks.take(5), 
                        recentBooks = allBooks,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    // Debug helper: Inject mock data if DB is empty
    fun addSampleData(books: List<com.wiztek.freader.library.model.LibraryBook>) {
        screenModelScope.launch {
            books.forEach { repository.insertBook(it) }
        }
    }
}
