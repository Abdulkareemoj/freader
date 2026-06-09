package com.wiztek.freader.ui.screens.collections

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class CollectionWithInfo(
    val collection: LibraryCollection,
    val bookCount: Int = 0,
    val firstBooks: List<LibraryBook> = emptyList()
)

data class CollectionsState(
    val collections: List<CollectionWithInfo> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: Int = 0
)

class CollectionsViewModel(
    private val repository: LibraryRepository
) : ScreenModel {

    private val _state = MutableStateFlow(CollectionsState())
    val state = _state.asStateFlow()

    private var bookCountJobs = mutableMapOf<String, kotlinx.coroutines.Job>()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllCollections().collectLatest { collections ->
                val infos = collections.map { col ->
                    val count = repository.getBookCountForCollection(col.id).first()
                    val books = repository.getBooksForCollection(col.id).first()
                    CollectionWithInfo(
                        collection = col,
                        bookCount = count.toInt(),
                        firstBooks = books.take(4)
                    )
                }
                _state.update { it.copy(collections = infos, isLoading = false) }

                collections.forEach { col ->
                    launch {
                        repository.getBookCountForCollection(col.id).collectLatest { count ->
                            _state.update { state ->
                                val updated = state.collections.map {
                                    if (it.collection.id == col.id) it.copy(bookCount = count.toInt())
                                    else it
                                }
                                state.copy(collections = updated)
                            }
                        }
                    }
                    launch {
                        repository.getBooksForCollection(col.id).collectLatest { books ->
                            _state.update { state ->
                                val updated = state.collections.map {
                                    if (it.collection.id == col.id) it.copy(firstBooks = books.take(4))
                                    else it
                                }
                                state.copy(collections = updated)
                            }
                        }
                    }
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun createCollection(name: String) {
        screenModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            repository.insertCollection(
                LibraryCollection(id = now.toString(), name = name, createdAt = now)
            )
        }
    }

    fun deleteCollection(id: String) {
        screenModelScope.launch {
            repository.deleteCollection(id)
        }
    }

    fun renameCollection(id: String, newName: String) {
        screenModelScope.launch {
            repository.renameCollection(id, newName)
        }
    }
}
