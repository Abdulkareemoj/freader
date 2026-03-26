package com.wiztek.freader.ui.screens.library

import com.wiztek.freader.library.model.LibraryBook

data class LibraryState(
    val books: List<LibraryBook> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val sortOrder: String = "Recently Added"
)
