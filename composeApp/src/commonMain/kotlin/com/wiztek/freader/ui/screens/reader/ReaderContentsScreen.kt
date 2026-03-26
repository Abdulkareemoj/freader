package com.wiztek.freader.ui.screens.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderContentsScreen(
    book: LibraryBook,
    onBack: () -> Unit,
    onChapterClick: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Chapters", "Bookmarks", "Notes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contents", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ChaptersList(onChapterClick)
                1 -> BookmarksList()
                2 -> NotesList()
            }
        }
    }
}

@Composable
fun ChaptersList(onChapterClick: (Int) -> Unit) {
    val chapters = listOf(
        "Chapter 1: The Beginning",
        "Chapter 2: Into the Wild",
        "Chapter 3: The Hidden Path",
        "Chapter 4: A New Hope",
        "Chapter 5: The Final Stand"
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(chapters) { index, chapter ->
            ListItem(
                headlineContent = { Text(chapter) },
                modifier = Modifier.clickable { onChapterClick(index) },
                supportingContent = { Text("Page ${index * 20 + 1}") }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
        }
    }
}

@Composable
fun BookmarksList() {
    val bookmarks = emptyList<String>() // Mock empty state
    
    if (bookmarks.isEmpty()) {
        EmptyState(
            title = "No bookmarks yet",
            message = "Add bookmarks to easily find your favorite parts later.",
            icon = Icons.Default.Bookmark
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(bookmarks) { _, bookmark ->
                ListItem(
                    headlineContent = { Text(bookmark) },
                    supportingContent = { Text("Added 2 days ago") },
                    leadingContent = { Icon(Icons.Default.Bookmark, null, tint = MaterialTheme.colorScheme.primary) }
                )
            }
        }
    }
}

@Composable
fun NotesList() {
    val notes = emptyList<String>() // Mock empty state
    
    if (notes.isEmpty()) {
        EmptyState(
            title = "No notes yet",
            message = "Highlight text and add notes to capture your thoughts.",
            icon = Icons.AutoMirrored.Filled.Notes
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(notes) { _, note ->
                ListItem(
                    headlineContent = { Text(note) },
                    supportingContent = { Text("The protagonist seems to be...") },
                    leadingContent = { Icon(Icons.AutoMirrored.Filled.Notes, null) }
                )
            }
        }
    }
}
