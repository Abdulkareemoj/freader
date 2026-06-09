package com.wiztek.freader.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.ui.components.*

enum class LibraryLayout { GRID, LIST }

@Composable
fun LibraryScreen(
    state: LibraryState,
    collections: List<LibraryCollection>,
    onBookClick: (LibraryBook) -> Unit,
    onImportClick: () -> Unit,
    onSortOrderChange: (String) -> Unit,
    onFilterFormatChange: (String) -> Unit,
    onDeleteBooks: (Set<String>) -> Unit,
    onRenameBook: (String, String) -> Unit,
    onAddToCollection: (String, Set<String>) -> Unit,
    onCreateCollection: (String) -> Unit
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var currentLayout by remember { mutableStateOf(LibraryLayout.GRID) }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<LibraryBook?>(null) }
    var showAddToCollectionDialog by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }

    fun exitSelectionMode() {
        selectionMode = false
        selectedIds = emptySet()
    }

    fun toggleSelection(bookId: String) {
        selectedIds = if (bookId in selectedIds) selectedIds - bookId else selectedIds + bookId
    }

    fun buildMenuItems(book: LibraryBook): List<CardMenuAction> {
        if (selectionMode) return emptyList()
        return listOf(
            CardMenuAction("Add to Collection", Icons.Default.CollectionsBookmark) {
                selectedIds = setOf(book.id)
                showAddToCollectionDialog = true
            },
            CardMenuAction("Rename", Icons.Default.DriveFileRenameOutline) {
                renameTarget = book
            },
            CardMenuAction("Delete", Icons.Default.Delete) {
                selectedIds = setOf(book.id)
                showDeleteDialog = true
            }
        )
    }

    // Dialogs
    if (showDeleteDialog) {
        DeleteConfirmDialog(
            count = selectedIds.size,
            onConfirm = {
                onDeleteBooks(selectedIds)
                showDeleteDialog = false
                exitSelectionMode()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    renameTarget?.let { book ->
        RenameDialog(
            book = book,
            onConfirm = { newTitle ->
                onRenameBook(book.id, newTitle)
                renameTarget = null
            },
            onDismiss = { renameTarget = null }
        )
    }

    if (showAddToCollectionDialog) {
        AddToCollectionDialog(
            collections = collections,
            onSelectCollection = { collectionId ->
                onAddToCollection(collectionId, selectedIds)
                showAddToCollectionDialog = false
                exitSelectionMode()
            },
            onCreateCollection = {
                showAddToCollectionDialog = false
                showCreateCollectionDialog = true
            },
            onDismiss = {
                showAddToCollectionDialog = false
                if (selectedIds.size <= 1) exitSelectionMode()
            }
        )
    }

    if (showCreateCollectionDialog) {
        CreateCollectionDialog(
            onConfirm = { name ->
                onCreateCollection(name)
                showCreateCollectionDialog = false
            },
            onDismiss = { showCreateCollectionDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                    IconButton(onClick = { showAddToCollectionDialog = true }) {
                        Icon(Icons.Default.CollectionsBookmark, "Add to collection")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
            }
        } else {
            // Sort and filter header
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${state.books.size} Books",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconToggleButton(
                            checked = currentLayout == LibraryLayout.LIST,
                            onCheckedChange = { isList ->
                                currentLayout = if (isList) LibraryLayout.LIST else LibraryLayout.GRID
                            }
                        ) {
                            Icon(
                                imageVector = if (currentLayout == LibraryLayout.GRID) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                                contentDescription = "Toggle Layout"
                            )
                        }
                        TextButton(onClick = { showSortSheet = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(state.sortOrder)
                        }
                    }
                }
            }
        }

        if (state.books.isEmpty() && !state.isLoading) {
            Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                EmptyStateView(
                    title = "Your Library is Empty",
                    message = "Tap 'Import Books' to add your first ebook or comic.",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    actionLabel = "Import Books",
                    onActionClick = onImportClick
                )
            }
        } else if (state.isLoading) {
            Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val columns = if (currentLayout == LibraryLayout.GRID) {
                    when {
                        maxWidth < 600.dp -> 3
                        maxWidth < 900.dp -> 4
                        else -> 6
                    }
                } else 1

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.books) { book ->
                        BookCard(
                            book = book,
                            onClick = {
                                if (selectionMode) toggleSelection(book.id)
                                else onBookClick(book)
                            },
                            onLongClick = {
                                if (!selectionMode) {
                                    selectionMode = true
                                    selectedIds = setOf(book.id)
                                }
                            },
                            isSelectionMode = selectionMode,
                            isSelected = book.id in selectedIds,
                            menuItems = buildMenuItems(book),
                            modifier = if (currentLayout == LibraryLayout.LIST) Modifier.fillMaxWidth().height(120.dp) else Modifier
                        )
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        LibrarySortSheet(
            onDismiss = { showSortSheet = false },
            currentSort = state.sortOrder,
            onSortSelected = {
                onSortOrderChange(it)
                showSortSheet = false
            },
            currentFilter = state.filterFormat,
            onFilterSelected = { onFilterFormatChange(it) }
        )
    }
}
