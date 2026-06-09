package com.wiztek.freader.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.navigation.VoyagerScreen
import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.ui.components.*

@Composable
fun HomeScreen(
    recentlyReadBooks: List<LibraryBook>,
    newlyAddedBooks: List<LibraryBook>,
    recentBooks: List<LibraryBook>,
    repository: LibraryRepository,
    collections: List<LibraryCollection>,
    onDeleteBooks: (Set<String>) -> Unit,
    onRenameBook: (String, String) -> Unit,
    onAddToCollection: (String, Set<String>) -> Unit,
    onCreateCollection: (String) -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<LibraryBook?>(null) }
    var showAddToCollectionDialog by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }

    val selectedBooks by remember {
        derivedStateOf { recentBooks.filter { it.id in selectedIds } }
    }

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

    fun onBookClick(book: LibraryBook) {
        if (selectionMode) toggleSelection(book.id)
        else navigator.push(VoyagerScreen.Reader(book))
    }

    fun onBookLongClick(book: LibraryBook) {
        if (!selectionMode) {
            selectionMode = true
            selectedIds = setOf(book.id)
        }
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
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = if (selectionMode) 72.dp else 16.dp)
        ) {
            item {
                HomeCarousel(
                    books = recentlyReadBooks,
                    title = "Recently Read",
                    onBookClick = { onBookClick(it) },
                    onBookLongClick = { onBookLongClick(it) },
                    onSeeAllClick = {
                        navigator.push(VoyagerScreen.RecentlyRead(recentlyReadBooks))
                    },
                    isSelectionMode = selectionMode,
                    isSelected = { it.id in selectedIds },
                    menuItems = { buildMenuItems(it) }
                )
            }

            item {
                ContinueReadingSection(
                    books = newlyAddedBooks,
                    onBookClick = { onBookClick(it) },
                    onBookLongClick = { onBookLongClick(it) },
                    isSelectionMode = selectionMode,
                    isSelected = { it.id in selectedIds },
                    menuItems = { buildMenuItems(it) },
                    title = "Newly Added",
                    onSeeAllClick = {
                        navigator.push(VoyagerScreen.Library)
                    }
                )
            }

            item {
                CategoriesSection(
                    categories = listOf(
                        CategoryChip("eBooks", BookFormat.EPUB, getFormatIcon(BookFormat.EPUB)),
                        CategoryChip("PDFs", BookFormat.PDF, getFormatIcon(BookFormat.PDF)),
                        CategoryChip("Comics", BookFormat.CBZ, getFormatIcon(BookFormat.CBZ)),
                        CategoryChip("Audiobooks")
                    ),
                    onCategoryClick = { chip ->
                        chip.format?.let { fmt ->
                            val filtered = recentBooks.filter { it.format == fmt }
                            if (filtered.isNotEmpty()) {
                                navigator.push(VoyagerScreen.BookGrid("${chip.label} (${filtered.size})", filtered))
                            }
                        }
                    }
                )
            }

            item {
                RecentBooksSection(
                    books = recentBooks,
                    onBookClick = { onBookClick(it) },
                    onBookLongClick = { onBookLongClick(it) },
                    isSelectionMode = selectionMode,
                    isSelected = { it.id in selectedIds },
                    menuItems = { buildMenuItems(it) }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
