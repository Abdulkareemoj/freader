package com.wiztek.freader.ui.screens.collections

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

data class CollectionsState(
    val collections: List<LibraryCollection> = emptyList(),
    val isLoading: Boolean = false
)

class CollectionsViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(CollectionsState())
    val state = _state.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllCollections().collectLatest { collections ->
                _state.update { it.copy(collections = collections, isLoading = false) }
            }
        }
    }

    fun createCollection(name: String) {
        screenModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            val newCollection = LibraryCollection(
                id = now.toString(),
                name = name,
                createdAt = now
            )
            repository.insertCollection(newCollection)
        }
    }

    fun deleteCollection(id: String) {
        screenModelScope.launch {
            repository.deleteCollection(id)
        }
    }
}
