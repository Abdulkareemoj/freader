package com.wiztek.freader.ui.screens.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.navigation.VoyagerScreen
import com.wiztek.freader.ui.components.BookCard
import com.wiztek.freader.ui.components.EmptyStateView
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

data class CollectionDetailsScreen(val collection: LibraryCollection) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<CollectionDetailsViewModel> { parametersOf(collection.id) }
        val state by viewModel.state.collectAsState()

        var bookToRemove by remember { mutableStateOf<LibraryBook?>(null) }
        var showDeleteCollectionConfirm by remember { mutableStateOf(false) }
        var showSortSheet by remember { mutableStateOf(false) }

        var selectionMode by remember { mutableStateOf(false) }
        var selectedIds by remember { mutableStateOf(setOf<String>()) }

        fun exitSelectionMode() {
            selectionMode = false
            selectedIds = emptySet()
        }

        fun toggleSelection(bookId: String) {
            selectedIds = if (bookId in selectedIds) selectedIds - bookId else selectedIds + bookId
        }

        Scaffold(
            topBar = {
                if (selectionMode) {
                    Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { exitSelectionMode() }) {
                                Icon(Icons.Default.Close, "Exit selection")
                            }
                            Text(
                                text = "${selectedIds.size} selected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                selectedIds.forEach { viewModel.removeBookFromCollection(it) }
                                exitSelectionMode()
                            }) {
                                Icon(Icons.Default.Delete, "Remove from collection")
                            }
                        }
                    }
                } else {
                    TopAppBar(
                        title = { Text(collection.name) },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            var showMenu by remember { mutableStateOf(false) }
                            TextButton(onClick = { showSortSheet = true }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(state.sortOrder, style = MaterialTheme.typography.labelLarge)
                            }
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
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (!selectionMode) {
                    Surface(tonalElevation = 2.dp) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${state.books.size} book${if (state.books.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.onSearchQueryChange(it) },
                                placeholder = { Text("Search in collection...") },
                                leadingIcon = { Icon(Icons.Default.Search, null) },
                                trailingIcon = {
                                    if (state.searchQuery.isNotBlank()) {
                                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                            Icon(Icons.Default.Close, "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        state.books.isEmpty() && state.searchQuery.isNotBlank() -> {
                            Column(
                                Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No results for \"${state.searchQuery}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        state.books.isEmpty() -> {
                            EmptyStateView(
                                title = "Collection is Empty",
                                message = "Add books to this collection from the Library or Discover tabs.",
                                icon = Icons.Default.Search
                            )
                        }
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(120.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.books, key = { it.id }) { book ->
                                    BookCard(
                                        book = book,
                                        onClick = {
                                            if (selectionMode) toggleSelection(book.id)
                                            else navigator.push(VoyagerScreen.BookDetails(book))
                                        },
                                        onLongClick = {
                                            if (!selectionMode) {
                                                selectionMode = true
                                                selectedIds = setOf(book.id)
                                            }
                                        },
                                        isSelectionMode = selectionMode,
                                        isSelected = book.id in selectedIds
                                    )
                                }
                            }
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

        if (showSortSheet) {
            CollectionSortSheet(
                onDismiss = { showSortSheet = false },
                currentSort = state.sortOrder,
                onSortSelected = {
                    viewModel.onSortOrderChange(it)
                    showSortSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionSortSheet(
    onDismiss: () -> Unit,
    currentSort: String,
    onSortSelected: (String) -> Unit
) {
    val sortOptions = listOf("Recently Added", "Title", "Author", "Progress")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                "Sort by",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
            sortOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == option,
                        onClick = { onSortSelected(option) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        option,
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
