package com.wiztek.freader.ui.screens.home

import com.wiztek.freader.library.model.LibraryBook

data class HomeState(
    val recentlyReadBooks: List<LibraryBook> = emptyList(),
    val newlyAddedBooks: List<LibraryBook> = emptyList(),
    val recentBooks: List<LibraryBook> = emptyList(),
    val isLoading: Boolean = false
)
