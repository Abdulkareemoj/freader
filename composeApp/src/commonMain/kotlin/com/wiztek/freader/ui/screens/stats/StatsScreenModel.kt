package com.wiztek.freader.ui.screens.stats

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatsState(
    val booksRead: Int = 0,
    val readingTimeHours: Int = 0
)

class StatsScreenModel(
    private val repository: LibraryRepository
) : ScreenModel {

    val state: StateFlow<StatsState> = repository.getAllBooks()
        .map { books ->
            // Simple logic: books with progress > 0.8 are "read"
            val read = books.filter { it.progress >= 0.8 }.size
            // Simple logic: estimate 5 hours per book read
            val hours = read * 5 
            StatsState(booksRead = read, readingTimeHours = hours)
        }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), StatsState())
}
