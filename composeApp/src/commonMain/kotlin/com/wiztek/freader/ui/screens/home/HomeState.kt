package com.wiztek.freader.ui.screens.home

import com.wiztek.freader.library.model.LibraryBook

data class HomeState(
    val trendingBooks: List<LibraryBook> = emptyList(),
    val recentBooks: List<LibraryBook> = emptyList(),
    val isLoading: Boolean = false
)
