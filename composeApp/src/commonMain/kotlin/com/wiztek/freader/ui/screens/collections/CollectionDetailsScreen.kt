package com.wiztek.freader.ui.screens.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.navigation.VoyagerScreen
import com.wiztek.freader.ui.components.BookCard
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

data class CollectionDetailsScreen(val collection: LibraryCollection) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<CollectionDetailsViewModel> { parametersOf(collection.id) }
        val state by viewModel.state.collectAsState()
        
        var bookToRemove by remember { mutableStateOf<com.wiztek.freader.library.model.LibraryBook?>(null) }
        var showDeleteCollectionConfirm by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(collection.name) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        var showMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete Collection") },
                                onClick = {
                                    showDeleteCollectionConfirm = true
                                    showMenu = false
                                },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.Delete, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    ) 
                                }
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (state.books.isEmpty() && !state.isLoading) {
                    Text(
                        text = "No books in this collection",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.books) { book ->
                            BookCard(
                                book = book,
                                onClick = { navigator.push(VoyagerScreen.BookDetails(book)) },
                                onLongClick = { 
                                    bookToRemove = book
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteCollectionConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteCollectionConfirm = false },
                title = { Text("Delete Collection") },
                text = { Text("Are you sure you want to delete '${collection.name}'? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteCollection()
                            navigator.pop()
                            showDeleteCollectionConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteCollectionConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        bookToRemove?.let { book ->
            AlertDialog(
                onDismissRequest = { bookToRemove = null },
                title = { Text("Remove from Collection") },
                text = { Text("Remove '${book.title}' from this collection?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.removeBookFromCollection(book.id)
                            bookToRemove = null
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookToRemove = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
