package com.wiztek.freader.ui.screens.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.ui.components.*

enum class LibraryLayout { GRID, LIST }

@Composable
fun LibraryScreen(
    state: LibraryState,
    onImportClick: () -> Unit,
    onBookClick: (LibraryBook) -> Unit,
    onSortOrderChange: (String) -> Unit
) {
    var showSortSheet by remember { mutableStateOf(false) }
    var currentLayout by remember { mutableStateOf(LibraryLayout.GRID) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sort and filter header
        Surface(tonalElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.books.size} Books",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Layout Toggle
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

        AnimatedContent(
            targetState = currentLayout,
            transitionSpec = { fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300)) },
            label = "LayoutAnimation"
        ) { targetLayout ->
            if (state.books.isEmpty() && !state.isLoading) {
                EmptyLibrary(
                    title = "Your Library is Empty",
                    message = "Tap 'Import Books' to add your first ebook or comic.",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    actionLabel = "Import Books",
                    onActionClick = onImportClick
                )
            } else if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val columns = if (targetLayout == LibraryLayout.GRID) {
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
                                onClick = { onBookClick(book) },
                                modifier = if (targetLayout == LibraryLayout.LIST) Modifier.fillMaxWidth().height(120.dp) else Modifier
                            )
                        }
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
            }
        )
    }
}
