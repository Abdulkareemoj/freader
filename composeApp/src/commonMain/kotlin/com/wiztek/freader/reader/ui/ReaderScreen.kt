package com.wiztek.freader.reader.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.wiztek.freader.di.LocalAppModule
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.engine.ReaderScreenModel
import com.wiztek.freader.reader.renderer.ReaderRenderer

data class ReaderScreen(val book: LibraryBook) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val appModule = LocalAppModule.current
        val repository = appModule?.libraryRepository ?: error("LibraryRepository not provided")
        
        val screenModel = ReaderScreenModel(
            book,
            repository = repository
        )
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.current
        var showUI by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                AnimatedVisibility(showUI, enter = slideInVertically(), exit = slideOutVertically()) {
                    TopAppBar(
                        title = { Text(book.title, maxLines = 1) },
                        navigationIcon = {
                            IconButton(onClick = { navigator?.pop() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    ReaderRenderer(
                        book = book,
                        pages = state.pages,
                        currentPage = state.currentPage,
                        onPageChanged = { screenModel.onPageChanged(it) }
                    )
                }
            }
        }
    }
}
