package com.wiztek.freader.reader.engine

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wiztek.freader.library.model.LibraryBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Publication

data class ReadiumReaderState(
    val publication: Publication? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ReadiumScreenModel(
    private val book: LibraryBook,
    private val readiumEngine: ReadiumEngine
) : ScreenModel {

    private val _state = MutableStateFlow(ReadiumReaderState())
    val state = _state.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        screenModelScope.launch {
            try {
                val publication = readiumEngine.openPublication(book)
                _state.value = ReadiumReaderState(publication = publication, isLoading = false)
            } catch (e: Exception) {
                _state.value = ReadiumReaderState(isLoading = false, error = e.message)
            }
        }
    }
}
